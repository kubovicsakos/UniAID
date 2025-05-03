package com.kakos.uniAID.core.presentation.components.buttons.floating_button

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
/**
 * Represents a Floating Action Button (FAB) for adding items.
 *
 * This component is used to trigger an action, typically to add a new item.
 *
 * @param modifier Optional [Modifier] to be applied to the FAB.
 * @param onClick Callback function to be invoked when the FAB is clicked.
 * @param description A string describing the action of the FAB.
 */
@Composable
fun SaveFloatingActionButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    description: String,
) {
    FloatingActionButton(
        modifier = modifier,
        onClick = {
            onClick()
        },
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            contentDescription = "Save $description"
        )
    }
}