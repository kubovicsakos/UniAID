package com.kakos.uniAID.core.presentation.components.buttons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
/**
 * Represents an Edit button with an icon.
 *
 * This component is used to trigger an edit action.
 *
 * @param modifier Optional [Modifier] to be applied to the button.
 * @param onClick Callback function to be invoked when the button is clicked.
 * @param description A string describing the action of the button.
 */
@Composable
fun EditButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    description: String
) {
    IconButton(
        onClick = {
            onClick()
        },
        modifier = modifier,
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Icon(
            imageVector = Icons.Default.Edit,
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = "Edit $description"
        )
    }
}