package com.kakos.uniAID.settings.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kakos.uniAID.core.presentation.components.dropdowns.TextItemPickerDropdown

/**
 * Composable that displays a color scheme selection dropdown.
 *
 * Allows users to select from predefined color schemes including
 * Auto, Blue, Green, Grey, Purple, Red, and Yellow options.
 *
 * @param selectedScheme Currently selected color scheme name.
 * @param onSchemeSelected Callback triggered when user selects a new scheme.
 * @param modifier Optional modifier for customizing the component layout.
 */
@Composable
fun ColorSchemePicker(
    selectedScheme: String,
    onSchemeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val schemes = listOf("Auto", "Blue", "Green", "Grey", "Purple", "Red", "Yellow")

    TextItemPickerDropdown(
        modifier = modifier,
        title = selectedScheme.replaceFirstChar { it.uppercase() },
        options = schemes,
        onOptionSelected = {
            onSchemeSelected(it)
        }
    )
}