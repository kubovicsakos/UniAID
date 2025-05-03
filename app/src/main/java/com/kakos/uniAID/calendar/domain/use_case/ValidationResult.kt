package com.kakos.uniAID.calendar.domain.use_case

/**
 * Data class for validation operation results.
 *
 * Encapsulates the outcome of data validation operations,
 * providing success status and optional error details.
 *
 * @property successful Boolean indicating validation passed (true) or failed (false).
 * @property errorMessage Optional message explaining validation failure reasons.
 */
data class ValidationResult(
    val successful: Boolean,
    val errorMessage: String? = null
)
