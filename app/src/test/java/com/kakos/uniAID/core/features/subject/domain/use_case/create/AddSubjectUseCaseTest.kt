package com.kakos.uniAID.core.features.subject.domain.use_case.create

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.kakos.uniAID.core.features.subject.data.repository.FakeSubjectRepository
import com.kakos.uniAID.core.domain.subject.domain.model.InvalidSubjectException
import com.kakos.uniAID.core.domain.subject.domain.model.Subject
import com.kakos.uniAID.core.domain.subject.domain.use_case.create.AddSubjectUseCase
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddSubjectUseCaseTest {
    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var fakeSubjectRepository: FakeSubjectRepository
    private lateinit var addSubjectUseCase: AddSubjectUseCase

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        fakeSubjectRepository = FakeSubjectRepository()
        addSubjectUseCase = AddSubjectUseCase(fakeSubjectRepository)
        fakeSubjectRepository.shouldHaveFilledList(true)
    }

    @Test
    fun `valid subject is added to repository successfully, added subject is in repository`() =
        runTest {
            val testSubjectId = 10
            val validSubject = Subject(
                id = testSubjectId,
                title = "Test Subject",
                description = "Description of test subject",
                semester = 3,
                credit = 5,
                finalGrade = 4,
                grade = 4
            )

            addSubjectUseCase(validSubject)

            val allSubjects = fakeSubjectRepository.getAllSubjects()
            val insertedSubject = fakeSubjectRepository.getSubjectById(testSubjectId)

            assertThat(allSubjects.first()).contains(validSubject)
            assertThat(allSubjects.first().size).isEqualTo(4)
            assertThat(insertedSubject).isEqualTo(validSubject)
        }


    @Test
    fun `subject with special characters in title is added successfully, added subject is in repository`() =
        runTest {
            val specialTitle = "!@#$%^&*()_+|~=-`{}[]:\";'<>?,./тест한국어"
            val subjectWithSpecialChars = Subject(
                id = 15,
                title = specialTitle,
                description = "Subject with special characters",
                semester = 3,
                credit = 4,
                finalGrade = 5,
                grade = 5
            )

            var exceptionThrown = false
            try {
                addSubjectUseCase(subjectWithSpecialChars)
            } catch (e: InvalidSubjectException) {
                exceptionThrown = true
            }
            assertThat(exceptionThrown).isFalse()

            val allSubjects = fakeSubjectRepository.getAllSubjects().first()
            assertThat(allSubjects).contains(subjectWithSpecialChars)
        }

    @Test
    fun `subject with long description and title is added successfully, added subject is in repository`() =
        runTest {
            val longDescription = "a".repeat(10000)
            val longTitle = "a".repeat(10000)
            val longSubjectId = 16
            val longSubject = Subject(
                id = longSubjectId,
                title = longTitle,
                description = longDescription,
                semester = 4,
                credit = 3,
                finalGrade = 4,
                grade = 4
            )

            addSubjectUseCase(longSubject)

            val allSubjects = fakeSubjectRepository.getAllSubjects().first()
            val insertedSubject = fakeSubjectRepository.getSubjectById(longSubjectId)

            assertThat(allSubjects).contains(longSubject)
            assertThat(allSubjects.size).isEqualTo(4)

            assertThat(insertedSubject).isEqualTo(longSubject)
            assertThat(insertedSubject?.description).isEqualTo(longDescription)
            assertThat(insertedSubject?.title).isEqualTo(longTitle)
        }

    @Test
    fun `add subject to empty repository results in repository with one subject, repository with size 1`() =
        runTest {
            fakeSubjectRepository.shouldHaveFilledList(false)

            val validSubject = Subject(
                id = 17,
                title = "Test Subject",
                description = "Description of test subject",
                semester = 1,
                credit = 5,
                finalGrade = 5,
                grade = 5
            )

            val initialSubjectsList = fakeSubjectRepository.getAllSubjects().first()
            val initialSize = initialSubjectsList.size

            addSubjectUseCase(validSubject)

            val allSubjects = fakeSubjectRepository.getAllSubjects().first()
            assertThat(allSubjects).contains(validSubject)
            assertThat(allSubjects.size).isEqualTo(initialSize + 1)
        }

    @Test
    fun `subject with null id gets assigned a new id, subject added with new id`() = runTest {
        val subjectWithNullId = Subject(
            id = null,
            title = "Test Subject",
            description = "Description of test subject",
            semester = 2,
            credit = 4,
            finalGrade = 4,
            grade = 4
        )

        val newId = addSubjectUseCase(subjectWithNullId)

        val allSubjects = fakeSubjectRepository.getAllSubjects().first()
        assertThat(newId > 0).isTrue()
        assertThat(allSubjects.any { it.id == newId.toInt() }).isTrue()
    }

    @Test
    fun `subject with same id as existing subject overwrites it, repository size unchanged`() =
        runTest {
            val initialSubjects = fakeSubjectRepository.getAllSubjects().first()
            println("Initial Subjects: $initialSubjects")
            val existingId = initialSubjects[0].id
            println("Existing ID: $existingId")

            val updatedSubject = Subject(
                id = existingId,
                title = "Updated Subject",
                description = "Updated description",
                semester = 5,
                credit = 6,
                finalGrade = 3,
                grade = 3
            )

            println("Updated Subject: $updatedSubject")
            addSubjectUseCase(updatedSubject)

            val allSubjects = fakeSubjectRepository.getAllSubjects().first()
            println("All Subjects: $allSubjects")
            println("Size of All Subjects: ${allSubjects.size} | Size of Initial Subjects: ${initialSubjects.size}")
            assertThat(allSubjects.size).isEqualTo(initialSubjects.size)
            assertThat(allSubjects).contains(updatedSubject)
        }

    @Test
    fun `subject with empty title throws exception, InvalidSubjectException`() = runTest {
        val emptyTitleSubject = Subject(
            id = 11,
            title = "",
            description = "Description of empty title subject",
            semester = 2,
            credit = 3,
            finalGrade = 5,
            grade = 4
        )

        var exceptionThrown = false
        try {
            addSubjectUseCase(emptyTitleSubject)
        } catch (e: InvalidSubjectException) {
            exceptionThrown = true
            assertThat(e.message).contains("Title")
        }
        assertThat(exceptionThrown).isTrue()
    }

    @Test
    fun `subject with negative semester throws exception, InvalidSubjectException`() = runTest {
        val negativeSemesterSubject = Subject(
            id = 12,
            title = "Test Subject",
            description = "Description of test subject",
            semester = -1,
            credit = 4,
            finalGrade = 4,
            grade = 5
        )

        var exceptionThrown = false
        try {
            addSubjectUseCase(negativeSemesterSubject)
        } catch (e: InvalidSubjectException) {
            exceptionThrown = true
            assertThat(e.message).contains("Semester")
        }
        assertThat(exceptionThrown).isTrue()
    }

    @Test
    fun `subject with negative credits throws exception, InvalidSubjectException`() = runTest {
        val negativeCreditSubject = Subject(
            id = 13,
            title = "Test Subject",
            description = "Description of test subject",
            semester = 1,
            credit = -3,
            finalGrade = 3,
            grade = 3
        )

        var exceptionThrown = false
        try {
            addSubjectUseCase(negativeCreditSubject)
        } catch (e: InvalidSubjectException) {
            exceptionThrown = true
            assertThat(e.message).contains("Credit")
        }
        assertThat(exceptionThrown).isTrue()
    }

    @Test
    fun `subject with final grade out of range throws exception, InvalidSubjectException`() =
        runTest {
            val invalidGradeSubject = Subject(
                id = 14,
                title = "Test Subject",
                description = "Description of test subject",
                semester = 2,
                credit = 4,
                finalGrade = 6, // Assuming valid range is 1-5
                grade = 4
            )

            var exceptionThrown = false
            try {
                addSubjectUseCase(invalidGradeSubject)
            } catch (e: InvalidSubjectException) {
                exceptionThrown = true
                assertThat(e.message).contains("Final")
            }
            assertThat(exceptionThrown).isTrue()
        }

    @Test
    fun `subject with grade out of range throws exception, InvalidSubjectException`() = runTest {
        val invalidGradeSubject = Subject(
            id = 14,
            title = "Test Subject",
            description = "Description of test subject",
            semester = 2,
            credit = 4,
            finalGrade = 4,
            grade = 6 // Assuming valid range is 1-5
        )

        var exceptionThrown = false
        try {
            addSubjectUseCase(invalidGradeSubject)
        } catch (e: InvalidSubjectException) {
            exceptionThrown = true
            assertThat(e.message).contains("Grade")
        }
        assertThat(exceptionThrown).isTrue()
    }
}