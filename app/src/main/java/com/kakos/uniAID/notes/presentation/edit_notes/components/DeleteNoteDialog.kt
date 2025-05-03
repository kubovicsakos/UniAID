package com.kakos.uniAID.notes.presentation.edit_notes.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.kakos.uniAID.calendar.domain.util.DeleteOption

/**
 * Composable that displays note deletion confirmation dialog.
 *
 * @param onDismiss Callback invoked when the dialog is dismissed.
 * @param onDelete Callback invoked when user confirms deletion, receives a DeleteOption.
 */
@Composable
fun DeleteNoteDialog(
    onDismiss: () -> Unit,
    onDelete: (DeleteOption) -> Unit
) {
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
}