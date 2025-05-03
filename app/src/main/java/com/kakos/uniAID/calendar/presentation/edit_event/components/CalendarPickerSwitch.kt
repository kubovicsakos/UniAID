package com.kakos.uniAID.calendar.presentation.edit_event.components

import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Composable that displays a toggle switch for boolean values.
 *
 * Provides a simple switch control for toggling between different calendar booleans values
 *
 * @param modifier Modifier to be applied to the layout.
 * @param checked Whether the switch is currently in the on state.
 * @param onCheckedChange Callback invoked when switch checked state changes.
 */
@Composable
fun CalenderPickerSwitch(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Switch(
        modifier = modifier,
        checked = checked,
        onCheckedChange = {
            onCheckedChange(it)
        }
    )
}