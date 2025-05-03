package com.kakos.uniAID.calendar.presentation.calendar.util

import com.kakos.uniAID.calendar.domain.model.Event
import java.time.DayOfWeek
import java.time.LocalDate

/**
 * Data class representing calendar events display state.
 *
 * Encapsulates the core properties required for calendar view rendering,
 * including current event collection, date selection state, and
 * layout configuration options for consistent user experience.
 *
 * @property events The collection of events to be displayed in the calendar.
 * @property selectedDate The currently highlighted date within the calendar.
 * @property weekStartDay Configurable first day of week for calendar alignment.
 * @property isLoading Whether data loading operations are currently in progress.
 */
data class CalendarEventState(
    val events: List<Event> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val weekStartDay: DayOfWeek = DayOfWeek.MONDAY,
    val isLoading: Boolean = true
)