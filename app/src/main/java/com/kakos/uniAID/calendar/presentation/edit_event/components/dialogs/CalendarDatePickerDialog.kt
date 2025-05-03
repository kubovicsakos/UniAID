package com.kakos.uniAID.calendar.presentation.edit_event.components.dialogs

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kakos.uniAID.ui.theme.UniAidTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset


/**
 * Composable that displays a calendar date picker dialog.
 *
 * Provides a configurable date selection interface with support for landscape
 * and portrait orientations, allowing users to browse and select dates through
 * a calendar interface with confirmation controls.
 *
 * @param selectedDate The currently selected date to initialize the picker with.
 * @param onDateSelected Callback invoked when user confirms a date selection.
 * @param isLandScape Whether to optimize layout for landscape orientation.
 * @param onDismiss Callback invoked when the dialog is dismissed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarDatePickerDialog(
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate?) -> Unit,
    isLandScape: Boolean = false,
    onDismiss: () -> Unit
) {
    // Convert LocalDate to milliseconds explicitly in the correct time zone
    val initialDateMillis = selectedDate?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()

    // Initialize the DatePickerState with the corrected initial date
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDateMillis,
        initialDisplayMode = if (isLandScape) DisplayMode.Input else DisplayMode.Picker
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                // Convert the selected timestamp to LocalDate
                val selectedDateMillis = datePickerState.selectedDateMillis
                val selectedLocalDate = selectedDateMillis?.let {
                    Instant.ofEpochMilli(it)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                }
                onDateSelected(selectedLocalDate)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .fillMaxHeight(0.8f)
    ) {
        DatePicker(
            state = datePickerState,
            showModeToggle = !isLandScape,
        )
    }
}


@Preview
@Composable
private fun CalendarDatePickerDialogP() {
    UniAidTheme {
        Surface(
            color = androidx.compose.ui.graphics.Color.White
        ) {
            CalendarDatePickerDialog(
                onDateSelected = { },
                onDismiss = { },
                selectedDate = LocalDate.now()
            )
        }
    }
}