package com.kakos.uniAID.core.presentation.components.dropdowns

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
/**
 * Represents a dropdown menu for selecting an item from a list of options.
 *
 * This component allows users to select an item from a predefined list of options.
 *
 * @param title The title to be displayed on the dropdown button.
 * @param options The list of options to be displayed in the dropdown menu.
 * @param onOptionSelected Callback function to be invoked when an option is selected.
 * @param modifier Optional [Modifier] to be applied to the dropdown menu.
 */
@Composable
fun TextItemPickerDropdown(
    title: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var displayedTitle by remember { mutableStateOf(title) }

    // Update displayed title when parent changes it
    LaunchedEffect(title) {
        displayedTitle = title
    }

    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(displayedTitle)
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = modifier
                .defaultMinSize(minHeight = 10.dp, minWidth = 0.dp)
                .height(200.dp)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        displayedTitle = option
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}