package com.kakos.uniAID.calendar.domain.use_case.event.delete

import android.util.Log
import com.kakos.uniAID.calendar.domain.repository.EventRepository
import java.time.LocalDate

/**
 * Use case for deleting future events in a recurring series.
 *
 * Encapsulates the logic for removing recurring events with the same repeatId
 * starting from a specified date, handling partial deletion of a series.
 *
 * @property repository The repository used to access and delete event data.
 * @throws IllegalArgumentException When the repeatId is invalid.
 */
class DeleteRepeatFromDateUseCase(
    private val repository: EventRepository
) {
    private val tag = "DeleteRepeatUseCase"

    suspend operator fun invoke(repeatId: Int, startDate: LocalDate) {
        Log.d(tag, "deleting all events with repeatId: $repeatId from startDate: $startDate")

        if (repeatId <= 0) {
            Log.e(tag, "EXCEPTION: event id is invalid: $repeatId")
            throw IllegalArgumentException("Event id is invalid: $repeatId")
        }

        repository.deleteEventsByRepeatIdAndStartDate(repeatId, startDate)
    }
}