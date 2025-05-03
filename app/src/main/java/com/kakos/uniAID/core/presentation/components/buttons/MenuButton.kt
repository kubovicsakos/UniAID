package com.kakos.uniAID.core.presentation.components.buttons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
/**
 * Represents a button with a menu icon.
 *
 * This component is used to trigger a menu action.
 *
 * @param modifier Optional [Modifier] to be applied to the button.
 * @param onClick Callback function to be invoked when the button is clicked.
 */
@Composable
fun MenuButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = {
            onClick()
        },
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.Menu,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            contentDescription = "Menu"
        )
    }
}