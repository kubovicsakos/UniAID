package com.kakos.uniAID.settings.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kakos.uniAID.core.presentation.Screen
import com.kakos.uniAID.core.presentation.components.dropdowns.TextItemPickerDropdown

/**
 * Composable that displays a default start screen selection dropdown.
 *
 * Allows users to select from predefined application screens including
 * Notes, Calendar, Subjects, and Statistics options.
 *
 * @param selectedScreen Currently selected screen object.
 * @param onScreenSelected Callback triggered when user selects a new screen.
 * @param modifier Optional modifier for customizing the component layout.
 */
@Composable
fun DefaultStartScreenPicker(
    selectedScreen: Any,
    onScreenSelected: (Any) -> Unit,
    modifier: Modifier = Modifier
) {
    val navLocations = listOf(
        "Notes" to Screen.NotesScreen,
        "Calendar" to Screen.CalendarScreen,
        "Subjects" to Screen.SubjectScreen,
        "Statistics" to Screen.StatisticsScreen
    )

    TextItemPickerDropdown(
        modifier = modifier,
        title = navLocations.first { it.second == selectedScreen }.first,
        options = navLocations.map { it.first },
        onOptionSelected = { selectedName ->
            navLocations.find { it.first == selectedName }?.second?.let(onScreenSelected)
        }
    )
}