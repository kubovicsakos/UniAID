package com.kakos.uniAID.core.features.subject.domain.use_case.read

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.kakos.uniAID.core.features.subject.data.repository.FakeSubjectRepository
import com.kakos.uniAID.core.domain.subject.domain.model.Subject
import com.kakos.uniAID.core.domain.subject.domain.use_case.read.GetSubjectsBySemesterUseCase
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetSubjectsBySemesterUseCaseTest {
    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var fakeSubjectRepository: FakeSubjectRepository
    private lateinit var getSubjectsBySemesterUseCase: GetSubjectsBySemesterUseCase

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        fakeSubjectRepository = FakeSubjectRepository()
        getSubjectsBySemesterUseCase = GetSubjectsBySemesterUseCase(fakeSubjectRepository)
        fakeSubjectRepository.shouldHaveFilledList(true)
    }

    @Test
    fun `get subjects for existing semester returns correct subjects list`() = runTest {
        val semester = 1 // Semester 1 has subjects in the fake repository

        val subjects = getSubjectsBySemesterUseCase(semester).first()

        assertThat(subjects).isNotEmpty()
        assertThat(subjects.size).isEqualTo(2) // Based on fake data setup
        assertThat(subjects[0].title).isEqualTo("a")
        assertThat(subjects[1].title).isEqualTo("b")
        assertThat(subjects.all { it.semester == semester }).isTrue()
    }

    @Test
    fun `get subjects for semester with no subjects, returns empty list`() = runTest {
        val semester = 3 // Assuming semester 3 has no subjects

        val subjects = getSubjectsBySemesterUseCase(semester).first()

        assertThat(subjects).isEmpty()
    }

    @Test
    fun `get subjects with zero semester throws IllegalArgumentException, IllegalArgumentException`() =
        runTest {
            val invalidSemester = 0

            var exceptionThrown = false
            try {
                getSubjectsBySemesterUseCase(invalidSemester).first()
            } catch (e: IllegalArgumentException) {
                exceptionThrown = true
                assertThat(e.message).contains("Invalid semester")
            }

            assertThat(exceptionThrown).isTrue()
        }

    @Test
    fun `get subjects with negative semester throws IllegalArgumentException, IllegalArgumentException`() =
        runTest {
            val invalidSemester = -1

            var exceptionThrown = false
            try {
                getSubjectsBySemesterUseCase(invalidSemester).first()
            } catch (e: IllegalArgumentException) {
                exceptionThrown = true
                assertThat(e.message).contains("Invalid semester")
            }

            assertThat(exceptionThrown).isTrue()
        }

    @Test
    fun `adding new subject to semester is reflected in results`() = runTest {
        val targetSemester = 3
        val initialSubjects = getSubjectsBySemesterUseCase(targetSemester).first()
        val initialSize = initialSubjects.size

        val newSubject = Subject(
            id = 4,
            title = "New Subject",
            description = "New Description",
            semester = targetSemester,
            credit = 3,
            finalGrade = 4,
            grade = 4
        )
        fakeSubjectRepository.insert(newSubject)

        val updatedSubjects = getSubjectsBySemesterUseCase(targetSemester).first()
        assertThat(updatedSubjects.size).isEqualTo(initialSize + 1)
        assertThat(updatedSubjects).containsExactly(newSubject)
    }

    @Test
    fun `deleting subject from semester is reflected in results`() = runTest {
        val targetSemester = 1
        val initialSubjects = getSubjectsBySemesterUseCase(targetSemester).first()
        val subjectToDelete = initialSubjects[0]
        val initialSize = initialSubjects.size

        fakeSubjectRepository.delete(subjectToDelete)

        val remainingSubjects = getSubjectsBySemesterUseCase(targetSemester).first()
        assertThat(remainingSubjects.size).isEqualTo(initialSize - 1)
        assertThat(remainingSubjects).doesNotContain(subjectToDelete)
    }

    @Test
    fun `updating subject semester moves it to different semester results`() = runTest {
        val initialSemester = 1
        val targetSemester = 3

        val initialSubjectsInSource = getSubjectsBySemesterUseCase(initialSemester).first()
        val initialSubjectsInTarget = getSubjectsBySemesterUseCase(targetSemester).first()
        val initialSize = initialSubjectsInSource.size
        assertThat(initialSize).isEqualTo(2)
        assertThat(initialSubjectsInTarget).isEmpty()

        val subjectToUpdate = initialSubjectsInSource[0]
        val updatedSubject = subjectToUpdate.copy(semester = targetSemester)

        fakeSubjectRepository.update(updatedSubject)

        val newSubjectsInSource = getSubjectsBySemesterUseCase(initialSemester).first()
        val newSubjectsInTarget = getSubjectsBySemesterUseCase(targetSemester).first()

        assertThat(newSubjectsInSource).doesNotContain(subjectToUpdate)
        assertThat(newSubjectsInTarget).contains(updatedSubject)
        assertThat(newSubjectsInSource.size).isEqualTo(initialSize - 1)
        assertThat(newSubjectsInTarget.size).isEqualTo(1)
    }
}