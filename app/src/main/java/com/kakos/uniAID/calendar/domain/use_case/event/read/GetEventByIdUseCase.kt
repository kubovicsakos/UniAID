package com.kakos.uniAID.calendar.domain.use_case.event.read

import android.util.Log
import com.kakos.uniAID.calendar.domain.model.Event
import com.kakos.uniAID.calendar.domain.repository.EventRepository

/**
 * Use case for retrieving a calendar event by ID.
 *
 * Encapsulates the logic for fetching a specific event by its unique identifier,
 * handling the interaction with the repository layer.
 *
 * @property repository The repository used to access event data.
 * @return The event with the specified ID, or null if no event was found.
 */
class GetEventByIdUseCase(
    private val repository: EventRepository
) {

    private val tag = "GetEventByIdUseCase"

    suspend operator fun invoke(eventId: Int): Event? {
        Log.d(tag, "getting event with id: $eventId")

        if (eventId <= 0) {
            Log.e(tag, "WARNING: event id is invalid: $eventId returning null")
        }

        return repository.getEventById(eventId)
    }
}