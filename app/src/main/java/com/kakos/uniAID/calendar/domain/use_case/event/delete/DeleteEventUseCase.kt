package com.kakos.uniAID.calendar.domain.use_case.event.delete

import android.util.Log
import com.kakos.uniAID.calendar.domain.model.Event
import com.kakos.uniAID.calendar.domain.repository.EventRepository

/**
 * Use case for deleting a single event from the calendar.
 *
 * Encapsulates the logic for removing an individual event from storage,
 * handling the interaction with the repository layer.
 *
 * @property repository The repository used to access and delete event data.
 * @throws IllegalArgumentException When the event id is invalid.
 */
class DeleteEventUseCase(
    private val repository: EventRepository
) {
    private val tag = "DeleteEventUseCase"

    suspend operator fun invoke(event: Event) {

        Log.d(tag, "deleting event: $event")

        if (event.id == null || event.id <= 0) {
            Log.e(tag, "EXCEPTION: event id is invalid: ${event.id}")
            throw IllegalArgumentException("Event id is invalid: ${event.id}")
        }

        repository.deleteEvent(event)
    }
}