package com.kakos.uniAID.notes.domain.use_case.note.delete

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

class DeleteNoteByIdUseCaseTest {
    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var fakeNoteRepository: FakeNoteRepository

    private lateinit var deleteNoteByIdUseCase: DeleteNoteByIdUseCase

    @Before
    fun setup() {

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        fakeNoteRepository = FakeNoteRepository()
        deleteNoteByIdUseCase = DeleteNoteByIdUseCase(fakeNoteRepository)
        fakeNoteRepository.shouldHaveFilledList(true)
    }

    @Test
    fun `deleting existing note by id removes it from repository, repository changed`() = runTest {
        val initialNotesList = fakeNoteRepository.getNotes().first()
        println("initialNotesList: $initialNotesList")
        val initialSize = initialNotesList.size
        val noteIdToDelete = initialNotesList[0].id ?: 1

        deleteNoteByIdUseCase(noteIdToDelete)

        val finalNotesList = fakeNoteRepository.getNotes().first()
        println("finalNotesList: $finalNotesList")
        val finalSize = finalNotesList.size

        assertThat(finalSize).isEqualTo(initialSize - 1)
        assertThat(finalNotesList.any { it.id == noteIdToDelete }).isFalse()
    }

    @Test
    fun `deleting non-existent note id does not change repository size, repository unchanged`() =
        runTest {
            val initialNotesList = fakeNoteRepository.getNotes().first()
            println("initialNotesList: $initialNotesList")
            val nonExistentNoteId = 99

            deleteNoteByIdUseCase(nonExistentNoteId)

            val finalNotesList = fakeNoteRepository.getNotes().first()
            println("finalNotesList: $finalNotesList")
            assertThat(finalNotesList.size).isEqualTo(initialNotesList.size)
        }

    @Test
    fun `deleting every note results in empty repository, empty repository`() = runTest {
        val initialNotesList = fakeNoteRepository.getNotes().first()
        println("initialNotesList: $initialNotesList")

        for (note in initialNotesList) {
            deleteNoteByIdUseCase(note.id ?: 1)
        }

        val finalNotesList = fakeNoteRepository.getNotes().first()
        println("finalNotesList: $finalNotesList")
        assertThat(finalNotesList.isEmpty()).isTrue()
    }

    @Test
    fun `deletion works correctly when repository is nearly empty, final empty repository`() =
        runTest {
            val initialNotesList = fakeNoteRepository.getNotes().first()
            println("initialNotesList: $initialNotesList")
            val initialSize = initialNotesList.size

            for (i in 1..<initialSize) { // Delete all but one note
                deleteNoteByIdUseCase(i)
            }

            // Check only one note remains
            val intermediateList = fakeNoteRepository.getNotes().first()
            println("intermediateList: $intermediateList")
            assertThat(intermediateList.size).isEqualTo(1)

            // Delete the last one
            deleteNoteByIdUseCase(initialSize)

            // Check it's now empty
            val finalNotesList = fakeNoteRepository.getNotes().first()
            println("finalNotesList: $finalNotesList")
            assertThat(finalNotesList.isEmpty()).isTrue()
        }

    @Test
    fun `zero note id throws IllegalArgumentException, IllegalArgumentException`() = runTest {
        var exceptionThrown = false
        try {
            deleteNoteByIdUseCase(0)
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
            deleteNoteByIdUseCase(-5)
        } catch (e: IllegalArgumentException) {
            exceptionThrown = true
            println("Exception thrown: ${e.message}")
        }

        assertThat(exceptionThrown).isTrue()
    }

    @Test
    fun `max positive note id does not throw exception, no exception`() = runTest {
        val bigId = Int.MAX_VALUE

        var exceptionThrown = false
        try {
            deleteNoteByIdUseCase(bigId)
        } catch (e: IllegalArgumentException) {
            exceptionThrown = true
            println("Exception thrown: ${e.message}")
        }

        assertThat(exceptionThrown).isFalse()
    }
}