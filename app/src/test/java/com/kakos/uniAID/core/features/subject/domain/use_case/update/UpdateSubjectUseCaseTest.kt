package com.kakos.uniAID.core.features.subject.domain.use_case.update

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.kakos.uniAID.core.features.subject.data.repository.FakeSubjectRepository
import com.kakos.uniAID.core.domain.subject.domain.model.InvalidSubjectException
import com.kakos.uniAID.core.domain.subject.domain.model.Subject
import com.kakos.uniAID.core.domain.subject.domain.use_case.update.UpdateSubjectUseCase
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class UpdateSubjectUseCaseTest {
    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var fakeSubjectRepository: FakeSubjectRepository
    private lateinit var updateSubjectUseCase: UpdateSubjectUseCase

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        fakeSubjectRepository = FakeSubjectRepository()
        updateSubjectUseCase = UpdateSubjectUseCase(fakeSubjectRepository)
        fakeSubjectRepository.shouldHaveFilledList(true)
    }

    @Test
    fun `successful update of existing subject, subject updated in repository`() = runTest {
        val initialSubjects = fakeSubjectRepository.getAllSubjects().first()
        val subjectToUpdate = initialSubjects[0]
        val updatedTitle = "Updated Title"
        val updatedDescription = "Updated Description"
        val updatedCredit = 6
        val updatedFinalGrade = 5
        val updatedSubject = subjectToUpdate.copy(
            title = updatedTitle,
            description = updatedDescription,
            credit = updatedCredit,
            finalGrade = updatedFinalGrade
        )

        updateSubjectUseCase(updatedSubject)

        val retrievedSubject = fakeSubjectRepository.getSubjectById(updatedSubject.id!!)
        val updatedSubjects = fakeSubjectRepository.getAllSubjects().first()

        assertThat(updatedSubjects).doesNotContain(subjectToUpdate)
        assertThat(updatedSubjects).contains(updatedSubject)

        assertThat(retrievedSubject).isEqualTo(updatedSubject)
        assertThat(retrievedSubject?.title).isEqualTo(updatedTitle)
        assertThat(retrievedSubject?.description).isEqualTo(updatedDescription)
        assertThat(retrievedSubject?.credit).isEqualTo(updatedCredit)
        assertThat(retrievedSubject?.finalGrade).isEqualTo(updatedFinalGrade)
    }

    @Test
    fun `update subject with blank title throws InvalidSubjectException, repository unchanged`() =
        runTest {
            val initialSubjects = fakeSubjectRepository.getAllSubjects().first()
            val subjectToUpdate = initialSubjects[0]
            val invalidSubject = subjectToUpdate.copy(title = "")

            var exceptionThrown = false
            try {
                updateSubjectUseCase(invalidSubject)
            } catch (e: InvalidSubjectException) {
                exceptionThrown = true
                assertThat(e.message).contains("Title")
            }

            assertThat(exceptionThrown).isTrue()
            val unchangedSubject = fakeSubjectRepository.getSubjectById(subjectToUpdate.id!!)
            assertThat(unchangedSubject).isEqualTo(subjectToUpdate)
        }

    @Test
    fun `update subject with null id throws InvalidSubjectException, repository unchanged`() =
        runTest {
            val initialSubjects = fakeSubjectRepository.getAllSubjects().first()
            // Ensure we have valid values for all other fields to reach ID validation
            val subjectToUpdate = initialSubjects[0].copy(
                id = null,
                semester = 1,
                credit = 3
            )

            var exceptionThrown = false
            try {
                updateSubjectUseCase(subjectToUpdate)
            } catch (e: InvalidSubjectException) {
                exceptionThrown = true
                assertThat(e.message).contains("Invalid subject id")
            }

            assertThat(exceptionThrown).isTrue()
            val updatedSubjects = fakeSubjectRepository.getAllSubjects().first()
            assertThat(updatedSubjects).isEqualTo(initialSubjects)
        }

    @Test
    fun `update subject with zero id throws InvalidSubjectException, repository unchanged`() =
        runTest {
            val initialSubjects = fakeSubjectRepository.getAllSubjects().first()
            // Ensure we have valid values for all other fields to reach ID validation
            val subjectToUpdate = initialSubjects[0].copy(
                id = 0,
                semester = 1,
                credit = 3
            )

            var exceptionThrown = false
            try {
                updateSubjectUseCase(subjectToUpdate)
            } catch (e: InvalidSubjectException) {
                exceptionThrown = true
                assertThat(e.message).contains("Invalid subject id")
            }

            assertThat(exceptionThrown).isTrue()
            val updatedSubjects = fakeSubjectRepository.getAllSubjects().first()
            assertThat(updatedSubjects).isEqualTo(initialSubjects)
        }

    @Test
    fun `update subject with negative id throws InvalidSubjectException, repository unchanged`() =
        runTest {
            val initialSubjects = fakeSubjectRepository.getAllSubjects().first()
            // Ensure we have valid values for all other fields to reach ID validation
            val subjectToUpdate = initialSubjects[0].copy(
                id = -1,
                semester = 1,
                credit = 3
            )

            var exceptionThrown = false
            try {
                updateSubjectUseCase(subjectToUpdate)
            } catch (e: InvalidSubjectException) {
                exceptionThrown = true
                assertThat(e.message).contains("Invalid subject id")
            }

            assertThat(exceptionThrown).isTrue()
            val updatedSubjects = fakeSubjectRepository.getAllSubjects().first()
            assertThat(updatedSubjects).isEqualTo(initialSubjects)
        }

    @Test
    fun `update subject semester changes its semester association, subject moved to new semester`() =
        runTest {
            val initialSubjects = fakeSubjectRepository.getAllSubjects().first()
            val originalSemester = 2
            val subjectToUpdate = initialSubjects.find { it.semester == originalSemester }!!
            val targetSemester = 3
            val updatedSubject = subjectToUpdate.copy(semester = targetSemester)

            val initialSourceSemesterSubjects =
                fakeSubjectRepository.getSubjectsBySemester(originalSemester).first()
            val initialTargetSemesterSubjects =
                fakeSubjectRepository.getSubjectsBySemester(targetSemester).first()

            assertThat(initialSourceSemesterSubjects).contains(subjectToUpdate)
            assertThat(initialTargetSemesterSubjects).doesNotContain(updatedSubject)

            updateSubjectUseCase(updatedSubject)

            val updatedSourceSemesterSubjects =
                fakeSubjectRepository.getSubjectsBySemester(originalSemester).first()
            val updatedTargetSemesterSubjects =
                fakeSubjectRepository.getSubjectsBySemester(targetSemester).first()

            assertThat(updatedSourceSemesterSubjects).doesNotContain(subjectToUpdate)
            assertThat(updatedTargetSemesterSubjects).contains(updatedSubject)
        }

    @Test
    fun `update non-existent subject does not add it to repository, repository unchanged`() =
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

            assertThat(initialSubjects).doesNotContain(nonExistentSubject)

            updateSubjectUseCase(nonExistentSubject)

            val updatedSubjects = fakeSubjectRepository.getAllSubjects().first()
            val updatedSubject = fakeSubjectRepository.getSubjectById(999)

            assertThat(updatedSubjects.size).isEqualTo(initialSize)
            assertThat(updatedSubjects).doesNotContain(nonExistentSubject)
            assertThat(updatedSubject).isNull()
        }

    @Test
    fun `partial update of subject preserves unchanged fields, only specified fields updated`() =
        runTest {
            val initialSubjects = fakeSubjectRepository.getAllSubjects().first()
            val originalSubject = initialSubjects[0]

            // Include valid credit value to avoid credit validation error
            val partiallyUpdatedSubject = originalSubject.copy(
                title = "New Title",
                description = "New Description",
                credit = originalSubject.credit  // Ensure credit value is preserved
            )

            updateSubjectUseCase(partiallyUpdatedSubject)

            val retrievedSubject = fakeSubjectRepository.getSubjectById(originalSubject.id!!)

            assertThat(retrievedSubject?.title).isEqualTo("New Title")
            assertThat(retrievedSubject?.description).isEqualTo("New Description")
            assertThat(retrievedSubject?.semester).isEqualTo(originalSubject.semester)
            assertThat(retrievedSubject?.credit).isEqualTo(originalSubject.credit)
            assertThat(retrievedSubject?.finalGrade).isEqualTo(originalSubject.finalGrade)
            assertThat(retrievedSubject?.grade).isEqualTo(originalSubject.grade)
        }
}