package com.kakos.uniAID.subjectAndStats.presentation.stats.statistics.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

/**
 * Composable that displays an information dialog.
 *
 * Presents a simple dialog with a title, description, and dismissal button
 * to provide contextual information to the user.
 *
 * @param title The title text displayed at the top of the dialog.
 * @param description The informational text displayed in the dialog body.
 * @param onDismiss Callback to execute when the dialog is dismissed.
 */
@Composable
fun InfoDialog(
    title: String,
    description: String,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = { Text(text = description) },
        confirmButton = {
            TextButton(
                onClick = { onDismiss() }
            ) {
                Text("Okay")
            }
        }
    )
}