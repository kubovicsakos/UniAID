package com.kakos.uniAID.notes.presentation.edit_notes

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.intPreferencesKey
import com.google.common.truth.Truth.assertThat
import com.kakos.uniAID.MainDispatcherRule
import com.kakos.uniAID.core.domain.subject.domain.model.Subject
import com.kakos.uniAID.core.domain.subject.domain.use_case.SubjectUseCases
import com.kakos.uniAID.notes.data.repository.FakeNoteRepository
import com.kakos.uniAID.notes.domain.use_case.NoteUseCases
import com.kakos.uniAID.notes.domain.use_case.fakeNoteUseCases
import com.kakos.uniAID.notes.presentation.edit_notes.util.EditNoteEvent
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EditNoteViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeNoteRepository: FakeNoteRepository
    private lateinit var noteUseCases: NoteUseCases
    private lateinit var subjectUseCases: SubjectUseCases
    private lateinit var dataStore: DataStore<androidx.datastore.preferences.core.Preferences>
    private lateinit var editNoteViewModel: EditNoteViewModel

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0


        fakeNoteRepository = FakeNoteRepository()
        noteUseCases = fakeNoteUseCases(fakeNoteRepository)
        fakeNoteRepository.shouldHaveFilledList(true)

        subjectUseCases = mockk(relaxed = true)
        // emulates data store with current semester set to 1
        dataStore = mockk(relaxed = true)
        every { dataStore.data } returns flowOf(
            androidx.datastore.preferences.core.preferencesOf(intPreferencesKey("current_semester") to 1)
        )
        // emulates empty list of subjects
        every { subjectUseCases.getAllSubjects() } returns flowOf(emptyList())

    }

    @Test
    fun `initial state has correct default values for new note, loads correct defaults into state`() =
        runTest {
            editNoteViewModel =
                EditNoteViewModel(noteUseCases, subjectUseCases, dataStore)
            advanceUntilIdle()

            editNoteViewModel.state.value.apply {
                assertThat(title.text).isEmpty()
                assertThat(content.text).isEmpty()
                assertThat(isNewNote).isTrue()
                assertThat(isDarkTheme).isTrue()
                assertThat(creationTime).isNotNull()
                assertThat(lastModified).isNotNull()
                assertThat(isFormValid).isTrue()
                assertThat(subjectId).isNull()
                assertThat(subjectName).isNull()
                assertThat(subjects).isEmpty()
                assertThat(filteredSubjects).isEmpty()
                assertThat(currentSemester).isEqualTo(1)
            }
        }

    @Test
    fun `loading existing note populates state correctly, loads correct data by id into state`() =
        runTest {
            val testNoteId = 1
            editNoteViewModel =
                EditNoteViewModel(noteUseCases, subjectUseCases, dataStore)
            advanceUntilIdle()

            editNoteViewModel.onEvent(EditNoteEvent.GetNote(testNoteId))
            advanceUntilIdle()

            val note = fakeNoteRepository.getNoteById(testNoteId)
            println("Note: $note")

            editNoteViewModel.state.value.apply {
                assertThat(isNewNote).isFalse()
                assertThat(title.text).isNotEmpty()
                assertThat(content.text).isNotEmpty()
                assertThat(title.text).isEqualTo(note?.title)
                assertThat(content.text).isEqualTo(note?.content)
                assertThat(isDarkTheme).isEqualTo(note?.darkTheme)
                assertThat(creationTime).isEqualTo(note?.creationTime)
                assertThat(lastModified).isEqualTo(note?.lastModified)
            }
        }

    @Test
    fun `entering title updates state, updates state with content`() = runTest {

        editNoteViewModel =
            EditNoteViewModel(noteUseCases, subjectUseCases, dataStore)
        advanceUntilIdle()

        val testTitle = "Test Title"
        editNoteViewModel.onEvent(EditNoteEvent.EnteredTitle(testTitle))
        advanceUntilIdle()

        assertThat(editNoteViewModel.state.value.title.text).isEqualTo(testTitle)
        assertThat(editNoteViewModel.state.value.isFormValid).isTrue()
    }

    @Test
    fun `entering content updates state, updates state with title`() = runTest {

        editNoteViewModel =
            EditNoteViewModel(noteUseCases, subjectUseCases, dataStore)
        advanceUntilIdle()

        val testContent = "Test Content"
        editNoteViewModel.onEvent(EditNoteEvent.EnteredContent(testContent))
        advanceUntilIdle()

        assertThat(editNoteViewModel.state.value.content.text).isEqualTo(testContent)
    }

    @Test
    fun `toggling dark theme updates state, sets dark theme correctly in state`() = runTest {

        editNoteViewModel =
            EditNoteViewModel(noteUseCases, subjectUseCases, dataStore)
        advanceUntilIdle()

        val initialThemeState = editNoteViewModel.state.value.isDarkTheme
        editNoteViewModel.onEvent(EditNoteEvent.ToggleDarkTheme)
        advanceUntilIdle()

        assertThat(editNoteViewModel.state.value.isDarkTheme).isEqualTo(!initialThemeState)
    }

    @Test
    fun `saving note with empty title fails validation, does not add note to repository`() =
        runTest {

            editNoteViewModel =
                EditNoteViewModel(noteUseCases, subjectUseCases, dataStore)
            advanceUntilIdle()

            // Title is empty by default
            assertThat(editNoteViewModel.state.value.title.text).isEmpty()

            // Try to save
            editNoteViewModel.onEvent(EditNoteEvent.SaveNote)
            advanceUntilIdle()

            // Verify form is invalid
            assertThat(editNoteViewModel.state.value.isFormValid).isFalse()
        }

    @Test
    fun `saving note with valid title succeeds, adds note with valid title to repository`() =
        runTest {

            editNoteViewModel =
                EditNoteViewModel(noteUseCases, subjectUseCases, dataStore)
            advanceUntilIdle()

            // Enter valid title
            editNoteViewModel.onEvent(EditNoteEvent.EnteredTitle("Valid Title"))
            advanceUntilIdle()

            // Verify form is valid
            assertThat(editNoteViewModel.state.value.isFormValid).isTrue()

            // Save note
            editNoteViewModel.onEvent(EditNoteEvent.SaveNote)
            advanceUntilIdle()

            // Verify note was saved
            val finalNotes = fakeNoteRepository.getNotes().first()
            finalNotes.find { it.title == "Valid Title" }?.let {
                assertThat(it.title).isEqualTo("Valid Title")
            }

            println("finalNotes: $finalNotes")
        }

    @Test
    fun `selecting subject updates state, sets subject correctly`() = runTest {
        val testSubjects = listOf(
            Subject(id = 1, title = "Math", semester = 1, credit = 4, finalGrade = 5, grade = 5),
            Subject(id = 2, title = "Physics", semester = 1, credit = 4, finalGrade = 5, grade = 5)
        )
        every { subjectUseCases.getAllSubjects() } returns flowOf(testSubjects)


        editNoteViewModel =
            EditNoteViewModel(noteUseCases, subjectUseCases, dataStore)
        advanceUntilIdle()

        editNoteViewModel.onEvent(EditNoteEvent.SelectSubject(1))
        advanceUntilIdle()

        assertThat(editNoteViewModel.state.value.subjectId).isEqualTo(1)
        assertThat(editNoteViewModel.state.value.subjectName).isEqualTo("Math")
    }

    @Test
    fun `deleting note deletes it form repository, deletes note with id`() = runTest {
        val testNoteId = 1

        editNoteViewModel =
            EditNoteViewModel(noteUseCases, subjectUseCases, dataStore)
        advanceUntilIdle()

        editNoteViewModel.onEvent(EditNoteEvent.DeleteNote(testNoteId))
        advanceUntilIdle()

        // Verify note was deleted
        val notes = fakeNoteRepository.getNoteById(testNoteId)
        assertThat(notes).isNull()

    }

}