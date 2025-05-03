package com.kakos.uniAID.settings.presentation.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

/**
 * Composable that displays a date picker dialog for semester date selection.
 *
 * @param selectedDate Initially selected date, or null if no date is selected.
 * @param onDateSelected Callback triggered when user selects a date.
 * @param onDismiss Callback triggered when the dialog is dismissed.
 * @param isLandScape Whether to display the dialog in landscape mode.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SemesterDatePickerDialog(
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate?) -> Unit,
    onDismiss: () -> Unit,
    isLandScape: Boolean = false
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
        }
    ) {
        DatePicker(state = datePickerState)
    }
}