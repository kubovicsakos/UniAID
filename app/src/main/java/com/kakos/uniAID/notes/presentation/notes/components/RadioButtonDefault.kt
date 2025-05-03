package com.kakos.uniAID.notes.presentation.notes.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Composable that displays a radio button with text label.
 *
 * @param text Text label displayed next to the radio button.
 * @param select Whether the radio button is currently selected.
 * @param onSelect Callback triggered when the radio button is clicked.
 * @param modifier Modifier for styling and layout adjustments.
 */
@Composable
fun RadioButtonDefault(
    text: String,
    select: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = select,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary,
                unselectedColor = MaterialTheme.colorScheme.onBackground
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}