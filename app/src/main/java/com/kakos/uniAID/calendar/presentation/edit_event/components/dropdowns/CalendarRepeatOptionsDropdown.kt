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
import com.kakos.uniAID.calendar.domain.model.Repeat

/**
 * Composable that displays a repeat option dropdown for event recurrence.
 *
 * Provides a button-activated dropdown interface for selecting event recurrence patterns,
 * enabling users to configure how events repeat through time using standard calendar
 * intervals including daily, weekly, monthly, and yearly options.
 *
 * @param modifier Modifier to be applied to the layout.
 * @param selectedRepeat The currently selected repeat option to display.
 * @param onRepeatSelected Callback invoked when user selects a repeat option.
 */
@Composable
fun CalendarRepeatOptionsDropdown(
    modifier: Modifier = Modifier,
    selectedRepeat: Repeat,
    onRepeatSelected: (Repeat) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val options = listOf(Repeat.NONE, Repeat.DAILY, Repeat.WEEKLY, Repeat.MONTHLY, Repeat.YEARLY)

    Box(modifier = modifier) {
        Button(
            onClick = { expanded = true },
            content = { Text(selectedRepeat.name.lowercase()) }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = modifier.height(200.dp)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.name.lowercase()) },
                    onClick = {
                        onRepeatSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}