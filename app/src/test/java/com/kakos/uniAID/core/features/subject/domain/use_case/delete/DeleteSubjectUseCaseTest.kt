package com.kakos.uniAID.core.features.subject.domain.use_case.delete

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.kakos.uniAID.core.features.subject.data.repository.FakeSubjectRepository
import com.kakos.uniAID.core.domain.subject.domain.model.Subject
import com.kakos.uniAID.core.domain.subject.domain.use_case.delete.DeleteSubjectUseCase
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DeleteSubjectUseCaseTest {
    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var fakeSubjectRepository: FakeSubjectRepository
    private lateinit var deleteSubjectUseCase: DeleteSubjectUseCase

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        fakeSubjectRepository = FakeSubjectRepository()
        deleteSubjectUseCase = DeleteSubjectUseCase(fakeSubjectRepository)
        fakeSubjectRepository.shouldHaveFilledList(true)
    }

    @Test
    fun `successful deletion of existing subject, subject removed from repository`() = runTest {
        val initialSubjects = fakeSubjectRepository.getAllSubjects().first()
        val subjectToDelete = initialSubjects[0]
        val initialSize = initialSubjects.size

        deleteSubjectUseCase(subjectToDelete)

        val remainingSubjects = fakeSubjectRepository.getAllSubjects().first()
        assertThat(remainingSubjects.size).isEqualTo(initialSize - 1)
        assertThat(remainingSubjects).doesNotContain(subjectToDelete)
    }

    @Test
    fun `deletion of subject with notes and events, associated entities updated with null references`() =
        runTest {
            fakeSubjectRepository.shouldHaveFilledNoteList(true)
            fakeSubjectRepository.shouldHaveFilledEventList(true)
            val initialSubjects = fakeSubjectRepository.getAllSubjects().first()
            val subjectToDelete = initialSubjects[0]

            val subjectWithNotes = fakeSubjectRepository.getSubjectWithNotes(subjectToDelete.id!!)
            println("Subject with notes: $subjectWithNotes")
            assertThat(subjectWithNotes).isNotNull()
            val noteWithSubject = subjectWithNotes.notes.first()

            val subjectWithEvents = fakeSubjectRepository.getSubjectWithEvents(subjectToDelete.id!!)
            println("Subject with events: $subjectWithEvents")
            assertThat(subjectWithEvents).isNotNull()
            val eventWithSubject = subjectWithEvents.events.first()

            deleteSubjectUseCase(subjectToDelete)

            val notes = fakeSubjectRepository.getNoteItems()
            val events = fakeSubjectRepository.getEventItems()
            println("Notes: $notes")
            println("Events: $events")

            assertThat(notes.none { it.subjectId == subjectToDelete.id!! }).isTrue()
            assertThat(notes.none { it.subjectName == subjectWithNotes.subject.title }).isTrue()
            assertThat(notes.none { it.id == noteWithSubject.id && it.subjectId != null && it.subjectName != null }).isTrue()
            assertThat(events.none { it.subjectId == subjectToDelete.id!! }).isTrue()
            assertThat(events.none { it.subjectName == subjectWithEvents.subject.title }).isTrue()
            assertThat(events.none { it.id == eventWithSubject.id && it.subjectId != null && it.subjectName != null }).isTrue()
        }

    @Test
    fun `attempt to delete subject with null id throws IllegalArgumentException, repository unchanged`() =
        runTest {
            val initialSubjects = fakeSubjectRepository.getAllSubjects().first()
            val initialSize = initialSubjects.size

            val subjectWithNullId = Subject(
                id = null,
                title = "Test Subject",
                description = "Description of test subject",
                semester = 1,
                credit = 3,
                finalGrade = 4,
                grade = 4
            )

            var exceptionThrown = false
            try {
                deleteSubjectUseCase(subjectWithNullId)
            } catch (e: IllegalArgumentException) {
                exceptionThrown = true
                assertThat(e.message).contains("Invalid subject id null")
            }

            assertThat(exceptionThrown).isTrue()
            val remainingSubjects = fakeSubjectRepository.getAllSubjects().first()
            assertThat(remainingSubjects.size).isEqualTo(initialSize)
        }

    @Test
    fun `attempt to delete subject with zero id throws IllegalArgumentException, repository unchanged`() =
        runTest {
            val initialSubjects = fakeSubjectRepository.getAllSubjects().first()
            val initialSize = initialSubjects.size

            val subjectWithZeroId = Subject(
                id = 0,
                title = "Test Subject",
                description = "Description of test subject",
                semester = 1,
                credit = 3,
                finalGrade = 4,
                grade = 4
            )

            var exceptionThrown = false
            try {
                deleteSubjectUseCase(subjectWithZeroId)
            } catch (e: IllegalArgumentException) {
                exceptionThrown = true
                assertThat(e.message).contains("Invalid subject id 0")
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

            val subjectWithNegativeId = Subject(
                id = -1,
                title = "Test Subject",
                description = "Description of test subject",
                semester = 1,
                credit = 3,
                finalGrade = 4,
                grade = 4
            )

            var exceptionThrown = false
            try {
                deleteSubjectUseCase(subjectWithNegativeId)
            } catch (e: IllegalArgumentException) {
                exceptionThrown = true
                assertThat(e.message).contains("Invalid subject id -1")
            }

            assertThat(exceptionThrown).isTrue()
            val remainingSubjects = fakeSubjectRepository.getAllSubjects().first()
            assertThat(remainingSubjects.size).isEqualTo(initialSize)
        }

    @Test
    fun `attempt to delete non-existent subject does not delete anything, repository unchanged`() =
        runTest {
            val initialSubjects = fakeSubjectRepository.getAllSubjects().first()
            val initialSize = initialSubjects.size

            val nonExistentSubject = Subject(
                id = 999, // Assuming this ID doesn't exist
                title = "Non-existent Subject",
                description = "This subject does not exist",
                semester = 1,
                credit = 3,
                finalGrade = 4,
                grade = 4
            )

            deleteSubjectUseCase(nonExistentSubject)

            val remainingSubjects = fakeSubjectRepository.getAllSubjects().first()
            assertThat(remainingSubjects.size).isEqualTo(initialSize)
            assertThat(remainingSubjects).doesNotContain(nonExistentSubject)
            assertThat(remainingSubjects).isEqualTo(initialSubjects)
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

            deleteSubjectUseCase(subjectToDelete)

            val remainingSemesterSubjects =
                fakeSubjectRepository.getSubjectsBySemester(semester).first()
            assertThat(remainingSemesterSubjects.size).isEqualTo(initialSemesterSize - 1)
            assertThat(remainingSemesterSubjects).doesNotContain(subjectToDelete)
        }
}