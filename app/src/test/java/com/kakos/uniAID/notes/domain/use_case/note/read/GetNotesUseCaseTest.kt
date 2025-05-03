package com.kakos.uniAID.notes.domain.use_case.note.read

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.kakos.uniAID.notes.data.repository.FakeNoteRepository
import com.kakos.uniAID.notes.domain.util.NoteOrder
import com.kakos.uniAID.notes.domain.util.OrderType
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetNotesUseCaseTest {

    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var fakeNoteRepository: FakeNoteRepository

    private lateinit var getNotesUseCase: GetNotesUseCase

    @Before
    fun setup() {

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        fakeNoteRepository = FakeNoteRepository()
        getNotesUseCase = GetNotesUseCase(fakeNoteRepository)
        fakeNoteRepository.shouldHaveFilledList(true)
    }

    @Test
    fun `get notes sort by title, sorted by ascending title`() = runTest {
        val notes = getNotesUseCase.invoke(NoteOrder.Title(OrderType.Ascending)).first()

        for (i in 0..notes.size - 2) {
            assertThat(notes[i].title).isLessThan(notes[i + 1].title)
        }

    }

    @Test
    fun `get notes sort by title, sorted by descending title`() = runTest {
        val notes = getNotesUseCase.invoke(NoteOrder.Title(OrderType.Descending)).first()

        for (i in 0..notes.size - 2) {
            assertThat(notes[i].title).isGreaterThan(notes[i + 1].title)
        }

    }

    @Test
    fun `get notes sort by date, sorted by ascending date`() = runTest {
        // from oldest to newest
        val notes = getNotesUseCase.invoke(NoteOrder.Date(OrderType.Ascending)).first()

        for (i in 0..notes.size - 2) {
            assertThat(notes[i].id).isGreaterThan(notes[i + 1].id)
        }
    }

    @Test
    fun `get notes sort by date, sorted by descending date`() = runTest {
        // from newest to oldest
        val notes = getNotesUseCase.invoke(NoteOrder.Date(OrderType.Descending)).first()

        for (i in 0..notes.size - 2) {
            assertThat(notes[i].id).isLessThan(notes[i + 1].id)
        }

    }

    @Test
    fun `get notes returns list when repository is not empty`() = runTest {
        val notes = getNotesUseCase.invoke(NoteOrder.Title(OrderType.Ascending)).first()

        assertThat(
            notes.isEmpty()
        ).isFalse()
        assertThat(
            notes.size == 3
        ).isTrue()
    }

    @Test
    fun `get notes returns empty list when repository is empty`() = runTest {
        fakeNoteRepository.shouldHaveFilledList(false)
        val notes = getNotesUseCase.invoke(NoteOrder.Title(OrderType.Ascending)).first()

        assertThat(
            notes.isEmpty()
        ).isTrue()
    }
}