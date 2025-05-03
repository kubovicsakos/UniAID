package com.kakos.uniAID.calendar.presentation.event_details.util

import com.kakos.uniAID.calendar.domain.model.Event
import com.kakos.uniAID.calendar.domain.util.DeleteOption

/**
 * Composable that displays detailed information about an event.
 *
 * Presents the event's location, recurrence pattern (if applicable),
 * and description in a structured layout with conditional visibility
 * for repeat information based on the event's configuration.
 *
 * @param event Event object containing all details to be displayed.
 */
sealed class EventDetailsEvent {
    data class GetEventById(val eventId: Int) : EventDetailsEvent()
    data class DeleteEvent(val event: Event, val deleteOption: DeleteOption) : EventDetailsEvent()
}
