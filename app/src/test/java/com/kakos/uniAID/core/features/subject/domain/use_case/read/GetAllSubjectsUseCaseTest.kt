package com.kakos.uniAID.core.features.subject.domain.use_case.read

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.kakos.uniAID.core.features.subject.data.repository.FakeSubjectRepository
import com.kakos.uniAID.core.domain.subject.domain.model.Subject
import com.kakos.uniAID.core.domain.subject.domain.use_case.read.GetAllSubjectsUseCase
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetAllSubjectsUseCaseTest {
    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var fakeSubjectRepository: FakeSubjectRepository
    private lateinit var getAllSubjectsUseCase: GetAllSubjectsUseCase

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        fakeSubjectRepository = FakeSubjectRepository()
        getAllSubjectsUseCase = GetAllSubjectsUseCase(fakeSubjectRepository)
        fakeSubjectRepository.shouldHaveFilledList(true)
    }

    @Test
    fun `get all subjects from filled repository returns list with correct subjects`() = runTest {
        fakeSubjectRepository.shouldHaveFilledList(true)

        val subjects = getAllSubjectsUseCase().first()

        assertThat(subjects).isNotEmpty()
        assertThat(subjects.size).isEqualTo(3)
        assertThat(subjects[0].title).isEqualTo("a")
        assertThat(subjects[1].title).isEqualTo("b")
        assertThat(subjects[2].title).isEqualTo("c")
    }

    @Test
    fun `get all subjects from empty repository returns empty list`() = runTest {
        fakeSubjectRepository.shouldHaveFilledList(false)
        val subjects = getAllSubjectsUseCase().first()

        assertThat(subjects).isEmpty()
    }

    @Test
    fun `repository updates are reflected in the flow containing updated subjects`() = runTest {

        val initialSubjects = getAllSubjectsUseCase().first()
        val initialSize = initialSubjects.size
        assertThat(initialSize).isEqualTo(3)

        val newSubject = Subject(
            id = 4,
            title = "d",
            description = "Description 4",
            semester = 2,
            credit = 5,
            finalGrade = 5,
            grade = 5
        )
        fakeSubjectRepository.insert(newSubject)

        val updatedSubjects = getAllSubjectsUseCase().first()
        assertThat(updatedSubjects.size).isEqualTo(initialSize + 1)
        assertThat(updatedSubjects).contains(newSubject)
    }

    @Test
    fun `subjects are returned in original order from repository`() = runTest {
        fakeSubjectRepository.shouldHaveFilledList(false)
        // Create a specific order of subjects
        val subject1 = Subject(
            id = 1,
            title = "A",
            description = "First",
            semester = 1,
            credit = 3,
            finalGrade = 4,
            grade = 4
        )
        val subject2 = Subject(
            id = 2,
            title = "B",
            description = "Second",
            semester = 1,
            credit = 4,
            finalGrade = 5,
            grade = 5
        )
        val subject3 = Subject(
            id = 3,
            title = "C",
            description = "Third",
            semester = 2,
            credit = 5,
            finalGrade = 3,
            grade = 3
        )

        fakeSubjectRepository.insert(subject1)
        fakeSubjectRepository.insert(subject2)
        fakeSubjectRepository.insert(subject3)

        val subjects = getAllSubjectsUseCase().first()

        assertThat(subjects.size).isEqualTo(3)
        assertThat(subjects[0]).isEqualTo(subject1)
        assertThat(subjects[1]).isEqualTo(subject2)
        assertThat(subjects[2]).isEqualTo(subject3)
    }

    @Test
    fun `deleting subject from repository is reflected in returned flow`() = runTest {

        val initialSubjects = getAllSubjectsUseCase().first()
        val initialSize = initialSubjects.size
        val subjectToDelete = initialSubjects[0]

        fakeSubjectRepository.delete(subjectToDelete)

        val updatedSubjects = getAllSubjectsUseCase().first()
        assertThat(updatedSubjects.size).isEqualTo(initialSize - 1)
        assertThat(updatedSubjects).doesNotContain(subjectToDelete)
    }

    @Test
    fun `updating subject in repository is reflected in returned flow`() = runTest {

        val initialSubjects = getAllSubjectsUseCase().first()
        val subjectToUpdate = initialSubjects[0]
        val updatedSubject = subjectToUpdate.copy(
            title = "Updated Title",
            description = "Updated Description",
            finalGrade = 5
        )

        fakeSubjectRepository.update(updatedSubject)

        val subjects = getAllSubjectsUseCase().first()
        assertThat(subjects).contains(updatedSubject)
        assertThat(subjects).doesNotContain(subjectToUpdate)
    }
}