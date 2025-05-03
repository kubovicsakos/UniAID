package com.kakos.uniAID.notes.domain.use_case.note.read

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.kakos.uniAID.notes.data.repository.FakeNoteRepository
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetNoteUseCaseTest {
    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var fakeNoteRepository: FakeNoteRepository

    private lateinit var getNoteUseCase: GetNoteUseCase

    @Before
    fun setup() {

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        fakeNoteRepository = FakeNoteRepository()
        getNoteUseCase = GetNoteUseCase(fakeNoteRepository)
        fakeNoteRepository.shouldHaveFilledList(true)
    }

    @Test
    fun `retrieving existing note by id returns correct note, returns the first note`() = runTest {
        val expectedNote = fakeNoteRepository.getNotes().first()[0]
        val id = expectedNote.id
        println("Expected note: $expectedNote")

        val result = getNoteUseCase(id!!)
        println("Result: $result")

        assertThat(result).isEqualTo(expectedNote)
    }

    @Test
    fun `retrieving non-existent note id returns null, returns null`() = runTest {
        val nonExistentId = 999

        val result = getNoteUseCase(nonExistentId)
        println("Result: $result")

        assertThat(result).isNull()
    }

    @Test
    fun `retrieving note from empty repository returns null, returns null`() = runTest {
        fakeNoteRepository.shouldHaveFilledList(false)

        val result = getNoteUseCase(1)
        println("Result: $result")

        assertThat(result).isNull()
    }

    @Test
    fun `retrieving each note in repository returns correct note, returns the correct note for every id`() =
        runTest {
            val notes = fakeNoteRepository.getNotes().first()

            for (note in notes) {
                val id = note.id
                val result = getNoteUseCase(id!!)
                println("Result: $result, Expected: $note")
                assertThat(result).isEqualTo(note)
            }
        }

    @Test
    fun `zero note id throws IllegalArgumentException, IllegalArgumentException`() = runTest {
        var exceptionThrown = false
        try {
            getNoteUseCase(0)
        } catch (e: IllegalArgumentException) {
            exceptionThrown = true
            println("Exception thrown: ${e.message}")
        }

        assertThat(exceptionThrown).isTrue()
    }

    @Test
    fun `negative note id throws IllegalArgumentException, IllegalArgumentException`() = runTest {
        var exceptionThrown = false
        try {
            getNoteUseCase(-5)
        } catch (e: IllegalArgumentException) {
            exceptionThrown = true
            println("Exception thrown: ${e.message}")
        }

        assertThat(exceptionThrown).isTrue()
    }

    @Test
    fun `max positive note id does not throw exception, no IllegalArgumentException`() = runTest {
        val bigId = Int.MAX_VALUE

        var exceptionThrown = false
        try {
            getNoteUseCase(bigId)
        } catch (e: IllegalArgumentException) {
            exceptionThrown = true
            println("Exception thrown: ${e.message}")
        }

        assertThat(exceptionThrown).isFalse()
    }

}