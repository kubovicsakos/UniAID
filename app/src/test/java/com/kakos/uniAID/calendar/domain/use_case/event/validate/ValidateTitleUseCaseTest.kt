package com.kakos.uniAID.calendar.domain.use_case.event.validate

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ValidateTitleUseCaseTest {
    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var validateTitleUseCase: ValidateTitleUseCase

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        validateTitleUseCase = ValidateTitleUseCase()
    }

    @Test
    fun `empty title returns unsuccessful, result with error message`() {
        val result = validateTitleUseCase.execute("")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).contains("Title can't be empty")
    }

    @Test
    fun `blank title returns unsuccessful, result with error message`() {
        val result = validateTitleUseCase.execute("   ")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).contains("Title can't be empty")
    }

    @Test
    fun `title with newline and tab character, returns successful result without error message`() {
        val result = validateTitleUseCase.execute("\tMeeting\n")

        assertThat(result.successful).isTrue()
    }


    @Test
    fun `valid title, returns successful result`() {
        val result = validateTitleUseCase.execute("Meeting")

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }

    @Test
    fun `title with special characters, returns successful result`() {
        val result = validateTitleUseCase.execute("Meeting #123 @Office")

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }

    @Test
    fun `title with only one character, returns successful result`() {
        val result = validateTitleUseCase.execute("X")

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }

    @Test
    fun `title with whitespace only at beginning or end, returns successful result`() {
        val result = validateTitleUseCase.execute(" Meeting ")

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }

}