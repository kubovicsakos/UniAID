package com.kakos.uniAID.calendar.domain.util


import android.util.Log
import com.kakos.uniAID.calendar.domain.model.Event
import com.kakos.uniAID.calendar.domain.model.Repeat
import java.time.DayOfWeek

const val TAG = "DeleteOption"

/**
 * Use case helper for generating recurring event series.
 *
 * Encapsulates the logic for creating multiple event instances based on
 * a base event's repetition rules, handling different repetition patterns
 * including daily, weekly, monthly, and yearly occurrences.
 *
 * @param baseEvent The template event containing repeat parameters and initial values.
 * @return List of generated event instances spanning the repetition period.
 */
fun generateRepeatingEvents(baseEvent: Event): List<Event> {

    val events = mutableListOf<Event>()

    var currentStartDate = baseEvent.startDate
    var currentEndDate = baseEvent.endDate

    val repeatEndDate = baseEvent.repeatEndDate
    val repeatDays = baseEvent.repeatDays

    Log.d(TAG, "Generating repeating events for: $baseEvent")

    while (currentStartDate <= repeatEndDate) {
        Log.d(TAG, "Current start date: $currentStartDate and repeat: ${baseEvent.repeat}")
        if (baseEvent.repeat == Repeat.WEEKLY) {
            if (currentStartDate.dayOfWeek in repeatDays) {
                val newEvent = baseEvent.copy(
                    startDate = currentStartDate,
                    endDate = currentEndDate,
                    repeatId = baseEvent.repeatId
                )
                events.add(newEvent)
                Log.d(TAG, "Generated event: $newEvent")
            }

            // Move to the next day in the week
            currentStartDate = currentStartDate.plusDays(1)
            currentEndDate = currentEndDate.plusDays(1)

            // Check if we've completed a week and apply the repeat difference
            if (currentStartDate.dayOfWeek == DayOfWeek.MONDAY) {
                // Increment weeks by the repeatDifference
                currentStartDate = currentStartDate.plusWeeks(baseEvent.repeatDifference - 1)
                currentEndDate = currentEndDate.plusWeeks(baseEvent.repeatDifference - 1)
            }
        } else {
            val newEvent = baseEvent.copy(
                startDate = currentStartDate,
                endDate = currentEndDate,
                repeatId = baseEvent.repeatId
            )
            events.add(newEvent)
            Log.d(TAG, "Generated event: $newEvent")
            when (baseEvent.repeat) {
                Repeat.DAILY -> {
                    currentStartDate = currentStartDate.plusDays(baseEvent.repeatDifference)
                    currentEndDate = currentEndDate.plusDays(baseEvent.repeatDifference)
                }

                Repeat.MONTHLY -> {
                    currentStartDate = currentStartDate.plusMonths(baseEvent.repeatDifference)
                    currentEndDate = currentEndDate.plusMonths(baseEvent.repeatDifference)
                }

                Repeat.YEARLY -> {
                    currentStartDate = currentStartDate.plusYears(baseEvent.repeatDifference)
                    currentEndDate = currentEndDate.plusYears(baseEvent.repeatDifference)
                }

                else -> break
            }
        }
    }
    Log.d(TAG, "Generated events: $events")
    return events
}


