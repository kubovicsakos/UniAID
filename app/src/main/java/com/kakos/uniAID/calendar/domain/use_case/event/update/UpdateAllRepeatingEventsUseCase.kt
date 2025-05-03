package com.kakos.uniAID.calendar.domain.use_case.event.update

import android.util.Log
import com.kakos.uniAID.calendar.domain.model.Event
import com.kakos.uniAID.calendar.domain.model.InvalidEventException
import com.kakos.uniAID.calendar.domain.model.Repeat
import com.kakos.uniAID.calendar.domain.repository.EventRepository
import com.kakos.uniAID.calendar.domain.util.generateRepeatingEvents

/**
 * Use case for updating all events in a recurring series.
 *
 * Encapsulates the logic for modifying all instances of a repeating event series,
 * handling the deletion of existing occurrences and regeneration based on updated parameters.
 *
 * @property repository The repository used to access and manage event data.
 * @throws InvalidEventException If the updated event is invalid or missing required properties.
 */
class UpdateAllRepeatingEventsUseCase(
    private val repository: EventRepository
) {
    private val tag = "UpdateAllRepeatingEventsUseCase"

    @Throws(InvalidEventException::class)
    suspend operator fun invoke(updatedEvent: Event) {

        if (updatedEvent.title.isBlank()) {
            Log.e(tag, "EXCEPTION: The title of the event can't be empty")
            throw InvalidEventException("The title of the event can't be empty")
        }
        if (updatedEvent.startDate.isAfter(updatedEvent.endDate)) {
            Log.e(tag, "EXCEPTION: The start date of the event must be before the end date")
            throw InvalidEventException("The start date of the event must be before the end date")
        }
        if (updatedEvent.repeatId == null || updatedEvent.repeatId <= 0) {
            Log.e(
                tag,
                "EXCEPTION: Repeat ID is required for updating all events in the series, but it is invalid: ${updatedEvent.repeatId}"
            )
            throw InvalidEventException("Valid repeat ID is required for updating all events in the series, but it is invalid: ${updatedEvent.repeatId}")
        }
        if (updatedEvent.repeat == Repeat.WEEKLY && updatedEvent.repeatDays.isEmpty()) {
            Log.e(tag, "EXCEPTION: Weekly events must specify repeat days")
            throw InvalidEventException("Weekly events must specify repeat days")
        }
        if (!updatedEvent.isValidEvent()) {
            Log.e(tag, "EXCEPTION: Event is invalid")
            throw InvalidEventException("Event is invalid")
        }

        // Fetch the original event to get the correct repeatStartDate
        Log.d(tag, "Updating all events in the series with repeatId: ${updatedEvent.repeatId}")
        val originalEvent = repository.getEventsByRepeatId(updatedEvent.repeatId).firstOrNull()
            ?: throw InvalidEventException("No existing events found by repeat ID: ${updatedEvent.repeatId}")


        val originalStartDate = originalEvent.startDate
        val originalEndDate = originalEvent.endDate
        Log.d(tag, "Original start date: $originalStartDate, original end date: $originalEndDate")

        // Delete all existing events in the series
        Log.d(
            tag,
            "Deleting all existing events in the series with repeatId: ${updatedEvent.repeatId}"
        )
        repository.deleteEventsByRepeatId(updatedEvent.repeatId)

        // Regenerate events with updated details starting from the correct start date
        val baseEvent = updatedEvent.copy(
            id = null,
            startDate = originalStartDate,
            endDate = originalEndDate
        )
        Log.d(tag, "Regenerating events with updated details: $baseEvent")
        val newEvents = generateRepeatingEvents(baseEvent)

        // Insert regenerated events
        Log.d(tag, "Inserting regenerated events: $newEvents")
        repository.insertEvents(newEvents)
    }
}