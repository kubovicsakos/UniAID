package com.kakos.uniAID.core.domain.subject.domain.use_case.validate

import android.util.Log
import com.kakos.uniAID.core.domain.subject.domain.use_case.ValidationResult

/**
 * Use case for validating a title.
 *
 * Encapsulates the logic for validating a title string.
 * @returns ValidationResult based on if the title is blank or not
 */
class ValidateSubjectTitleUseCase {
    private val tag = "ValidateTitleUseCase"
    fun execute(title: String): ValidationResult {
        Log.d(tag, "Title validation started")
        return if (title.isBlank()) {
            Log.e(tag, "Title is invalid")
            ValidationResult(
                successful = false,
                errorMessage = "Title can't be empty"
            )
        } else {
            Log.d(tag, "Title is valid")
            ValidationResult(true)
        }
    }
}