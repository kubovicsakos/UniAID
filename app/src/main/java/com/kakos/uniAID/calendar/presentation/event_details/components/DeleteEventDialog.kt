package com.kakos.uniAID.calendar.presentation.event_details.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kakos.uniAID.calendar.domain.util.DeleteOption

/**
 * Composable that displays a radio button with an accompanying text label.
 *
 * @param text Text label displayed next to the radio button.
 * @param selected Whether the radio button is currently selected.
 * @param onClick Callback triggered when the radio button is clicked.
 */
@Composable
fun DeleteEventDialog(
    singleEvent: Boolean,
    onDismiss: () -> Unit,
    onDelete: (DeleteOption) -> Unit
) {
    if (singleEvent) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = "Delete Event") },
            text = { Text(text = "Delete this event?") },
            confirmButton = {
                TextButton(onClick = { onDelete(DeleteOption.THIS) }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    } else {
        val (selectedOption, setSelectedOption) = remember { mutableStateOf(DeleteOption.THIS) }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = "Delete Event") },
            text = {
                Column {
                    Text(text = "Choose how you want to delete the event:")
                    RadioButtonWithLabel(
                        text = "This Event",
                        selected = selectedOption == DeleteOption.THIS,
                        onClick = { setSelectedOption(DeleteOption.THIS) }
                    )
                    RadioButtonWithLabel(
                        text = "This and following events",
                        selected = selectedOption == DeleteOption.THIS_AND_FUTURE,
                        onClick = { setSelectedOption(DeleteOption.THIS_AND_FUTURE) }
                    )
                    RadioButtonWithLabel(
                        text = "All events",
                        selected = selectedOption == DeleteOption.ALL,
                        onClick = { setSelectedOption(DeleteOption.ALL) }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { onDelete(selectedOption) }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun RadioButtonWithLabel(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors()
        )
        Text(text = text, modifier = Modifier.padding(start = 8.dp))
    }
}