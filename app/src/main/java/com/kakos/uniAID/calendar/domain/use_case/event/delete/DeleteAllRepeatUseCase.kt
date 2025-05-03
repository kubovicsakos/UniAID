package com.kakos.uniAID.calendar.domain.use_case.event.delete

import android.util.Log
import com.kakos.uniAID.calendar.domain.repository.EventRepository

/**
 * Use case for deleting all events in a recurring series.
 *
 * Encapsulates the logic for removing all repeating events with the same repeatId,
 * handling the complete deletion of a recurring event series through the repository.
 *
 * @property repository The repository used to access and delete event data.
 * @throws IllegalArgumentException When the repeatId is invalid.
 */
class DeleteAllRepeatUseCase(
    private val repository: EventRepository
) {
    private val tag = "DeleteRepeatUseCase"

    suspend operator fun invoke(repeatId: Int) {
        Log.d(tag, "deleting all events with repeatId: $repeatId")

        if (repeatId <= 0) {
            Log.e(tag, "EXCEPTION: repeatId is invalid: $repeatId")
            throw IllegalArgumentException("Invalid repeatId: $repeatId")
        }

        repository.deleteEventsByRepeatId(repeatId)
    }
}