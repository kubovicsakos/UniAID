package com.kakos.uniAID.core.presentation.components.buttons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
/**
 * Represents a Delete button with an icon.
 *
 * This component is used to trigger a delete action.
 *
 * @param modifier Optional [Modifier] to be applied to the button.
 * @param onClick Callback function to be invoked when the button is clicked.
 * @param description A string describing the action of the button.
 */
@Composable
fun DeleteButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    description: String
) {
    IconButton(
        onClick = {
            onClick()
        },
        modifier = modifier, // Ensure the modifier is used here
        colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = "Delete $description"
        )
    }
}