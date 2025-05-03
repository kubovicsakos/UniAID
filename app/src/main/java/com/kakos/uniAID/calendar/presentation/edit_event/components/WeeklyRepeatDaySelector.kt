package com.kakos.uniAID.calendar.presentation.edit_event.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kakos.uniAID.ui.theme.UniAidTheme
import java.time.DayOfWeek

/**
 * Composable that displays a day selector for weekly event repetition.
 *
 * Provides an interactive interface with radio buttons representing days of the week,
 * enabling users to select multiple days for recurring events with visual feedback
 * and abbreviated day labels for efficient space utilization.
 *
 * @param modifier Modifier to be applied to the layout.
 * @param initialSelectedDays List of days that should be initially selected.
 * @param onSelectedDaysChanged Callback invoked when selection changes.
 */
@Composable
fun WeeklyRepeatDaySelector(
    modifier: Modifier = Modifier,
    initialSelectedDays: List<DayOfWeek> = emptyList(),
    onSelectedDaysChanged: (List<DayOfWeek>) -> Unit
) {
    // Internal state to manage selected days
    var selectedDays by remember { mutableStateOf(initialSelectedDays) }

    Column(
        modifier = modifier.padding(8.dp),
    ) {
        Text(
            text = "Repeat on:",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DayOfWeek.entries.forEach { day ->
                val isSelected = day in selectedDays
                Column {
                    RadioButton(
                        selected = isSelected,
                        onClick = {
                            selectedDays = if (isSelected) {
                                selectedDays - day // Remove if already selected
                            } else {
                                selectedDays + day // Add if not selected
                            }
                            onSelectedDaysChanged(selectedDays) // Notify the parent of the change
                        },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.primary,
                            unselectedColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = modifier.size(45.dp) // Adjust size if needed
                    )
                    Text(
                        text = day.name.substring(0, 2), // Abbreviation (e.g., "Mo" for Monday)
                        fontSize = 10.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.size(8.dp))
    }
}


@Preview
@Composable
private fun WeeklyPrev() {
    UniAidTheme(
        themeMode = "Auto"
    ) {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            WeeklyRepeatDaySelector(
                initialSelectedDays = listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY)
            ) { }
        }
    }
}

