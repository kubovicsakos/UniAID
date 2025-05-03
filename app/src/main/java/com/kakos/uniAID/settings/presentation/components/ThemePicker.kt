package com.kakos.uniAID.settings.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kakos.uniAID.core.presentation.components.dropdowns.TextItemPickerDropdown

/**
 * Composable that displays a theme selection dropdown.
 *
 * Allows users to select from predefined theme options including
 * Auto, Light, and Dark.
 *
 * @param selectedTheme Currently selected theme name.
 * @param onThemeSelected Callback triggered when user selects a new theme.
 * @param modifier Optional modifier for customizing the component layout.
 */
@Composable
fun ThemePicker(
    selectedTheme: String,
    onThemeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val themes = listOf("Auto", "Light", "Dark")

    TextItemPickerDropdown(
        modifier = modifier,
        title = selectedTheme,
        options = themes,
        onOptionSelected = { selected ->
            onThemeSelected(selected)
        }
    )
}