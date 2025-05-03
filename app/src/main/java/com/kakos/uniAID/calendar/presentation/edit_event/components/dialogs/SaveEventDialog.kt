package com.kakos.uniAID.calendar.presentation.edit_event.components.dialogs

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
import com.kakos.uniAID.calendar.domain.util.SaveOption

/**
 * Composable that displays a dialog for event save options.
 *
 * Presents a modal interface with radio button choices allowing users to
 * select how an event modification should be applied across recurring events,
 * supporting single event, future events, or entire series options.
 *
 * @param onDismiss Callback invoked when dialog is dismissed without selection.
 * @param onSave Callback invoked when user confirms a save option selection.
 */
@Composable
fun SaveEventDialog(
    onDismiss: () -> Unit,
    onSave: (SaveOption) -> Unit
) {
    val (selectedOption, setSelectedOption) = remember { mutableStateOf(SaveOption.THIS) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Save Event") },
        text = {
            Column {
                Text(text = "Choose how you want to save the event:")
                RadioButtonWithLabel(
                    text = "This Event",
                    selected = selectedOption == SaveOption.THIS,
                    onClick = { setSelectedOption(SaveOption.THIS) }
                )
                RadioButtonWithLabel(
                    text = "This and following events",
                    selected = selectedOption == SaveOption.THIS_AND_FUTURE,
                    onClick = { setSelectedOption(SaveOption.THIS_AND_FUTURE) }
                )
                RadioButtonWithLabel(
                    text = "All events",
                    selected = selectedOption == SaveOption.ALL,
                    onClick = { setSelectedOption(SaveOption.ALL) }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(selectedOption) }) {
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

/**
 * Composable that displays a labeled radio button.
 *
 * Combines a radio button with descriptive text in a horizontally aligned layout,
 * providing a selectable option with clear visual feedback of selection state.
 *
 * @param text Label text to display beside the radio button.
 * @param selected Whether this radio button is currently selected.
 * @param onClick Callback invoked when radio button is clicked.
 */
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