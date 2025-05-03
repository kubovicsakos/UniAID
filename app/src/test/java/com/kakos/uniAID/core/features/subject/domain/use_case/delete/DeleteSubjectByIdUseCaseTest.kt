package com.kakos.uniAID.core.features.subject.domain.use_case.delete

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.kakos.uniAID.core.features.subject.data.repository.FakeSubjectRepository
import com.kakos.uniAID.core.domain.subject.domain.use_case.delete.DeleteSubjectByIdUseCase
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DeleteSubjectByIdUseCaseTest {
    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var fakeSubjectRepository: FakeSubjectRepository
    private lateinit var deleteSubjectByIdUseCase: DeleteSubjectByIdUseCase

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        fakeSubjectRepository = FakeSubjectRepository()
        deleteSubjectByIdUseCase = DeleteSubjectByIdUseCase(fakeSubjectRepository)
        fakeSubjectRepository.shouldHaveFilledList(true)
    }

    @Test
    fun `successful deletion of existing subject by id, subject removed from repository`() =
        runTest {
            val initialSubjects = fakeSubjectRepository.getAllSubjects().first()
            val subjectIdToDelete = initialSubjects[0].id!!
            val initialSize = initialSubjects.size

            deleteSubjectByIdUseCase(subjectIdToDelete)

            val remainingSubjects = fakeSubjectRepository.getAllSubjects().first()
            assertThat(remainingSubjects.size).isEqualTo(initialSize - 1)
            assertThat(remainingSubjects.none { it.id == subjectIdToDelete }).isTrue()
        }

    @Test
    fun `deletion of subject with notes and events, associated notes updated with null subjectId and name`() =
        runTest {
            // Setup some notes and events for a subject
            fakeSubjectRepository.shouldHaveFilledNoteList(true)
            fakeSubjectRepository.shouldHaveFilledEventList(true)
            val subjectIdToDelete = 1 // This subject has associated notes and even


            val subjectWithNotes = fakeSubjectRepository.getSubjectWithNotes(subjectIdToDelete)
            println("Subject with notes: $subjectWithNotes")
            assertThat(subjectWithNotes).isNotNull()
            val noteWithSubject = subjectWithNotes.notes.first()

            val subjectWithEvents = fakeSubjectRepository.getSubjectWithEvents(subjectIdToDelete)
            println("Subject with events: $subjectWithEvents")
            assertThat(subjectWithEvents).isNotNull()
            val eventWithSubject = subjectWithEvents.events.first()

            // Delete the subject -> associated notes and events should be updated
            deleteSubjectByIdUseCase(subjectIdToDelete)

            // Check if notes and events are updated with null subjectId and name
            val notes = fakeSubjectRepository.getNoteItems()
            val events = fakeSubjectRepository.getEventItems()
            println("Notes: $notes")
            println("Events: $events")

            assertThat(notes.none { it.subjectId == subjectIdToDelete }).isTrue()
            assertThat(notes.none { it.subjectName == subjectWithNotes.subject.title }).isTrue()
            assertThat(notes.none { it.id == noteWithSubject.id && it.subjectId != null && it.subjectName != null }).isTrue()
            assertThat(events.none { it.subjectId == subjectIdToDelete }).isTrue()
            assertThat(events.none { it.subjectName == subjectWithEvents.subject.title }).isTrue()
            assertThat(events.none { it.id == eventWithSubject.id && it.subjectId != null && it.subjectName != null }).isTrue()
        }

    @Test
    fun `attempt to delete subject with zero id throws IllegalArgumentException, repository unchanged`() =
        runTest {
            val initialSubjects = fakeSubjectRepository.getAllSubjects().first()
            val initialSize = initialSubjects.size

            var exceptionThrown = false
            try {
                deleteSubjectByIdUseCase(0)
            } catch (e: IllegalArgumentException) {
                exceptionThrown = true
                assertThat(e.message).contains("Invalid subject id")
            }

            assertThat(exceptionThrown).isTrue()
            val remainingSubjects = fakeSubjectRepository.getAllSubjects().first()
            assertThat(remainingSubjects.size).isEqualTo(initialSize)
        }

    @Test
    fun `attempt to delete subject with negative id throws IllegalArgumentException, repository unchanged`() =
        runTest {
            val initialSubjects = fakeSubjectRepository.getAllSubjects().first()
            val initialSize = initialSubjects.size

            var exceptionThrown = false
            try {
                deleteSubjectByIdUseCase(-1)
            } catch (e: IllegalArgumentException) {
                exceptionThrown = true
                assertThat(e.message).contains("Invalid subject id")
            }

            assertThat(exceptionThrown).isTrue()
            val remainingSubjects = fakeSubjectRepository.getAllSubjects().first()
            assertThat(remainingSubjects.size).isEqualTo(initialSize)
        }

    @Test
    fun `deletion of non-existent subject id does not throw exception, repository unchanged`() =
        runTest {
            val initialSubjects = fakeSubjectRepository.getAllSubjects().first()
            val nonExistentId = 999 // Assuming this ID doesn't exist
            val initialSize = initialSubjects.size

            deleteSubjectByIdUseCase(nonExistentId)

            val remainingSubjects = fakeSubjectRepository.getAllSubjects().first()
            assertThat(remainingSubjects.size).isEqualTo(initialSize)
        }

    @Test
    fun `deletion of subject removes it from specific semester list, semester list updated`() =
        runTest {
            val initialSubjects = fakeSubjectRepository.getAllSubjects().first()
            val subjectToDelete = initialSubjects[0]
            val semester = subjectToDelete.semester!!

            val initialSemesterSubjects =
                fakeSubjectRepository.getSubjectsBySemester(semester).first()
            val initialSemesterSize = initialSemesterSubjects.size

            deleteSubjectByIdUseCase(subjectToDelete.id!!)

            val remainingSemesterSubjects =
                fakeSubjectRepository.getSubjectsBySemester(semester).first()
            assertThat(remainingSemesterSubjects.size).isEqualTo(initialSemesterSize - 1)
            assertThat(remainingSemesterSubjects.none { it.id == subjectToDelete.id }).isTrue()
        }
}