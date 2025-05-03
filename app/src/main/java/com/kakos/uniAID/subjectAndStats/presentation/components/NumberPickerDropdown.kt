package com.kakos.uniAID.subjectAndStats.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kakos.uniAID.ui.theme.UniAidTheme

/**
 * A composable function that provides a dropdown menu for selecting a number within a specified range,
 * or the option to select "None".
 *
 * @param lowerBound The lower bound of the selectable number range (inclusive).
 * @param upperBound The upper bound of the selectable number range (inclusive).
 * @param defaultNumber The initially selected number. If null, "None" is initially selected.
 * @param onValueChange A callback function invoked when the selected number changes. It provides the newly selected number (or null if "None" is selected).
 * @param modifier Modifier for styling and layout.
 */
@Composable
fun NumberPickerDropdown(
    modifier: Modifier = Modifier,
    lowerBound: Int,
    upperBound: Int,
    defaultNumber: Int?,
    hasNullOption: Boolean = true,
    onValueChange: (Int?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedNumber by remember { mutableStateOf(defaultNumber) }

    val range: List<Int?> =
        if (hasNullOption) {
            listOf(null) + (lowerBound..upperBound).toList()
        } else {
            (lowerBound..upperBound).toList()
        }


    Column(modifier = modifier) {
        Button(
            onClick = { expanded = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ),
            modifier = modifier
        ) {
            Text(if (selectedNumber == null) "?" else selectedNumber.toString())
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = modifier.height(200.dp)
        ) {
            range.forEach { number ->
                DropdownMenuItem(
                    text = { Text(number?.toString() ?: "None") },
                    onClick = {
                        selectedNumber = number
                        onValueChange(number)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun NumberPickerDropdownP() {
    UniAidTheme {
        Surface(
            color = androidx.compose.ui.graphics.Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            NumberPickerDropdown(
                lowerBound = 1,
                upperBound = 5,
                defaultNumber = 3,
                onValueChange = {},
            )
        }
    }
}
