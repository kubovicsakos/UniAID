package com.kakos.uniAID.calendar.domain.use_case.event.validate

import android.util.Log
import com.kakos.uniAID.calendar.domain.use_case.ValidationResult

/**
 * Use case for validating event title input.
 *
 * Encapsulates the logic for ensuring event titles contain valid content,
 * handling validation that titles are not empty or blank.
 * @return ValidationResult indicating success or failure of the validation.
 */
class ValidateTitleUseCase {

    private val tag = "ValidateTitleUseCase"

    fun execute(title: String): ValidationResult {
        return if (title.isBlank()) {
            Log.e(tag, "EXCEPTION: Title can't be empty")
            ValidationResult(
                successful = false,
                errorMessage = "Title can't be empty"
            )
        } else {
            Log.d(tag, "Validation successful, returning true")
            ValidationResult(true)
        }
    }
}