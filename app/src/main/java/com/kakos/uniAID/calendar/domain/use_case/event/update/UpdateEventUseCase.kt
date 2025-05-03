package com.kakos.uniAID.calendar.domain.use_case.event.update

import android.util.Log
import com.kakos.uniAID.calendar.domain.model.Event
import com.kakos.uniAID.calendar.domain.model.InvalidEventException
import com.kakos.uniAID.calendar.domain.model.Repeat
import com.kakos.uniAID.calendar.domain.repository.EventRepository
import com.kakos.uniAID.calendar.domain.use_case.event.create.TAG

/**
 * Use case for updating a single calendar event.
 *
 * Encapsulates the logic for modifying an existing event,
 * handling the interaction with the repository layer.
 *
 * @property repository The repository used to access and update event data.
 * @throws InvalidEventException When event data fails validation.
 */
class UpdateEventUseCase(
    private val repository: EventRepository
) {

    private val tag = "UpdateEventUseCase"

    @Throws(InvalidEventException::class)
    suspend operator fun invoke(event: Event) {
        Log.d(tag, "updating event: $event")

        if (event.title.isBlank()) {
            Log.e(tag, "EXCEPTION: The title of the event can't be empty")
            throw InvalidEventException("The title of the event can't be empty")
        }
        if (event.startDate.isAfter(event.endDate)) {
            Log.e(tag, "EXCEPTION: The start date of the event must be before the end date")
            throw InvalidEventException("The start date of the event must be before the end date")
        }
        if (event.repeat == Repeat.WEEKLY && event.repeatDays.isEmpty()) {
            Log.e(
                TAG,
                "EXCEPTION: For weekly repeating events, at least one day of the week must be specified."
            )
            throw InvalidEventException("For weekly repeating events, at least one day of the week must be specified")
        }
        if (event.id == null || event.id <= 0) {
            Log.e(tag, "EXCEPTION: Event ID can't be null or negative")
            throw InvalidEventException("Event ID is invalid: ${event.id}")
        }
        if (!event.isValidEvent()) {
            Log.e(tag, "EXCEPTION: Event is not valid")
            throw InvalidEventException("Event is not valid")
        }

        repository.updateEvent(event)
        return
    }
}