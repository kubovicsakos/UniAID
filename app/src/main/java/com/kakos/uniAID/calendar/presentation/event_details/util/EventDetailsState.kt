package com.kakos.uniAID.calendar.presentation.event_details.util

import com.kakos.uniAID.calendar.domain.model.Event
import com.kakos.uniAID.calendar.domain.model.Repeat
import java.time.LocalDate
import java.time.LocalTime

/**
 * Represents UI state for event details screen.
 *
 * Encapsulates the currently displayed event information with a default state
 * that provides placeholder values when no specific event is loaded.
 *
 * @property event Event object containing all details to be displayed, initialized
 * with sensible defaults for new or unloaded events.
 */
data class EventDetailsState(
    val event: Event = Event(
        id = -1,
        title = "Default event",
        description = "Description forDefault event",
        startDate = LocalDate.now(),
        startTime = LocalTime.of(12, 0),
        endDate = LocalDate.now(),
        endTime = LocalTime.of(13, 0),
        subjectId = 1,
        repeat = Repeat.NONE,
        location = "Location for Default event",
        color = 0,
        repeatEndDate = LocalDate.now().plusWeeks(1)
    )
)