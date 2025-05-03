package com.kakos.uniAID.calendar.domain.use_case.event.read

import android.util.Log
import com.kakos.uniAID.calendar.domain.model.Event
import com.kakos.uniAID.calendar.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Use case for retrieving events within a specified date range.
 *
 * Encapsulates the logic for fetching events occurring between start and end dates,
 * handling the retrieval of event data through the repository.
 *
 * @property repository The repository used to access event data.
 * @throws IllegalArgumentException When the start date is after the end date.
 * @return A flow of events occurring within the specified date range.
 */
class GetEventsInRangeUseCase(
    private val repository: EventRepository
) {

    private val tag = "GetEventsInRangeUseCase"

    operator fun invoke(start: LocalDate, end: LocalDate): Flow<List<Event>> {
        Log.d(tag, "getting events in range: $start - $end")

        if (start.isAfter(end)) {
            Log.e(tag, "EXCEPTION: start date is after end date: $start - $end")
            throw IllegalArgumentException("Start date is after end date: $start - $end")
        }

        return repository.getEventsInRange(start, end)
    }
}