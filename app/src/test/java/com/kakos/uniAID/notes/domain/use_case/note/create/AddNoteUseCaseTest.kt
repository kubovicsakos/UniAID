package com.kakos.uniAID.notes.domain.use_case.note.create

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

class AddNoteUseCaseTest {
    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var fakeNoteRepository: FakeNoteRepository
    private lateinit var addNoteUseCase: AddNoteUseCase

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        fakeNoteRepository = FakeNoteRepository()
        addNoteUseCase = AddNoteUseCase(fakeNoteRepository)
        fakeNoteRepository.shouldHaveFilledList(true)
    }

    @Test
    fun `valid note is added to repository successfully, added note is in repository`() = runTest {
        val now = LocalDateTime.now()
        val validNote = Note(
            id = 10,
            title = "Valid Title",
            content = "Some content",
            creationTime = now,
            lastModified = now,
            darkTheme = true
        )

        println("notes before addNote: ${fakeNoteRepository.getNotes().first()}")

        addNoteUseCase(validNote)

        val allNotes = fakeNoteRepository.getNotes()
        println("notes after addNote: ${allNotes.first()}")
        assertThat(allNotes.first()).contains(validNote)
    }


    @Test
    fun `note with future timestamp is added successfully, added note is in repository`() =
        runTest {
            val now = LocalDateTime.now()
            val futureTime = now.plusYears(10)
            val noteWithFutureTime = Note(
                id = 13,
                title = "Future Note",
                content = "This note is from the future",
                creationTime = now,
                lastModified = futureTime,
                darkTheme = true
            )

            addNoteUseCase(noteWithFutureTime)

            val allNotes = fakeNoteRepository.getNotes()
            assertThat(allNotes.first()).contains(noteWithFutureTime)
        }

    @Test
    fun `note with special characters in title is added successfully, added note is in repository`() =
        runTest {
            val now = LocalDateTime.now()
            val specialTitle = "!@#$%^&*()_+|~=-`{}[]:\";'<>?,./тест한국어"
            val noteWithSpecialChars = Note(
                id = 14,
                title = specialTitle,
                content = "Content with special chars",
                creationTime = now,
                lastModified = now,
                darkTheme = true
            )

            var exceptionThrown = false
            try {
                addNoteUseCase(noteWithSpecialChars)
            } catch (e: InvalidNoteException) {
                exceptionThrown = true
                println("Exception thrown: ${e.message}")
            }
            assertThat(exceptionThrown).isFalse()

            val allNotes = fakeNoteRepository.getNotes()
            assertThat(allNotes.first()).contains(noteWithSpecialChars)
        }

    @Test
    fun `note with very long content is added successfully, added note is in repository`() =
        runTest {
            val now = LocalDateTime.now()
            val longContent = "a".repeat(100000) // Very long content with 10000 characters
            val noteWithLongContent = Note(
                id = 15,
                title = "Valid Title",
                content = longContent,
                creationTime = now,
                lastModified = now,
                darkTheme = true
            )

            var exceptionThrown = false
            try {
                addNoteUseCase(noteWithLongContent)
            } catch (e: InvalidNoteException) {
                exceptionThrown = true
                println("Exception thrown: ${e.message}")
            }
            assertThat(exceptionThrown).isFalse()

            val allNotes = fakeNoteRepository.getNotes()
            assertThat(allNotes.first()).contains(noteWithLongContent)
        }

    @Test
    fun `note with very long title is added successfully, added note is in repository`() = runTest {
        val now = LocalDateTime.now()
        val longTitle = "a".repeat(1000) // Very long title with 1000 characters
        val noteWithLongTitle = Note(
            id = 15,
            title = longTitle,
            content = "Valid content",
            creationTime = now,
            lastModified = now,
            darkTheme = true
        )

        var exceptionThrown = false
        try {
            addNoteUseCase(noteWithLongTitle)
        } catch (e: InvalidNoteException) {
            exceptionThrown = true
            println("Exception thrown: ${e.message}")
        }
        assertThat(exceptionThrown).isFalse()

        val allNotes = fakeNoteRepository.getNotes()
        assertThat(allNotes.first()).contains(noteWithLongTitle)
    }

    @Test
    fun `add note to empty repository results in repository with one note, repository with size 1`() =
        runTest {
            fakeNoteRepository.shouldHaveFilledList(false)

            val now = LocalDateTime.now()
            val validNote = Note(
                id = 10,
                title = "Valid Title",
                content = "Some content",
                creationTime = now,
                lastModified = now,
                darkTheme = true
            )
            val initialNotesList = fakeNoteRepository.getNotes().first()
            val initialSize = initialNotesList.size

            addNoteUseCase(validNote)

            val allNotes = fakeNoteRepository.getNotes()
            assertThat(allNotes.first()).contains(validNote)
            assertThat(allNotes.first().size).isEqualTo(initialSize + 1)
        }

    @Test
    fun `note with empty content is added successfully, no InvalidNoteException`() = runTest {
        val now = LocalDateTime.now()
        val emptyContentNote = Note(
            id = 16,
            title = "Empty Content",
            content = "",
            creationTime = now,
            lastModified = now,
            darkTheme = true
        )

        var exceptionThrown = false
        try {
            addNoteUseCase(emptyContentNote)
        } catch (e: InvalidNoteException) {
            exceptionThrown = true
            println("Exception thrown: ${e.message}")
        }
        assertThat(exceptionThrown).isFalse()

        val allNotes = fakeNoteRepository.getNotes()
        assertThat(allNotes.first()).contains(emptyContentNote)
    }

    @Test
    fun `note with empty title throws exception, InvalidNoteException`() = runTest {
        val now = LocalDateTime.now()
        val emptyTitleNote = Note(
            id = 16,
            title = "",
            content = "Content with empty title",
            creationTime = now,
            lastModified = now,
            darkTheme = true
        )

        var exceptionThrown = false
        try {
            addNoteUseCase(emptyTitleNote)
        } catch (e: InvalidNoteException) {
            exceptionThrown = true
            println("Exception thrown: ${e.message}")
        }

        assertThat(exceptionThrown).isTrue()
    }

    @Test
    fun `note with last modified date before creation date throws exception, InvalidNoteException`() =
        runTest {
            val now = LocalDateTime.now()
            val futureTime = now.plusYears(10)
            val noteWithFutureTime = Note(
                id = 17,
                title = "Future Note",
                content = "This note is from the future",
                creationTime = futureTime,
                lastModified = now,
                darkTheme = true
            )

            var exceptionThrown = false
            try {
                addNoteUseCase(noteWithFutureTime)
            } catch (e: InvalidNoteException) {
                exceptionThrown = true
                println("Exception thrown: ${e.message}")
            }

            assertThat(exceptionThrown).isTrue()
        }
}