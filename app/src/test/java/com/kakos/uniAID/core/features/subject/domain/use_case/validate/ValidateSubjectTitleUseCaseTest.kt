package com.kakos.uniAID.core.features.subject.domain.use_case.validate

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.kakos.uniAID.core.domain.subject.domain.use_case.validate.ValidateSubjectTitleUseCase
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ValidateSubjectTitleUseCaseTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var validateSubjectTitleUseCase: ValidateSubjectTitleUseCase

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        validateSubjectTitleUseCase = ValidateSubjectTitleUseCase()
    }

    @Test
    fun `valid title returns successful validation result, true`() {
        val title = "Valid Title"

        val result = validateSubjectTitleUseCase.execute(title)

        assertThat(result.successful).isTrue()
    }

    @Test
    fun `empty title returns unsuccessful validation result, false`() {
        val title = ""

        val result = validateSubjectTitleUseCase.execute(title)

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).contains("Title can't be empty")
    }

    @Test
    fun `blank title with only spaces returns unsuccessful validation result, false`() {
        val title = "   "

        val result = validateSubjectTitleUseCase.execute(title)

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).contains("Title can't be empty")
    }

    @Test
    fun `title with only newlines and tabs returns unsuccessful validation result, false`() {
        val title = "\n\t\r"

        val result = validateSubjectTitleUseCase.execute(title)

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).contains("Title can't be empty")
    }

    @Test
    fun `title with special characters returns successful validation result, true`() {
        val specialTitle = "!@#$%^&*()_+|~=-`{}[]:\";'<>?,./тест한국어"

        val result = validateSubjectTitleUseCase.execute(specialTitle)

        assertThat(result.successful).isTrue()
    }

    @Test
    fun `title with leading and trailing spaces but non-blank content returns successful validation result, true`() {
        val title = "  Valid Subject Title  "

        val result = validateSubjectTitleUseCase.execute(title)

        assertThat(result.successful).isTrue()
    }

    @Test
    fun `title with leading and trailing newlines and tabs but non-blank content returns successful validation result, true`() {
        val title = "\n\t\rValid Subject Title\n\t\r"

        val result = validateSubjectTitleUseCase.execute(title)

        assertThat(result.successful).isTrue()
    }

    @Test
    fun `very big title returns successful validation result, true`() {
        val bigTitle = "a".repeat(100000)

        val result = validateSubjectTitleUseCase.execute(bigTitle)

        assertThat(result.successful).isTrue()
    }
}