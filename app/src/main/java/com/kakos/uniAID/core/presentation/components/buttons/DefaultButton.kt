package com.kakos.uniAID.core.presentation.components.buttons


import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
/**
 * Represents a default button with a text label.
 *
 * This component is used to trigger an action when clicked.
 *
 * @param modifier Optional [Modifier] to be applied to the button.
 * @param text The text label to be displayed on the button.
 * @param onClick Callback function to be invoked when the button is clicked.
 */
@Composable
fun DefaultButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = {
            onClick()
        },
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}