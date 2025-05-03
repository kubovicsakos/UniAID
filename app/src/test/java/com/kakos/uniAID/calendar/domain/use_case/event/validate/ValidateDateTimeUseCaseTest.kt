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
import java.time.LocalTime

class ValidateDateTimeUseCaseTest {
    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var validateDateTimeUseCase: ValidateDateTimeUseCase

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        validateDateTimeUseCase = ValidateDateTimeUseCase()
    }

    @Test
    fun `start date after end date, returns unsuccessful result with error message`() {
        // Use case with startDate after endDate
        val startDate = LocalDate.now().plusDays(1)
        val endDate = LocalDate.now()
        val startTime = LocalTime.of(10, 0)
        val endTime = LocalTime.of(11, 0)

        val result = validateDateTimeUseCase.execute(startDate, endDate, startTime, endTime, false)

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).contains("Start date")
    }

    @Test
    fun `same date but start time after end time, returns unsuccessful result with error message`() {
        val date = LocalDate.now()
        val startTime = LocalTime.of(12, 0)
        val endTime = LocalTime.of(10, 0)

        val result = validateDateTimeUseCase.execute(date, date, startTime, endTime, false)

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).contains("Start time")
    }

    @Test
    fun `same date and time for start and end, returns successful result`() {
        val date = LocalDate.now()
        val time = LocalTime.of(10, 0)

        val result = validateDateTimeUseCase.execute(date, date, time, time, false)

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }

    @Test
    fun `valid date and time with start before end, returns successful result`() {
        val startDate = LocalDate.now()
        val endDate = LocalDate.now().plusDays(1)
        val startTime = LocalTime.of(10, 0)
        val endTime = LocalTime.of(11, 0)

        val result = validateDateTimeUseCase.execute(startDate, endDate, startTime, endTime, false)

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }

    @Test
    fun `all day event ignores time validation, returns successful result`() {
        val startDate = LocalDate.now()
        val endDate = LocalDate.now().plusDays(1)
        val startTime = LocalTime.of(12, 0)  // Would be invalid if not all-day
        val endTime = LocalTime.of(10, 0)    // Would be invalid if not all-day

        val result = validateDateTimeUseCase.execute(startDate, endDate, startTime, endTime, true)

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }

    @Test
    fun `same date with valid times, returns successful result`() {
        val date = LocalDate.now()
        val startTime = LocalTime.of(10, 0)
        val endTime = LocalTime.of(11, 0)

        val result = validateDateTimeUseCase.execute(date, date, startTime, endTime, false)

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }

    @Test
    fun `start date before end date with start time after end time, returns successful result`() {
        val startDate = LocalDate.now()
        val endDate = LocalDate.now().plusDays(1)
        val startTime = LocalTime.of(12, 0)
        val endTime = LocalTime.of(10, 0)

        val result = validateDateTimeUseCase.execute(startDate, endDate, startTime, endTime, false)

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }
}