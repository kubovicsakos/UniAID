package com.kakos.uniAID.calendar.domain.use_case.event.read


import android.util.Log
import com.kakos.uniAID.calendar.domain.model.Event
import com.kakos.uniAID.calendar.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for retrieving all calendar events.
 *
 * Encapsulates the logic for accessing the complete list of events,
 * handling the retrieval of event data through the repository.
 *
 * @property repository The repository used to access event data.
 * @return A flow of all events in the calendar.
 */
class GetAllEventsUseCase(
    private val repository: EventRepository
) {

    private val tag = "GetAllEventsUseCase"

    operator fun invoke(): Flow<List<Event>> {
        Log.d(tag, "getting all events")
        return repository.getAllEvents()
    }
}