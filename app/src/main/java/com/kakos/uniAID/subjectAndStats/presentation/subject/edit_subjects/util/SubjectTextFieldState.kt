package com.kakos.uniAID.subjectAndStats.presentation.subject.edit_subjects.util

/**
 * Represents UI state for a subject input text field.
 *
 * @property text The current text value entered in the field.
 * @property error Optional validation error message when text fails validation criteria.
 */
data class SubjectTextFieldState(
    val text: String = "",
    val error: String? = null,
)