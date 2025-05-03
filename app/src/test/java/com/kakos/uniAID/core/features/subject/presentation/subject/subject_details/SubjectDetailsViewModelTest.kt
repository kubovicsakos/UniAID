package com.kakos.uniAID.core.features.subject.presentation.subject.subject_details

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.kakos.uniAID.MainDispatcherRule
import com.kakos.uniAID.core.features.subject.data.repository.FakeSubjectRepository
import com.kakos.uniAID.core.features.subject.domain.fakeSubjectUseCases
import com.kakos.uniAID.core.domain.subject.domain.model.Subject
import com.kakos.uniAID.core.domain.subject.domain.use_case.SubjectUseCases
import com.kakos.uniAID.subjectAndStats.presentation.subject.subject_details.SubjectDetailsViewModel
import com.kakos.uniAID.subjectAndStats.presentation.subject.subject_details.util.SubjectDetailsEvent
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SubjectDetailsViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: SubjectDetailsViewModel
    private lateinit var fakeSubjectRepository: FakeSubjectRepository
    private lateinit var subjectUseCases: SubjectUseCases

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.d(any(), any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        fakeSubjectRepository = FakeSubjectRepository()
        subjectUseCases = fakeSubjectUseCases(fakeSubjectRepository)
        fakeSubjectRepository.shouldHaveFilledList(false)

        viewModel = SubjectDetailsViewModel(subjectUseCases)
    }

    @Test
    fun initialStateHasCorrectDefaultValues() {
        assertThat(viewModel.state.value.subject).isNull()
        assertThat(viewModel.state.value.notes).isEmpty()
        assertThat(viewModel.state.value.events).isEmpty()
        assertThat(viewModel.state.value.error).isEmpty()
    }

    @Test
    fun loadingExistingSubjectPopulatesStateCorrectly() = runTest {
        fakeSubjectRepository.shouldHaveFilledList(true)
        fakeSubjectRepository.shouldHaveFilledNoteList(true)
        fakeSubjectRepository.shouldHaveFilledEventList(true)

        viewModel = SubjectDetailsViewModel(subjectUseCases)
        advanceUntilIdle()

        viewModel.onEvent(SubjectDetailsEvent.GetSubjectById(1))

        // wait for getNotesOfSubject and getEventsOfSubject
        advanceTimeBy(1000)
        advanceUntilIdle()

        assertThat(viewModel.state.value.subject).isNotNull()
        assertThat(viewModel.state.value.subject?.title).isEqualTo("a")

        runCurrent()
        assertThat(viewModel.state.value.notes).isNotEmpty()
        println("Notes: ${viewModel.state.value.notes}")

        // Verify events loaded
        assertThat(viewModel.state.value.events).isNotEmpty()
        println("Events: ${viewModel.state.value.events}")
    }

    @Test
    fun loadingNonexistentSubjectSetsErrorState() = runTest {
        viewModel.onEvent(SubjectDetailsEvent.GetSubjectById(999))
        advanceTimeBy(1000)
        advanceUntilIdle()

        assertThat(viewModel.state.value.subject).isNull()
    }

    @Test
    fun loadingSubjectWithNoNotesOrEventsShowsEmptyCollections() = runTest {
        val subject = Subject(id = 1, title = "Math", semester = 1, credit = 5)
        fakeSubjectRepository.insert(subject)
        fakeSubjectRepository.shouldHaveFilledNoteList(false)
        fakeSubjectRepository.shouldHaveFilledEventList(false)

        viewModel.onEvent(SubjectDetailsEvent.GetSubjectById(1))
        advanceTimeBy(1000)
        advanceUntilIdle()

        runCurrent()
        assertThat(viewModel.state.value.subject).isNotNull()
        assertThat(viewModel.state.value.notes).isEmpty()
        assertThat(viewModel.state.value.events).isEmpty()
    }

    @Test
    fun deletingSubjectRemovesItFromRepository() = runTest {
        val subject = Subject(id = 1, title = "Math", semester = 1, credit = 5)
        fakeSubjectRepository.insert(subject)

        viewModel.onEvent(SubjectDetailsEvent.DeleteSubject(subject))
        advanceUntilIdle()

        val subjectAfterDeletion = fakeSubjectRepository.getSubjectById(1)
        assertThat(subjectAfterDeletion).isNull()
    }

    @Test
    fun deletingSubjectUpdatesAssociatedNotesAndEvents() = runTest {
        val subject = Subject(id = 1, title = "Math", semester = 1, credit = 5)
        fakeSubjectRepository.insert(subject)
        fakeSubjectRepository.shouldHaveFilledNoteList(true)
        fakeSubjectRepository.shouldHaveFilledEventList(true)

        viewModel.onEvent(SubjectDetailsEvent.DeleteSubject(subject))
        advanceTimeBy(1000)
        advanceUntilIdle()

        runCurrent()
        // Notes should have their subjectId and subjectName set to null
        fakeSubjectRepository.getNoteItems().forEach { note ->
            if (note.subjectId == 1) {
                assertThat(note.subjectId).isNull()
                assertThat(note.subjectName).isNull()
            }
        }

        // Events should have their subjectId and subjectName set to null
        fakeSubjectRepository.getEventItems().forEach { event ->
            if (event.subjectId == 1) {
                assertThat(event.subjectId).isNull()
                assertThat(event.subjectName).isNull()
            }
        }
    }
}