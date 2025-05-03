package com.kakos.uniAID.settings.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kakos.uniAID.core.presentation.components.dropdowns.TextItemPickerDropdown
import java.time.DayOfWeek

/**
 * Composable that displays a week start day selection dropdown.
 *
 * Allows users to select from days of the week (Mon-Sun)
 * as the starting day for weekly views.
 *
 * @param selectedDay Currently selected day of week.
 * @param onDaySelected Callback triggered when user selects a new day.
 * @param modifier Optional modifier for customizing the component layout.
 */
@Composable
fun WeekStartDayPicker(
    selectedDay: DayOfWeek,
    onDaySelected: (DayOfWeek) -> Unit,
    modifier: Modifier = Modifier
) {
    val dayOptions = listOf(
        "Mon" to DayOfWeek.MONDAY,
        "Tue" to DayOfWeek.TUESDAY,
        "Wed" to DayOfWeek.WEDNESDAY,
        "Thu" to DayOfWeek.THURSDAY,
        "Fri" to DayOfWeek.FRIDAY,
        "Sat" to DayOfWeek.SATURDAY,
        "Sun" to DayOfWeek.SUNDAY
    )

    TextItemPickerDropdown(
        modifier = modifier,
        title = dayOptions.first { it.second == selectedDay }.first,
        options = dayOptions.map { it.first },
        onOptionSelected = { selected ->
            dayOptions.find { it.first == selected }?.second?.let(onDaySelected)
        }
    )
}

