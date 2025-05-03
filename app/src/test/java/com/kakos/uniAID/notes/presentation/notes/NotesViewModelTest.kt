package com.kakos.uniAID.notes.presentation.notes

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.kakos.uniAID.MainDispatcherRule
import com.kakos.uniAID.notes.data.repository.FakeNoteRepository
import com.kakos.uniAID.notes.domain.use_case.NoteUseCases
import com.kakos.uniAID.notes.domain.use_case.fakeNoteUseCases
import com.kakos.uniAID.notes.domain.util.NoteOrder
import com.kakos.uniAID.notes.domain.util.OrderType
import com.kakos.uniAID.notes.presentation.notes.util.NotesEvent
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NotesViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeNoteRepository: FakeNoteRepository
    private lateinit var noteUseCases: NoteUseCases
    private lateinit var notesViewModel: NotesViewModel

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        fakeNoteRepository = FakeNoteRepository()
        noteUseCases = fakeNoteUseCases(fakeNoteRepository)
        fakeNoteRepository.shouldHaveFilledList(true)
    }

    @Test
    fun `initial state has correct default values with notes not empty, notes ordered by date descending with order section hidden with not empty notes`() =
        runTest {
            // Advance the test dispatcher to complete any pending coroutines
            notesViewModel = NotesViewModel(noteUseCases)
            advanceUntilIdle()

            notesViewModel.state.value.apply {
                println("State value: $this")
                assertThat(noteOrder).isInstanceOf(NoteOrder.Date::class.java)
                assertThat((noteOrder as NoteOrder.Date).orderType).isEqualTo(OrderType.Descending)
                assertThat(isOrderSectionVisible).isFalse()
                assertThat(notes.isEmpty()).isFalse()

            }
        }

    @Test
    fun `toggling order section, order section is visible and hidden again`() = runTest {
        notesViewModel = NotesViewModel(noteUseCases)
        advanceUntilIdle()

        // Initially order section should be hidden
        assertThat(notesViewModel.state.value.isOrderSectionVisible).isFalse()

        // Toggle order section
        notesViewModel.onEvent(NotesEvent.ToggleOrderSection)
        advanceUntilIdle()

        // Order section should now be visible
        println("State value: ${notesViewModel.state.value}")
        assertThat(notesViewModel.state.value.isOrderSectionVisible).isTrue()

        // Toggle again
        notesViewModel.onEvent(NotesEvent.ToggleOrderSection)
        advanceUntilIdle()

        // Order section should be hidden again
        println("State value: ${notesViewModel.state.value}")
        assertThat(notesViewModel.state.value.isOrderSectionVisible).isFalse()
    }

    @Test
    fun `changing note order updates state and retrieves reordered notes, ordering changed to title ascending`() =
        runTest {
            notesViewModel = NotesViewModel(noteUseCases)
            advanceUntilIdle()

            // Change order to Title Ascending (default is Date Descending)
            notesViewModel.onEvent(NotesEvent.Order(NoteOrder.Title(OrderType.Ascending)))
            advanceUntilIdle()

            // State should be updated - check type and orderType
            val stateOrder = notesViewModel.state.value.noteOrder
            assertThat(stateOrder).isInstanceOf(NoteOrder.Title::class.java)
            assertThat((stateOrder as NoteOrder.Title).orderType).isEqualTo(OrderType.Ascending)

            // Notes should be ordered by title ascending
            val notes = notesViewModel.state.value.notes
            for (i in 0 until notes.size - 1) {
                assertThat(notes[i].title).isLessThan(notes[i + 1].title)
            }
        }

    @Test
    fun `deleting note removes it from state, first note (with id = 1) deleted`() = runTest {
        notesViewModel = NotesViewModel(noteUseCases)
        advanceUntilIdle()

        // Get initial notes and count
        val initialNotes = notesViewModel.state.value.notes
        println("Initial notes: $initialNotes")
        val initialCount = initialNotes.size
        val noteToDelete = initialNotes[0]
        println("Note to delete: $noteToDelete")

        // Delete a note
        notesViewModel.onEvent(NotesEvent.DeleteNote(noteToDelete))
        advanceUntilIdle()

        // Verify it's removed from the state
        val updatedNotes = notesViewModel.state.value.notes
        println("Updated notes: $updatedNotes")
        assertThat(updatedNotes.size).isEqualTo(initialCount - 1)
        assertThat(updatedNotes).doesNotContain(noteToDelete)
    }

    @Test
    fun `restoring note adds it back to state, final state is equal to original`() = runTest {
        notesViewModel = NotesViewModel(noteUseCases)
        advanceUntilIdle()

        // Get initial notes and count
        val initialNotes = notesViewModel.state.value.notes
        println("Initial notes: $initialNotes")
        val initialCount = initialNotes.size
        val noteToDelete = initialNotes[0]
        println("Note to delete: $noteToDelete")

        // Delete a note
        notesViewModel.onEvent(NotesEvent.DeleteNote(noteToDelete))
        advanceUntilIdle()

        // Verify it's removed from the state
        val updatedNotes = notesViewModel.state.value.notes
        println("Updated notes: $updatedNotes")
        assertThat(updatedNotes.size).isEqualTo(initialCount - 1)
        assertThat(updatedNotes).doesNotContain(noteToDelete)

        // Restore the note
        notesViewModel.onEvent(NotesEvent.RestoreNote)
        advanceUntilIdle()

        // Verify it's added back to the state
        val restoredNotes = notesViewModel.state.value.notes
        println("Restored notes: $restoredNotes")
        assertThat(restoredNotes.size).isEqualTo(initialCount)
        assertThat(restoredNotes).contains(noteToDelete)
        assertThat(restoredNotes).isEqualTo(initialNotes)
    }

    @Test
    fun `initial state has correct default values with empty notes, notes ordered by date descending with order section hidden with empty notes`() =
        runTest {
            fakeNoteRepository.shouldHaveFilledList(false)
            notesViewModel = NotesViewModel(noteUseCases)
            // Advance the test dispatcher to complete any pending coroutines
            advanceUntilIdle()

            notesViewModel.state.value.apply {
                println("State value: $this")
                assertThat(noteOrder).isInstanceOf(NoteOrder.Date::class.java)
                assertThat((noteOrder as NoteOrder.Date).orderType).isEqualTo(OrderType.Descending)
                assertThat(isOrderSectionVisible).isFalse()
                assertThat(notes.isEmpty()).isTrue()

            }
        }

    @Test
    fun `changing note order updates state and retrieves reordered notes with empty notes, ordering changed to date ascending with empty notes`() =
        runTest {
            fakeNoteRepository.shouldHaveFilledList(false)
            notesViewModel = NotesViewModel(noteUseCases)
            advanceUntilIdle()

            // Change order to Title Ascending (default is Date Descending)
            notesViewModel.onEvent(NotesEvent.Order(NoteOrder.Title(OrderType.Ascending)))
            advanceUntilIdle()

            // State should be updated - check type and orderType
            val stateOrder = notesViewModel.state.value.noteOrder
            assertThat(stateOrder).isInstanceOf(NoteOrder.Title::class.java)
            assertThat((stateOrder as NoteOrder.Title).orderType).isEqualTo(OrderType.Ascending)

            assertThat(notesViewModel.state.value.notes.isEmpty()).isTrue()
        }
}