package com.kakos.uniAID.calendar.domain.use_case.event.create

import android.util.Log
import com.kakos.uniAID.calendar.domain.model.Event
import com.kakos.uniAID.calendar.domain.model.InvalidEventException
import com.kakos.uniAID.calendar.domain.model.Repeat
import com.kakos.uniAID.calendar.domain.repository.EventRepository
import com.kakos.uniAID.calendar.domain.util.generateRepeatingEvents

const val TAG = "AddEventUseCase"

/**
 * Use case for adding a new event to the calendar.
 *
 * Encapsulates the logic for creating and storing events, handling validation
 * and processing of both single and repeating events. For repeating events,
 * generates all instances in the specified date range.
 *
 * @property repository The repository used to persist event data.
 * @throws InvalidEventException When event data fails validation.
 */
class AddEventUseCase(
    private val repository: EventRepository
) {
    @Throws(InvalidEventException::class)
    suspend operator fun invoke(event: Event) {

        // Validate the event properties TODO: add more validation if needed
        if (event.title.isBlank()) {
            Log.e(
                TAG,
                "EXCEPTION: The title of the event can't be empty. Is title empty: ${event.title.isBlank()}"
            )
            throw InvalidEventException("The title of the event can't be empty")
        }
        if (event.repeat == Repeat.WEEKLY && event.repeatDays.isEmpty()) {
            Log.e(
                TAG,
                "EXCEPTION: For weekly repeating events, at least one day of the week must be specified."
            )
            throw InvalidEventException("For weekly repeating events, at least one day of the week must be specified")
        }
        if (event.startDate.isAfter(event.endDate)) {
            Log.e(
                TAG,
                "EXCEPTION: The start date of the event must be before the end date."
            )
            throw InvalidEventException("The start date of the event must be before the end date")
        }
        if (!event.isValidEvent()) {
            Log.e(
                TAG,
                "EXCEPTION: Event is not valid. Is event valid: ${event.isValidEvent()}"
            )
            throw InvalidEventException("Event is not valid")
        }

        if (event.repeat == Repeat.NONE) {
            Log.d(TAG, "New event is not repeating: ${event.repeat}")
            val newEvent = event.copy(
                repeatId = null
            )
            Log.d(TAG, "inserting new event: $newEvent")
            repository.insertEvent(newEvent)
            return
        } else {
            Log.d(TAG, "New event is repeating: ${event.repeat}")
            val tempEvent = event.copy(
                repeatId = null
            )
            Log.d(TAG, "inserting temp event: $tempEvent")
            val eventID = repository.insertEvent(tempEvent)
            val updatedEvent = event.copy(
                repeatId = eventID.toInt(),
                repeatDifference = event.repeatDifference,
                repeatEndDate = event.repeatEndDate
            )
            Log.d(TAG, "updating event event with id: $eventID with info: $updatedEvent")
            repository.updateEvent(updatedEvent)
            Log.d(TAG, "generating repeating events")
            val generatedEvents = generateRepeatingEvents(updatedEvent)
            Log.d(TAG, "inserting generated events: $generatedEvents")
            repository.insertEvents(generatedEvents)
            Log.d(TAG, "deleting temp event")
            repository.deleteEventById(eventID.toInt())
            return
        }
    }
}


