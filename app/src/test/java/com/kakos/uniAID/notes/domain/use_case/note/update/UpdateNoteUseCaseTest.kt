package com.kakos.uniAID.notes.domain.use_case.note.update

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.kakos.uniAID.notes.data.repository.FakeNoteRepository
import com.kakos.uniAID.notes.domain.model.InvalidNoteException
import com.kakos.uniAID.notes.domain.model.Note
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

class UpdateNoteUseCaseTest {

    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var fakeNoteRepository: FakeNoteRepository
    private lateinit var updateNoteUseCase: UpdateNoteUseCase

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        fakeNoteRepository = FakeNoteRepository()
        updateNoteUseCase = UpdateNoteUseCase(fakeNoteRepository)
        fakeNoteRepository.shouldHaveFilledList(true)
    }


    @Test
    fun `updating existing note with valid data succeeds, note is updated`() = runTest {
        val initialNotes = fakeNoteRepository.getNotes().first()
        println("Initial notes: $initialNotes")
        val noteToUpdate = initialNotes[0].copy(
            title = "Updated Title",
            content = "Updated Content"
        )

        updateNoteUseCase(noteToUpdate)

        val updatedNotes = fakeNoteRepository.getNotes().first()
        println("Updated notes: $updatedNotes")
        val updatedNote = updatedNotes.find { it.id == noteToUpdate.id }
        println("Updated note: $updatedNote")

        assertThat(updatedNote).isNotNull()
        assertThat(updatedNote?.title).isEqualTo("Updated Title")
        assertThat(updatedNote?.content).isEqualTo("Updated Content")
    }

    @Test
    fun `update every note in the repository, all notes are updated`() = runTest {
        val initialNotes = fakeNoteRepository.getNotes().first()
        println("Initial notes: $initialNotes")

        for (note in initialNotes) {
            val updatedNote = note.copy(
                title = "Updated Title",
                content = "Updated Content"
            )
            updateNoteUseCase(updatedNote)
        }

        val updatedNotes = fakeNoteRepository.getNotes().first()
        println("Updated notes: $updatedNotes")

        for (note in updatedNotes) {
            assertThat(note.title).isEqualTo("Updated Title")
            assertThat(note.content).isEqualTo("Updated Content")
        }
    }

    @Test
    fun `updating non-existent note id not updates anything, repository not updated`() = runTest {
        val noID = 999
        val now = LocalDateTime.now()
        val noIdNote = Note(
            id = noID,
            title = "Non-existent ID",
            content = "Content for non-existent ID",
            creationTime = now,
            lastModified = now,
            darkTheme = true
        )

        val initialNotes = fakeNoteRepository.getNotes().first()
        println("Initial notes: $initialNotes")

        updateNoteUseCase(noIdNote)

        val updatedNotes = fakeNoteRepository.getNotes().first()
        println("Updated notes: $updatedNotes")
        val updatedNote = updatedNotes.find { it.id == noID }

        assertThat(updatedNotes).isEqualTo(initialNotes)
        assertThat(updatedNotes.size).isEqualTo(initialNotes.size)
        assertThat(updatedNote).isNull()
    }

    @Test
    fun `updating note with empty title throws exception, InvalidNoteException`() = runTest {
        val initialNotes = fakeNoteRepository.getNotes().first()
        println("Initial notes: $initialNotes")
        val noteToUpdate = initialNotes[0].copy(title = "")

        var exceptionThrown = false
        try {
            updateNoteUseCase(noteToUpdate)
        } catch (e: InvalidNoteException) {
            exceptionThrown = true
            println("Exception thrown: ${e.message}")
        }

        assertThat(exceptionThrown).isTrue()
    }

    @Test
    fun `updating note with null id throws exception, IllegalArgumentException`() = runTest {
        val initialNotes = fakeNoteRepository.getNotes().first()
        println("Initial notes: $initialNotes")
        val noteToUpdate = initialNotes[0].copy(id = null)

        var exceptionThrown = false
        try {
            updateNoteUseCase(noteToUpdate)
        } catch (e: IllegalArgumentException) {
            exceptionThrown = true
            println("Exception thrown: ${e.message}")
        }

        assertThat(exceptionThrown).isTrue()
    }

    @Test
    fun `updating note with zero id throws exception, IllegalArgumentException`() = runTest {
        val initialNotes = fakeNoteRepository.getNotes().first()
        println("Initial notes: $initialNotes")
        val noteToUpdate = initialNotes[0].copy(id = 0)

        var exceptionThrown = false
        try {
            updateNoteUseCase(noteToUpdate)
        } catch (e: IllegalArgumentException) {
            exceptionThrown = true
            println("Exception thrown: ${e.message}")
        }

        assertThat(exceptionThrown).isTrue()
    }

    @Test
    fun `updating note with negative id throws exception, IllegalArgumentException`() = runTest {
        val initialNotes = fakeNoteRepository.getNotes().first()
        println("Initial notes: $initialNotes")
        val noteToUpdate = initialNotes[0].copy(id = -5)

        var exceptionThrown = false
        try {
            updateNoteUseCase(noteToUpdate)
        } catch (e: IllegalArgumentException) {
            exceptionThrown = true
            println("Exception thrown: ${e.message}")
        }

        assertThat(exceptionThrown).isTrue()
    }

    @Test
    fun `updating note with very long title succeeds, no InvalidNoteException`() = runTest {
        val initialNotes = fakeNoteRepository.getNotes().first()
        println("Initial notes: $initialNotes")
        val longTitle = "a".repeat(1000)
        val noteToUpdate = initialNotes[0].copy(title = longTitle)

        var exceptionThrown = false
        try {
            updateNoteUseCase(noteToUpdate)
        } catch (e: InvalidNoteException) {
            exceptionThrown = true
            println("Exception thrown: ${e.message}")
        }

        assertThat(exceptionThrown).isFalse()

        val updatedNotes = fakeNoteRepository.getNotes().first()
        val updatedNote = updatedNotes.find { it.id == noteToUpdate.id }
        println("Updated note: $updatedNote")
        assertThat(updatedNote?.title).isEqualTo(longTitle)
    }

    @Test
    fun `updating note with special characters in title succeeds, no InvalidNoteException`() =
        runTest {
            val initialNotes = fakeNoteRepository.getNotes().first()
            println("Initial notes: $initialNotes")
            val specialTitle = "!@#$%^&*()_+|~=-`{}[]:\";'<>?,./тест한국어"
            val noteToUpdate = initialNotes[0].copy(title = specialTitle)

            var exceptionThrown = false
            try {
                updateNoteUseCase(noteToUpdate)
            } catch (e: InvalidNoteException) {
                exceptionThrown = true
                println("Exception thrown: ${e.message}")
            }

            assertThat(exceptionThrown).isFalse()

            val updatedNotes = fakeNoteRepository.getNotes().first()
            val updatedNote = updatedNotes.find { it.id == noteToUpdate.id }
            println("Updated note: $updatedNote")
            assertThat(updatedNote?.title).isEqualTo(specialTitle)
        }
}