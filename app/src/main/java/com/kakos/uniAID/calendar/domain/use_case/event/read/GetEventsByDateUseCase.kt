package com.kakos.uniAID.calendar.domain.use_case.event.read

import android.util.Log
import com.kakos.uniAID.calendar.domain.model.Event
import com.kakos.uniAID.calendar.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate

/**
 * Use case for retrieving events on a specific date.
 *
 * Encapsulates the logic for fetching events occurring on a given date,
 * handling the retrieval of event data through the repository.
 *
 * @property repository The repository used to access event data.
 * @return A flow of events occurring on the specified date.
 */
class GetEventsByDateUseCase(
    private val repository: EventRepository
) {

    private val tag = "GetEventsByDateUseCase"

    operator fun invoke(date: LocalDate): Flow<List<Event>> {
        Log.d(tag, "getting events for date: $date")
        try {
            return repository.getEventsByDate(date)
        } catch (e: Exception) {
            Log.e(tag, "EXCEPTION: while getting events for date: $date", e)
            return flowOf(emptyList())
        } catch (e: Error) {
            Log.e(tag, "ERROR: while getting events for date: $date", e)
            return flowOf(emptyList())
        }
    }
}