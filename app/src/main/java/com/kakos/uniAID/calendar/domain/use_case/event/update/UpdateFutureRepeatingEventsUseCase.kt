package com.kakos.uniAID.calendar.domain.use_case.event.update

import android.util.Log
import com.kakos.uniAID.calendar.domain.model.Event
import com.kakos.uniAID.calendar.domain.model.InvalidEventException
import com.kakos.uniAID.calendar.domain.model.Repeat
import com.kakos.uniAID.calendar.domain.repository.EventRepository
import com.kakos.uniAID.calendar.domain.util.generateRepeatingEvents

/**
 * Use case for updating future instances of a recurring event series.
 *
 * Encapsulates the logic for modifying events in a recurring series from a specific date forward,
 * handling deletion of future occurrences and regeneration based on updated parameters.
 *
 * @property repository The repository used to access and manage event data.
 * @throws InvalidEventException When event data fails validation.
 */
class UpdateFutureRepeatingEventsUseCase(
    private val repository: EventRepository
) {

    private val tag = "UpdateFutureRepeatingEventsUseCase"

    @Throws(InvalidEventException::class)
    suspend operator fun invoke(event: Event) {

        if (event.title.isBlank()) {
            Log.e(tag, "EXCEPTION: The title of the event can't be empty")
            throw InvalidEventException("The title of the event can't be empty")
        }
        if (event.startDate.isAfter(event.endDate)) {
            Log.e(tag, "EXCEPTION: The start date of the event must be before the end date")
            throw InvalidEventException("The start date of the event must be before the end date")
        }
        if (!event.isValidEvent()) {
            Log.e(tag, "EXCEPTION: Event is not valid")
            throw InvalidEventException("Event is not valid")
        }

        if (event.repeat == Repeat.NONE) {
            // Non-repeating event: update directly
            Log.d(tag, "Updating non-repeating event: $event")
            repository.updateEvent(event)
            return
        }

        // Validate the input for weekly repeating events TODO: add more validation if needed
        if (event.repeat == Repeat.WEEKLY && event.repeatDays.isEmpty()) {
            Log.e(
                tag,
                "EXCEPTION: For weekly repeating events, at least one day of the week must be specified"
            )
            throw InvalidEventException("For weekly repeating events, at least one day of the week must be specified")
        }

        // Fetch the original event to check if it exists
        if (event.repeatId != null) {
            repository.getEventsByRepeatId(event.repeatId).firstOrNull()
                ?: throw InvalidEventException("No existing events found by repeat ID: ${event.repeatId}")
        }

        // Update the base event and regenerate future occurrences
        val newEventDetails = event.copy(
            id = null, // Clear the ID for new occurrences
            repeatId = event.repeatId
        )

        // Delete all future events starting from the event's start date
        Log.d(
            tag,
            "Deleting all future events starting from: ${event.startDate} with repeatId: ${event.repeatId}"
        )
        repository.deleteEventsByRepeatIdAndStartDate(event.repeatId, event.startDate)

        // Generate and insert new occurrences
        Log.d(tag, "Generating and inserting new occurrences: $newEventDetails")
        val generatedEvents = generateRepeatingEvents(newEventDetails)
        Log.d(tag, "Inserting generated events: $generatedEvents")
        repository.insertEvents(generatedEvents)
    }
}
