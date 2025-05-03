package com.kakos.uniAID.calendar.domain.use_case.event.validate

import android.util.Log
import com.kakos.uniAID.calendar.domain.use_case.ValidationResult
import java.time.LocalDate
import java.time.LocalTime

/**
 * Use case for validating event date and time constraints.
 *
 * Encapsulates the logic for ensuring event dates and times follow
 * logical sequence rules, handling validation of start/end chronology
 * and special cases for all-day events.
 * @return ValidationResult indicating success or failure of the validation.
 */
class ValidateDateTimeUseCase {

    private val tag = "ValidateDateTimeUseCase"

    fun execute(
        startDate: LocalDate,
        endDate: LocalDate,
        startTime: LocalTime,
        endTime: LocalTime,
        isAllDay: Boolean, // Add all-day flag
    ): ValidationResult {
        if (startDate.isAfter(endDate)) {
            Log.e(tag, "EXCEPTION: Start date is after end date")
            return ValidationResult(false, "Start date can't be after end date")
        }
        // Only check times if not all-day
        if (!isAllDay && startDate == endDate && startTime.isAfter(endTime)) {
            Log.e(tag, "EXCEPTION: Start time is after end time")
            return ValidationResult(false, "Start time can't be after end time")
        }
        Log.d(tag, "Validation successful, returning true")
        return ValidationResult(true)
    }
}