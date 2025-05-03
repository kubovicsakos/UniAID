package com.kakos.uniAID.notes.domain.use_case.note.validate

import android.util.Log
import com.kakos.uniAID.notes.domain.util.ValidationResult


/**
 * Use case for validating a note title.
 *
 * Encapsulates the logic for validating a note title string.
 * @return [ValidationResult] object with the result of the validation.
 */
class ValidateNoteTitleUseCase {
    private val tag = "ValidateNoteTitleUseCase"

    fun execute(title: String): ValidationResult {
        Log.d(tag, "Note title validation started")
        return if (title.isBlank()) {
            Log.e(tag, "Note title is invalid")
            ValidationResult(successful = false)
        } else {
            Log.d(tag, "Note title is valid")
            ValidationResult(successful = true)
        }
    }
}