package com.kakos.uniAID.notes.domain.use_case.note.delete

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.kakos.uniAID.notes.data.repository.FakeNoteRepository
import com.kakos.uniAID.notes.domain.model.Note
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

class DeleteNoteUseCaseTest {
    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var fakeNoteRepository: FakeNoteRepository

    private lateinit var deleteNoteUseCase: DeleteNoteUseCase

    @Before
    fun setup() {

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        fakeNoteRepository = FakeNoteRepository()
        deleteNoteUseCase = DeleteNoteUseCase(fakeNoteRepository)
        fakeNoteRepository.shouldHaveFilledList(true)
    }

    @Test
    fun `delete note successfully removes note from repository, one less note in repository`() =
        runTest {
            val initialNotes = fakeNoteRepository.getNotes().first()
            println("initialNotes: $initialNotes")
            val initialSize = initialNotes.size

            val noteToDelete = initialNotes[0]
            deleteNoteUseCase.invoke(noteToDelete)

            val remainingNotes = fakeNoteRepository.getNotes().first()
            println("remainingNotes: $remainingNotes")
            assertThat(remainingNotes.size).isEqualTo(initialSize - 1)
            assertThat(remainingNotes.contains(noteToDelete)).isFalse()
        }

    @Test
    fun `delete note with valid id updates repository state, second note deleted`() = runTest {
        val initialNotes = fakeNoteRepository.getNotes().first()
        println("initialNotes: $initialNotes")
        val validNote = initialNotes[1]

        deleteNoteUseCase.invoke(validNote)

        val notes = fakeNoteRepository.getNotes().first()
        println("notes: $notes")
        assertThat(notes.find { it.id == 2 }).isNull()
        assertThat(notes.size).isEqualTo(2)
    }

    @Test
    fun `delete note with null id throws IllegalArgumentException, IllegalArgumentException`() =
        runTest {
            val noteWithNullId = Note(
                id = null,
                title = "Invalid Note",
                content = "This note has a null ID",
                creationTime = LocalDateTime.now(),
                lastModified = LocalDateTime.now(),
                darkTheme = true
            )

            val exception = kotlin.runCatching {
                deleteNoteUseCase.invoke(noteWithNullId)
            }.exceptionOrNull()

            assertThat(exception).isInstanceOf(IllegalArgumentException::class.java)
            assertThat(exception?.message).isEqualTo("Note with ID null not found")
        }

    @Test
    fun `delete note with zero id throws IllegalArgumentException, IllegalArgumentException`() =
        runTest {
            val noteWithZeroId = Note(
                id = 0,
                title = "Invalid Note",
                content = "This note has an ID of zero",
                creationTime = LocalDateTime.now(),
                lastModified = LocalDateTime.now(),
                darkTheme = true
            )

            val exception = kotlin.runCatching {
                deleteNoteUseCase.invoke(noteWithZeroId)
            }.exceptionOrNull()

            assertThat(exception).isInstanceOf(IllegalArgumentException::class.java)
            assertThat(exception?.message).isEqualTo("Note with ID 0 not found")
        }

    @Test
    fun `delete note with negative id throws IllegalArgumentException, IllegalArgumentException`() =
        runTest {
            val noteWithNegativeId = Note(
                id = -5,
                title = "Invalid Note",
                content = "This note has a negative ID",
                creationTime = LocalDateTime.now(),
                lastModified = LocalDateTime.now(),
                darkTheme = true
            )

            val exception = kotlin.runCatching {
                deleteNoteUseCase.invoke(noteWithNegativeId)
            }.exceptionOrNull()

            assertThat(exception).isInstanceOf(IllegalArgumentException::class.java)
            assertThat(exception?.message).isEqualTo("Note with ID -5 not found")
        }
}