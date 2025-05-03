package com.kakos.uniAID.subjectAndStats.presentation.subject.edit_subjects.util

/**
 * Represents UI state for subject editing screen.
 *
 * @property title The state of the title text field, including value and validation errors.
 * @property description The description text of the subject.
 * @property semester The semester to which the subject belongs, can be null if not set.
 * @property credit The number of credits associated with the subject.
 * @property finalGrade The final grade received in the subject.
 * @property grade The current grade in the subject.
 * @property isFormValid Whether the current form data passes validation checks.
 */
data class EditSubjectState(
    val title: SubjectTextFieldState = SubjectTextFieldState(),
    val description: String = "",
    val semester: Int? = null,
    val credit: Int? = 0,
    val finalGrade: Int? = null,
    val grade: Int? = null,
    val isFormValid: Boolean = false
)