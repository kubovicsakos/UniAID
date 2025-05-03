package com.kakos.uniAID.core.presentation.components.buttons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
/**
 * Represents a Cancel button with an icon.
 *
 * This component is used to trigger a cancel action.
 *
 * @param modifier Optional [Modifier] to be applied to the button.
 * @param onClick Callback function to be invoked when the button is clicked.
 */
@Composable
fun CancelButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    IconButton(onClick = {
        onClick()
    }) {
        Icon(
            imageVector = Icons.Default.Close,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            contentDescription = "Back",
            modifier = modifier
        )
    }
}