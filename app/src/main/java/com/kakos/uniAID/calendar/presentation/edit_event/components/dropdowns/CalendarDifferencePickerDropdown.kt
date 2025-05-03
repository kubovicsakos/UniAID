package com.kakos.uniAID.calendar.presentation.edit_event.components.dropdowns

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Composable that displays a numerical difference picker dropdown.
 *
 * Provides a button-activated dropdown interface for selecting numeric values
 * from a configurable range, enabling precise numerical input for calendar
 * event configuration through a scrollable, contained selection list.
 *
 * @param modifier Modifier to be applied to the layout.
 * @param selectedNumber The currently selected number displayed on the button.
 * @param onNumberSelected Callback invoked when user selects a number.
 * @param range Range of numbers to display in the dropdown.
 */
@Composable
fun CalendarDifferencePickerDropdown(
    modifier: Modifier = Modifier,
    selectedNumber: Long,
    onNumberSelected: (Long) -> Unit,
    range: LongRange = 1L..99L
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Button(
            onClick = { expanded = true },
            content = { Text(selectedNumber.toString()) }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = modifier.height(200.dp)
        ) {
            range.forEach { number ->
                DropdownMenuItem(
                    text = { Text(number.toString()) },
                    onClick = {
                        onNumberSelected(number)
                        expanded = false
                    }
                )
            }
        }
    }
}