package com.kakos.uniAID.calendar.presentation.edit_event.util

import com.kakos.uniAID.calendar.domain.model.Repeat
import com.kakos.uniAID.calendar.domain.util.SaveOption
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

/**
 * Sealed class representing events for calendar event editing.
 *
 * Defines possible user interactions or system events that can occur
 * within the event editing feature, including form input changes,
 * validation requests, and save operations.
 */
sealed class EditCalendarEventEvent {

    //General
    data class GetEventById(val eventId: Int) : EditCalendarEventEvent()
    data class SaveEvent(val saveOption: SaveOption) : EditCalendarEventEvent()
    data object ValidateEvent : EditCalendarEventEvent()

    //Title
    data class EnteredTitle(val value: String) : EditCalendarEventEvent()

    //All Day
    data class EnteredAllDay(val value: Boolean) : EditCalendarEventEvent()

    //Start Time
    data class EnteredStartTime(val value: LocalTime) : EditCalendarEventEvent()

    //End Time
    data class EnteredEndTime(val value: LocalTime) : EditCalendarEventEvent()

    //Start Date
    data class EnteredStartDate(val value: LocalDate) : EditCalendarEventEvent()
    data class InitializeStartDate(val value: LocalDate) : EditCalendarEventEvent()

    //End Date
    data class EnteredEndDate(val value: LocalDate) : EditCalendarEventEvent()
    data class InitializeEndDate(val value: LocalDate) : EditCalendarEventEvent()

    //Repeat
    data class EnteredRepeat(val value: Repeat) : EditCalendarEventEvent()

    //Selected Repeat Days
    data class EnteredSelectedDays(val value: List<DayOfWeek>) : EditCalendarEventEvent()

    //Repeat Difference
    data class EnteredRepeatDifference(val value: Long) : EditCalendarEventEvent()

    //Repeat End Date
    data class EnteredRepeatEndDate(val value: LocalDate) : EditCalendarEventEvent()

    //Subject
    data class EnteredSubject(val value: Int?) : EditCalendarEventEvent()

    //Color
    data class EnteredColor(val value: Int) : EditCalendarEventEvent()

    //Location
    data class EnteredLocation(val value: String) : EditCalendarEventEvent()

    //Description
    data class EnteredDescription(val value: String) : EditCalendarEventEvent()

    // Datastore operations
    data object FetchSubjects : EditCalendarEventEvent()

    //data object GetCurrentSemesterEndDate : EditCalendarEventEvent()

    data object GetDefaultEventColor : EditCalendarEventEvent()

    data object SetCurrentSemesterEndDate : EditCalendarEventEvent()

}