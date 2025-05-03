package com.kakos.uniAID.calendar.presentation.calendar.util

import java.time.LocalDate
import java.time.YearMonth

/**
 * Sealed class for calendar event operations.
 *
 * Encapsulates the various user interactions and system events that can
 * trigger state changes within the calendar view, providing a consistent
 * event handling approach for date selection and content management.
 */
sealed class CalendarEventsEvent {
    data class GetEventByDate(val date: LocalDate) : CalendarEventsEvent()
    data class GetEventsByMonth(val month: YearMonth) : CalendarEventsEvent()
}