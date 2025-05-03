package com.kakos.uniAID.core.features.subject.domain.use_case.read

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.kakos.uniAID.core.features.subject.data.repository.FakeSubjectRepository
import com.kakos.uniAID.core.domain.subject.domain.use_case.read.GetNotesOfSubjectUseCase
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetNotesOfSubjectUseCaseTest {
    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var fakeSubjectRepository: FakeSubjectRepository
    private lateinit var getNotesOfSubjectUseCase: GetNotesOfSubjectUseCase

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        fakeSubjectRepository = FakeSubjectRepository()
        getNotesOfSubjectUseCase = GetNotesOfSubjectUseCase(fakeSubjectRepository)
        fakeSubjectRepository.shouldHaveFilledList(true)
    }

    @Test
    fun `get notes for existing subject with notes returns correct notes list`() = runTest {
        fakeSubjectRepository.shouldHaveFilledNoteList(true)
        val subjectId = 1 // Subject with notes in the fake repository

        val notes = getNotesOfSubjectUseCase(subjectId).first()
        val noteItems = fakeSubjectRepository.getNoteItems()

        assertThat(notes).isNotEmpty()
        assertThat(notes.size).isEqualTo(1)
        assertThat(notes[0].title).isEqualTo("b")
        assertThat(notes[0].subjectId).isEqualTo(subjectId)
        assertThat(notes[0].subjectName).isEqualTo(noteItems[1].subjectName)
    }

    @Test
    fun `get notes for existing subject without notes returns empty list`() = runTest {
        fakeSubjectRepository.shouldHaveFilledNoteList(true)
        val subjectId = 3 // Subject without notes in the fake repository

        val notes = getNotesOfSubjectUseCase(subjectId).first()

        assertThat(notes).isEmpty()
    }

    @Test
    fun `get notes for non-existent subject id returns empty list`() = runTest {
        fakeSubjectRepository.shouldHaveFilledNoteList(true)
        val nonExistentSubjectId = 999 // Non-existent subject ID

        val notes = getNotesOfSubjectUseCase(nonExistentSubjectId).first()

        assertThat(notes).isEmpty()
    }

    @Test
    fun `get notes with zero subject id throws IllegalArgumentException`() = runTest {
        fakeSubjectRepository.shouldHaveFilledNoteList(true)
        val invalidSubjectId = 0

        var exceptionThrown = false
        try {
            getNotesOfSubjectUseCase(invalidSubjectId).first()
        } catch (e: IllegalArgumentException) {
            exceptionThrown = true
            assertThat(e.message).contains("Invalid subject id")
        }

        assertThat(exceptionThrown).isTrue()
    }

    @Test
    fun `get notes with negative subject id throws IllegalArgumentException`() = runTest {
        fakeSubjectRepository.shouldHaveFilledNoteList(true)
        val invalidSubjectId = -1

        var exceptionThrown = false
        try {
            getNotesOfSubjectUseCase(invalidSubjectId).first()
        } catch (e: IllegalArgumentException) {
            exceptionThrown = true
            assertThat(e.message).contains("Invalid subject id")
        }

        assertThat(exceptionThrown).isTrue()
    }

    @Test
    fun `deleted subject has no related notes, returns empty list`() = runTest {
        fakeSubjectRepository.shouldHaveFilledNoteList(true)
        val subjectId = 1

        // First verify the subject has notes
        val initialNotes = getNotesOfSubjectUseCase(subjectId).first()
        assertThat(initialNotes).isNotEmpty()

        // Delete the subject
        fakeSubjectRepository.deleteSubjectById(subjectId)

        // Try to get notes - should return empty list due to error handling
        val notesAfterDeletion = getNotesOfSubjectUseCase(subjectId).first()
        assertThat(notesAfterDeletion).isEmpty()
    }

}