package com.kakos.uniAID.core.features.subject.domain.use_case.read

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.kakos.uniAID.core.features.subject.data.repository.FakeSubjectRepository
import com.kakos.uniAID.core.domain.subject.domain.use_case.read.GetSubjectByIdUseCase
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetSubjectByIdUseCaseTest {
    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var fakeSubjectRepository: FakeSubjectRepository
    private lateinit var getSubjectByIdUseCase: GetSubjectByIdUseCase

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        fakeSubjectRepository = FakeSubjectRepository()
        getSubjectByIdUseCase = GetSubjectByIdUseCase(fakeSubjectRepository)
        fakeSubjectRepository.shouldHaveFilledList(true)
    }

    @Test
    fun `get existing subject by id returns correct subject`() = runTest {
        val subjectId = 1

        val subject = getSubjectByIdUseCase(subjectId)

        assertThat(subject).isNotNull()
        assertThat(subject?.id).isEqualTo(subjectId)
        assertThat(subject?.title).isEqualTo("a")
    }

    @Test
    fun `get non-existent subject by id returns null`() = runTest {
        val nonExistentId = 999

        val subject = getSubjectByIdUseCase(nonExistentId)

        assertThat(subject).isNull()
    }

    @Test
    fun `get subject with zero id throws IllegalArgumentException`() = runTest {
        val invalidId = 0

        var exceptionThrown = false
        try {
            getSubjectByIdUseCase(invalidId)
        } catch (e: IllegalArgumentException) {
            exceptionThrown = true
            assertThat(e.message).contains("Invalid subject id")
        }

        assertThat(exceptionThrown).isTrue()
    }

    @Test
    fun `get subject with negative id throws IllegalArgumentException`() = runTest {
        val invalidId = -1

        var exceptionThrown = false
        try {
            getSubjectByIdUseCase(invalidId)
        } catch (e: IllegalArgumentException) {
            exceptionThrown = true
            assertThat(e.message).contains("Invalid subject id")
        }

        assertThat(exceptionThrown).isTrue()
    }

    @Test
    fun `deleted subject no longer exists in repository`() = runTest {
        val subjectId = 2

        // First verify the subject exists
        val initialSubject = getSubjectByIdUseCase(subjectId)
        assertThat(initialSubject).isNotNull()

        // Delete the subject
        fakeSubjectRepository.deleteSubjectById(subjectId)

        // Try to get the subject - should return null
        val subjectAfterDeletion = getSubjectByIdUseCase(subjectId)
        assertThat(subjectAfterDeletion).isNull()
    }
}