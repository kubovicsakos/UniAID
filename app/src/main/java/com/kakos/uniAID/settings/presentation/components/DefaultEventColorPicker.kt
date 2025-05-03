package com.kakos.uniAID.settings.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kakos.uniAID.core.presentation.components.dropdowns.TextItemPickerDropdown

/**
 * Composable that displays a default event color selection dropdown.
 *
 * Allows users to select from predefined color options with both bright
 * and dark variants of various colors.
 *
 * @param selectedColor Integer index of the currently selected color (0-15).
 * @param onColorSelected Callback triggered when user selects a new color index.
 * @param modifier Optional modifier for customizing the component layout.
 */
@Composable
fun DefaultEventColorPicker(
    selectedColor: Int,
    onColorSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    val colors = listOf(
        "Green Bright" to 0,
        "Green Dark" to 1,
        "Blue Bright" to 2,
        "Blue Dark" to 3,
        "Orange Bright" to 4,
        "Orange Dark" to 5,
        "Red Bright" to 6,
        "Red Dark" to 7,
        "Purple Bright" to 8,
        "Purple Dark" to 9,
        "Pink Bright" to 10,
        "Pink Dark" to 11,
        "Yellow Bright" to 12,
        "Yellow Dark" to 13,
        "Teal Bright" to 14,
        "Teal Dark" to 15
    )

    TextItemPickerDropdown(
        modifier = modifier,
        title = colors.find { it.second == selectedColor }?.first ?: "?",
        options = colors.map { it.first },
        onOptionSelected = {
            onColorSelected(colors.find { pair -> pair.first == it }?.second ?: 0)
        }
    )
}