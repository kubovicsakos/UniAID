package com.kakos.uniAID.subjectAndStats.presentation.subject.subject_details.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

/**
 * Dialog for confirming subject deletion.
 *
 * @param onDismiss Callback when dialog is dismissed or canceled.
 * @param onDelete Callback when deletion is confirmed.
 */
@Composable
fun DeleteSubjectDialog(
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Delete Subject") },
        text = { Text(text = "Delete this Subject?") },
        confirmButton = {
            TextButton(
                onClick = { onDelete() }
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}