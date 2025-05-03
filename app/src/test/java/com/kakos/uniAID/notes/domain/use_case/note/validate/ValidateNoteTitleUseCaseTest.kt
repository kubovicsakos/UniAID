package com.kakos.uniAID.notes.domain.use_case.note.validate

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ValidateNoteTitleUseCaseTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var validateNoteTitleUseCase: ValidateNoteTitleUseCase

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        validateNoteTitleUseCase = ValidateNoteTitleUseCase()
    }

    @Test
    fun `valid title returns successful validation result, true`() {
        val title = "Valid Title"

        val result = validateNoteTitleUseCase.execute(title)

        assertThat(result.successful).isTrue()
    }

    @Test
    fun `empty title returns unsuccessful validation result, false`() {
        val title = ""

        val result = validateNoteTitleUseCase.execute(title)

        assertThat(result.successful).isFalse()
    }

    @Test
    fun `blank title with only spaces returns unsuccessful validation result, false`() {
        val title = "   "

        val result = validateNoteTitleUseCase.execute(title)

        assertThat(result.successful).isFalse()
    }

    @Test
    fun `title with only newlines and tabs returns unsuccessful validation result, false`() {
        val title = "\n\t\r"

        val result = validateNoteTitleUseCase.execute(title)

        assertThat(result.successful).isFalse()
    }

    @Test
    fun `very long title returns successful validation result, true`() {
        val longTitle = "a".repeat(1000)

        val result = validateNoteTitleUseCase.execute(longTitle)

        assertThat(result.successful).isTrue()
    }

    @Test
    fun `title with special characters returns successful validation result, true`() {
        val specialTitle = "!@#$%^&*()_+|~=-`{}[]:\";'<>?,./тест한국어"

        val result = validateNoteTitleUseCase.execute(specialTitle)

        assertThat(result.successful).isTrue()
    }

    @Test
    fun `title with leading and trailing spaces but non-blank content returns successful validation result, true`() {
        val title = "  Valid Title with spaces  "

        val result = validateNoteTitleUseCase.execute(title)

        assertThat(result.successful).isTrue()
    }

    @Test
    fun `title with leading and trailing newlines and tabs but non-blank content returns successful validation result, true`() {
        val title = "\n\t\rValid Title with newlines and tabs\n\t\r"

        val result = validateNoteTitleUseCase.execute(title)

        assertThat(result.successful).isTrue()
    }

    @Test
    fun `very big title returns successful validation result, true`() {
        val bigTitle = "a".repeat(100000)

        val result = validateNoteTitleUseCase.execute(bigTitle)

        assertThat(result.successful).isTrue()
    }
}