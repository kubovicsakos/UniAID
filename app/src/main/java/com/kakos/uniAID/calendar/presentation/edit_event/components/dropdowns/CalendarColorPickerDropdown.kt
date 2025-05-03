package com.kakos.uniAID.calendar.presentation.edit_event.components.dropdowns

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kakos.uniAID.calendar.domain.model.Event

/**
 * Composable that displays a color picker dropdown for event color styling.
 *
 * Provides a circular color preview that expands to reveal available color options,
 * enabling users to select from predefined event colors with associated labels
 * for consistent visual categorization of calendar events.
 *
 * @param modifier Modifier to be applied to the layout.
 * @param selectedColor Index of the currently selected color in the event colors list.
 * @param onColorSelected Callback invoked when user selects a new color.
 */
@Composable
fun CalendarColorPickerDropdown(
    modifier: Modifier = Modifier,
    selectedColor: Int,
    onColorSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val colors = Event.eventColors

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(Event.eventColors[selectedColor].first.value))
                .clickable { expanded = true }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = modifier.height(200.dp)
        ) {
            colors.forEachIndexed { index, (color, name) ->
                DropdownMenuItem(
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                    },
                    text = { Text(name) },
                    onClick = {
                        onColorSelected(index)
                        expanded = false
                    }
                )
            }
        }
    }
}