package com.kakos.uniAID.calendar.domain.use_case.event.read

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.kakos.uniAID.calendar.data.repository.FakeEventRepository
import com.kakos.uniAID.calendar.domain.model.Event
import com.kakos.uniAID.calendar.domain.model.Repeat
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class GetEventsInRangeUseCaseTest {
    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var fakeEventRepository: FakeEventRepository
    private lateinit var getEventsInDateUseCase: GetEventsInRangeUseCase

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        fakeEventRepository = FakeEventRepository()
        getEventsInDateUseCase = GetEventsInRangeUseCase(fakeEventRepository)
        fakeEventRepository.shouldHaveFilledList(true)
    }

    @Test
    fun `get events in valid date range returns all events in that range, events within range are returned`() =
        runTest {
            val today = LocalDate.now()
            val nextWeek = today.plusWeeks(1)

            val eventsInRange = getEventsInDateUseCase(today, nextWeek).first()

            // All default events are on today's date in FakeEventRepository
            assertThat(eventsInRange.size).isEqualTo(3)
            assertThat(eventsInRange.all { it.startDate in today..nextWeek }).isTrue()
        }

    @Test
    fun `get events in range with no events returns empty list, result is empty`() = runTest {
        val farFuture = LocalDate.now().plusYears(5)
        val evenFurtherFuture = farFuture.plusWeeks(1)

        val eventsInRange = getEventsInDateUseCase(farFuture, evenFurtherFuture).first()

        assertThat(eventsInRange).isEmpty()
    }

    @Test
    fun `get events where start date is after end date throws exception, invalid range validation works`() =
        runTest {
            val today = LocalDate.now()
            val yesterday = today.minusDays(1)

            var exceptionThrown = false
            try {
                getEventsInDateUseCase(today, yesterday).first()
            } catch (e: IllegalArgumentException) {
                exceptionThrown = true
                assertThat(e.message).contains("Start date is after end date")
            }

            assertThat(exceptionThrown).isTrue()
        }

    @Test
    fun `get events in range includes events that partially overlap with range, partially overlapping events are returned`() =
        runTest {
            val today = LocalDate.now()
            val nextWeek = today.plusWeeks(1)
            val twoWeeks = today.plusWeeks(2)

            // Event that starts in the range but ends after it
            val partiallyOverlappingEvent1 = Event(
                id = 4,
                title = "Event Starting In Range",
                description = "Test Description",
                color = 3,
                location = "Test Location",
                startDate = nextWeek,
                endDate = twoWeeks,
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(11, 0),
                repeatId = null,
                repeat = Repeat.NONE,
                repeatDifference = 1,
                repeatEndDate = twoWeeks,
                repeatDays = emptyList(),
                allDay = false,
                subjectId = null,
                subjectName = null
            )

            // Event that starts before the range but ends in it
            val partiallyOverlappingEvent2 = Event(
                id = 5,
                title = "Event Ending In Range",
                description = "Test Description",
                color = 3,
                location = "Test Location",
                startDate = today.minusWeeks(1),
                endDate = today.plusDays(2),
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(11, 0),
                repeatId = null,
                repeat = Repeat.NONE,
                repeatDifference = 1,
                repeatEndDate = today.plusDays(2),
                repeatDays = emptyList(),
                allDay = false,
                subjectId = null,
                subjectName = null
            )

            fakeEventRepository.insertEvent(partiallyOverlappingEvent1)
            fakeEventRepository.insertEvent(partiallyOverlappingEvent2)

            val eventsInRange = getEventsInDateUseCase(today, nextWeek).first()
            println("Events in range: ")
            eventsInRange.forEach { println(it) }

            assertThat(eventsInRange).contains(partiallyOverlappingEvent1)
            assertThat(eventsInRange).contains(partiallyOverlappingEvent2)
        }

    @Test
    fun `get events in range includes events that completely encapsulate range, spanning events are returned`() =
        runTest {
            val midWeek = LocalDate.now().plusDays(3)
            val nextWeek = LocalDate.now().plusWeeks(1)

            val spanningEvent = Event(
                id = 4,
                title = "Spanning Event",
                description = "Test Description",
                color = 3,
                location = "Test Location",
                startDate = LocalDate.now().minusWeeks(1),
                endDate = LocalDate.now().plusWeeks(2),
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(11, 0),
                repeatId = null,
                repeat = Repeat.NONE,
                repeatDifference = 1,
                repeatEndDate = LocalDate.now().plusWeeks(2),
                repeatDays = emptyList(),
                allDay = false,
                subjectId = null,
                subjectName = null
            )

            fakeEventRepository.insertEvent(spanningEvent)

            val eventsInRange = getEventsInDateUseCase(midWeek, nextWeek).first()
            println("Events in range: ")
            eventsInRange.forEach { println(it) }

            assertThat(eventsInRange).contains(spanningEvent)
        }

    @Test
    fun `get events in range with same start and end date returns events on that date, single day range works`() =
        runTest {
            val today = LocalDate.now()

            val eventsOnDay = getEventsInDateUseCase(today, today).first()
            println("Events on day: ")

            assertThat(eventsOnDay.size).isEqualTo(3) // Default events in repository
            assertThat(eventsOnDay.all { it.startDate == today }).isTrue()
        }

    @Test
    fun `get events after adding event within range includes the new event, new event is retrieved`() =
        runTest {
            val today = LocalDate.now()
            val nextWeek = today.plusWeeks(1)
            val midWeek = today.plusDays(3)

            val newEvent = Event(
                id = 4,
                title = "New Mid-Week Event",
                description = "Test Description",
                color = 3,
                location = "Test Location",
                startDate = midWeek,
                endDate = midWeek,
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(11, 0),
                repeatId = null,
                repeat = Repeat.NONE,
                repeatDifference = 1,
                repeatEndDate = midWeek,
                repeatDays = emptyList(),
                allDay = false,
                subjectId = null,
                subjectName = null
            )

            fakeEventRepository.insertEvent(newEvent)

            val eventsInRange = getEventsInDateUseCase(today, nextWeek).first()

            assertThat(eventsInRange).contains(newEvent)
        }

    @Test
    fun `get events after deleting event within range excludes the deleted event, deleted event is not retrieved`() =
        runTest {
            val today = LocalDate.now()
            val nextWeek = today.plusWeeks(1)

            val initialEvents = getEventsInDateUseCase(today, nextWeek).first()
            println("Initial events: ")
            initialEvents.forEach { println(it) }
            val eventToDelete = initialEvents[0]
            println("Event to delete: $eventToDelete")

            fakeEventRepository.deleteEvent(eventToDelete)

            val eventsAfterDelete = getEventsInDateUseCase(today, nextWeek).first()
            println("Events after delete: ")
            eventsAfterDelete.forEach { println(it) }

            assertThat(eventsAfterDelete).doesNotContain(eventToDelete)
            assertThat(eventsAfterDelete.size).isEqualTo(initialEvents.size - 1)
        }

    @Test
    fun `get events after updating event date to be outside range excludes the event, moved event is not retrieved`() =
        runTest {
            val today = LocalDate.now()
            val nextWeek = today.plusWeeks(1)

            val initialEvents = getEventsInDateUseCase(today, nextWeek).first()
            println("Initial events: ")
            initialEvents.forEach { println(it) }
            val eventToUpdate = initialEvents[0]
            println("Event to update: $eventToUpdate")

            // Move the event outside the query range
            val updatedEvent = eventToUpdate.copy(
                startDate = nextWeek.plusDays(1),
                endDate = nextWeek.plusDays(1)
            )

            fakeEventRepository.updateEvent(updatedEvent)

            val eventsAfterUpdate = getEventsInDateUseCase(today, nextWeek).first()
            println("Events after update: ")
            eventsAfterUpdate.forEach { println(it) }

            assertThat(eventsAfterUpdate).doesNotContain(updatedEvent)
        }
}