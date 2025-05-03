package com.kakos.uniAID.calendar.presentation.edit_event.components.dialogs


import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import java.time.LocalTime

/**
 * Composable that displays a time picker dialog.
 *
 * Provides a configurable time selection interface with support for both
 * 24-hour and AM/PM formats, allowing users to select hours and minutes
 * through an interactive dial or input interface based on device orientation.
 *
 * @param selectedTime The initially selected time to populate the picker.
 * @param is24HourFormat Whether to use 24-hour format instead of AM/PM.
 * @param onConfirm Callback invoked when user confirms time selection.
 * @param onDismiss Callback invoked when dialog is dismissed.
 * @param isLandScape Whether to optimize layout for landscape orientation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarTimePickerDialog(
    selectedTime: LocalTime,
    is24HourFormat: Boolean = true,
    onConfirm: (LocalTime) -> Unit,
    onDismiss: () -> Unit,
    isLandScape: Boolean = false
) {
    // Extract initial hour and minute from selectedTime or use the current time
    //val currentTime = LocalTime.now()
    val initialHour = selectedTime.hour
    val initialMinute = selectedTime.minute

    // Create a TimePickerState with initial hour and minute
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = is24HourFormat
    )

    TimePickerDialog(
        onDismiss = onDismiss,
        onConfirm = {
            // Convert the TimePickerState to LocalTime
            val selectedLocalTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
            onConfirm(selectedLocalTime)
        }
    ) {
        if (isLandScape) {
            TimeInput(state = timePickerState)
        } else {
            TimePicker(state = timePickerState)
        }
    }
}


/**
 * A composable function that displays a time picker dialog.
 *
 * This function provides a basic dialog structure with dismiss and confirm buttons,
 * allowing the user to select a time. The actual time selection UI is provided
 * through the `content` parameter.
 *
 * @param onDismiss Callback invoked when the user dismisses the dialog (e.g., by tapping outside or pressing the dismiss button).
 * @param onConfirm Callback invoked when the user confirms the time selection by pressing the confirm button.
 * @param content Composable content representing the time picker UI that will be displayed within the dialog.
 *                This should contain the actual UI elements for the user to select the time (e.g., a time picker wheel).
 *
 */
@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Dismiss")
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text("OK")
            }
        },
        text = { content() }
    )
}

@Preview
@Composable
private fun Time() {
    CalendarTimePickerDialog(
        selectedTime = LocalTime.now(),
        onConfirm = { },
        onDismiss = { },
        isLandScape = true
    )
}



