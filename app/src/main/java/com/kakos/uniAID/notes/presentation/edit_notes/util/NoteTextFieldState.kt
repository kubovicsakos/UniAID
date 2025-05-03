package com.kakos.uniAID.notes.presentation.edit_notes.util

/**
 * Represents UI state for note text field component.
 *
 * @property text The current text content of the field.
 * @property hint The placeholder text shown when field is empty.
 * @property isHintVisible Whether the hint should currently be displayed.
 */
data class NoteTextFieldState(
    val text: String = "",
    val hint: String = "",
    val isHintVisible: Boolean = true
)
