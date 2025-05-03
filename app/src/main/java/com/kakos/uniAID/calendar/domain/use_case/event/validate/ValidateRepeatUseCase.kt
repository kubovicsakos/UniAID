package com.kakos.uniAID.calendar.domain.use_case.event.validate

import android.util.Log
import com.kakos.uniAID.calendar.domain.use_case.ValidationResult
import java.time.LocalDate

/**
 * Use case for validating repeating event constraints.
 *
 * Encapsulates the logic for ensuring repeat parameters follow
 * chronological requirements, validating that end dates occur
 * after start dates for recurring events.
 * @return ValidationResult indicating success or failure of the validation.
 */
class ValidateRepeatUseCase {

    private val tag = "ValidateRepeatUseCase"

    fun execute(
        startDate: LocalDate,
        repeatEndDate: LocalDate
    ): ValidationResult {
        if (repeatEndDate.isBefore(startDate)) {
            Log.e(tag, "EXCEPTION: Repeat end date is before start date")
            return ValidationResult(false, "Repeat end date can't be before start date")
        }
        Log.d(tag, "Validation successful, returning true")
        return ValidationResult(true)
    }
}