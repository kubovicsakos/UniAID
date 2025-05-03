package com.kakos.uniAID.core.presentation.components.dropdowns

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kakos.uniAID.ui.theme.UniAidTheme
/**
 * A dropdown menu for selecting a number within a specified range.
 *
 * @param lowerBound The minimum number in the range.
 * @param upperBound The maximum number in the range.
 * @param defaultNumber The default number to be displayed when the dropdown is closed.
 * @param modifier Optional [Modifier] to be applied to the dropdown.
 * @param onNumberSelected Callback function to be invoked when a number is selected.
 */
@Composable
fun NumberPickerDropdown(
    lowerBound: Int,
    upperBound: Int,
    defaultNumber: Int,
    modifier: Modifier = Modifier,
    onNumberSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) } // State to control menu visibility
    var selectedNumber by remember(defaultNumber) {
        mutableIntStateOf(defaultNumber)
    }

    val range = (lowerBound..upperBound).toList() // Generate the number range

    Column(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(selectedNumber.toString()) // Display the selected number
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }, // Close dropdown when clicked outside
            modifier = modifier.height(200.dp)
        ) {
            range.forEach { number ->
                DropdownMenuItem(
                    text = { Text(number.toString()) },
                    onClick = {
                        selectedNumber = number // Update the selected number
                        onNumberSelected(number) // Notify the parent about the selection
                        expanded = false // Close the dropdown
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun NumberPickerDropdownP() {
    UniAidTheme(
        themeMode = "Auto"
    ) {
        Surface(
            color = androidx.compose.ui.graphics.Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            NumberPickerDropdown(
                lowerBound = 1,
                upperBound = 5,
                defaultNumber = 3,
                onNumberSelected = {}
            )
        }
    }
}
