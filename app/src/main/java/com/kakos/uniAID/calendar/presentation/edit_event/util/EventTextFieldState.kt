package com.kakos.uniAID.calendar.presentation.edit_event.util

/**
 * Represents state for text input fields in event forms.
 *
 * Encapsulates input text value, placeholder hint text, and validation
 * error messages for tracking and displaying form field status within
 * the event editing interface.
 *
 * @property text Current text content entered by the user.
 * @property hint Placeholder text displayed when field is empty.
 * @property error Optional validation error message when input is invalid.
 */
data class EventTextFieldState(
    val text: String = "",
    val hint: String = "",
    val error: String? = null,
)