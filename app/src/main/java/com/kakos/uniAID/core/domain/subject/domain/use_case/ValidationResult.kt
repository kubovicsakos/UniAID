package com.kakos.uniAID.core.domain.subject.domain.use_case

/**
 * Represents the result of a validation operation.
 *
 * @property successful `true` if the validation was successful, `false` otherwise.
 * @property errorMessage An optional error message describing why the validation failed.
 */
data class ValidationResult(
    val successful: Boolean,
    val errorMessage: String? = null
)