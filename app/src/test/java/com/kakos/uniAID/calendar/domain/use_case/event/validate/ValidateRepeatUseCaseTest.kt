package com.kakos.uniAID.calendar.domain.use_case.event.validate

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class ValidateRepeatUseCaseTest {
    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var validateRepeatUseCase: ValidateRepeatUseCase


    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        validateRepeatUseCase = ValidateRepeatUseCase()
    }

    @Test
    fun `valid start date and end date, returns successful result`() {
        val startDate = LocalDate.now()
        val endDate = LocalDate.now().plusDays(7)

        val result = validateRepeatUseCase.execute(startDate, endDate)

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }

    @Test
    fun `end date before start date returns unsuccessful, result with error message`() {
        val startDate = LocalDate.now()
        val endDate = LocalDate.now().minusDays(1)

        val result = validateRepeatUseCase.execute(startDate, endDate)

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).contains("Repeat end date")
    }

    @Test
    fun `same date for start and end, returns successful result`() {
        val date = LocalDate.now()

        val result = validateRepeatUseCase.execute(date, date)

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }

    @Test
    fun `far future end date, returns successful result`() {
        val startDate = LocalDate.now()
        val endDate = LocalDate.now().plusYears(10)

        val result = validateRepeatUseCase.execute(startDate, endDate)

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }
}