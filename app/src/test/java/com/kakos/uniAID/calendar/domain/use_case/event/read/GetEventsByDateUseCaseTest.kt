package com.kakos.uniAID.calendar.domain.use_case.event.read

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.kakos.uniAID.calendar.data.repository.FakeEventRepository
import com.kakos.uniAID.calendar.domain.model.Event
import com.kakos.uniAID.calendar.domain.model.Repeat
import com.kakos.uniAID.calendar.domain.repository.EventRepository
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class GetEventsByDateUseCaseTest {

    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var fakeEventRepository: FakeEventRepository
    private lateinit var getEventsByDateUseCase: GetEventsByDateUseCase

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        fakeEventRepository = FakeEventRepository()
        getEventsByDateUseCase = GetEventsByDateUseCase(fakeEventRepository)
        fakeEventRepository.shouldHaveFilledList(true)
    }

    @Test
    fun `get events for valid date returns events on that date, events on specified date are returned`() =
        runTest {
            val today = LocalDate.now()

            val eventsOnDate = getEventsByDateUseCase(today).first()

            // All default events are on today's date in FakeEventRepository
            assertThat(eventsOnDate.size).isEqualTo(3)
            assertThat(eventsOnDate.all { it.startDate == today }).isTrue()
        }

    @Test
    fun `get events for date with no events returns empty list, result is empty`() = runTest {
        val dateWithNoEvents = LocalDate.now().plusYears(10)

        val eventsOnDate = getEventsByDateUseCase(dateWithNoEvents).first()

        assertThat(eventsOnDate).isEmpty()
    }

    @Test
    fun `get events for multi-day event includes event when date is between start and end date, spanning events are returned`() =
        runTest {
            val today = LocalDate.now()
            val tomorrow = today.plusDays(1)
            val dayAfterTomorrow = today.plusDays(2)

            val multiDayEvent = Event(
                id = 4,
                title = "Multi-day Event",
                description = "Test Description",
                color = 3,
                location = "Test Location",
                startDate = today,
                endDate = dayAfterTomorrow,
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(11, 0),
                repeatId = null,
                repeat = Repeat.NONE,
                repeatDifference = 1,
                repeatEndDate = today.plusDays(5),
                repeatDays = emptyList(),
                allDay = false,
                subjectId = null,
                subjectName = null
            )

            fakeEventRepository.insertEvent(multiDayEvent)

            // Check for the event on the middle day
            val eventsOnMiddleDay = getEventsByDateUseCase(tomorrow).first()

            assertThat(eventsOnMiddleDay).contains(multiDayEvent)
        }

    @Test
    fun `get events for date at boundary of event period includes the event, events on start and end dates are returned`() =
        runTest {
            val today = LocalDate.now()
            val nextWeek = today.plusWeeks(1)

            val boundaryEvent = Event(
                id = 4,
                title = "Boundary Event",
                description = "Test Description",
                color = 3,
                location = "Test Location",
                startDate = today,
                endDate = nextWeek,
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(11, 0),
                repeatId = null,
                repeat = Repeat.NONE,
                repeatDifference = 1,
                repeatEndDate = today.plusDays(5),
                repeatDays = emptyList(),
                allDay = false,
                subjectId = null,
                subjectName = null
            )

            fakeEventRepository.insertEvent(boundaryEvent)

            // Check for the event on the start date
            val eventsOnStartDay = getEventsByDateUseCase(today).first()
            assertThat(eventsOnStartDay).contains(boundaryEvent)

            // Check for the event on the end date
            val eventsOnEndDay = getEventsByDateUseCase(nextWeek).first()
            assertThat(eventsOnEndDay).contains(boundaryEvent)
        }

    @Test
    fun `get events after adding event for specific date includes the new event, new event is retrieved`() =
        runTest {
            val specificDate = LocalDate.now().plusDays(5)

            val newEvent = Event(
                id = 4,
                title = "Future Event",
                description = "Test Description",
                color = 3,
                location = "Test Location",
                startDate = specificDate,
                endDate = specificDate,
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(11, 0),
                repeatId = null,
                repeat = Repeat.NONE,
                repeatDifference = 1,
                repeatEndDate = specificDate.plusDays(5),
                repeatDays = emptyList(),
                allDay = false,
                subjectId = null,
                subjectName = null
            )

            // Initially there should be no events on the specific date
            assertThat(getEventsByDateUseCase(specificDate).first()).isEmpty()

            fakeEventRepository.insertEvent(newEvent)

            val eventsOnSpecificDate = getEventsByDateUseCase(specificDate).first()

            assertThat(eventsOnSpecificDate).containsExactly(newEvent)
        }

    @Test
    fun `get events after deleting event for specific date excludes the deleted event, deleted event is not retrieved`() =
        runTest {
            val today = LocalDate.now()
            val initialEvents = getEventsByDateUseCase(today).first()
            val eventToDelete = initialEvents[0]

            fakeEventRepository.deleteEvent(eventToDelete)

            val eventsAfterDelete = getEventsByDateUseCase(today).first()

            assertThat(eventsAfterDelete).doesNotContain(eventToDelete)
            assertThat(eventsAfterDelete.size).isEqualTo(initialEvents.size - 1)
        }

    @Test
    fun `get events after updating event for specific date includes the updated event, updated properties are reflected`() =
        runTest {
            val today = LocalDate.now()
            val initialEvents = getEventsByDateUseCase(today).first()
            val eventToUpdate = initialEvents[0]

            val updatedEvent = eventToUpdate.copy(
                title = "Updated Title",
                description = "Updated Description"
            )

            fakeEventRepository.updateEvent(updatedEvent)

            val eventsAfterUpdate = getEventsByDateUseCase(today).first()

            assertThat(eventsAfterUpdate).contains(updatedEvent)
            assertThat(eventsAfterUpdate.find { it.id == eventToUpdate.id }?.title).isEqualTo("Updated Title")
        }

    @Test
    fun `get events for specific date handles repository error and returns empty flow, error handling works`() =
        runTest {
            val errorDate = LocalDate.of(2000, 1, 1) // Using a specific date to trigger error

            // Make repository throw exception for this specific date
            val mockRepository = io.mockk.mockk<EventRepository>()
            every { mockRepository.getEventsByDate(errorDate) } throws RuntimeException("Database error")

            val useCase = GetEventsByDateUseCase(mockRepository)
            val result = useCase(errorDate).first()

            assertThat(result).isEmpty()
        }

    @Test
    fun `get events for date with custom error handling returns empty flow, error is caught`() =
        runTest {
            val errorDate = LocalDate.of(2000, 1, 1)

            // Setup mockk to throw Error (not Exception)
            val mockRepository = io.mockk.mockk<EventRepository>()
            every { mockRepository.getEventsByDate(errorDate) } throws Error("Critical error")

            val useCase = GetEventsByDateUseCase(mockRepository)
            val result = useCase(errorDate).first()

            assertThat(result).isEmpty()
        }

    @Test
    fun `get events for null date throws exception, invalid input validation works`() = runTest {
        var exceptionThrown = false

        try {
            @Suppress("NULL_FOR_NONNULL_TYPE")
            getEventsByDateUseCase(null)
        } catch (e: NullPointerException) {
            exceptionThrown = true
        }

        assertThat(exceptionThrown).isTrue()
    }

    @Test
    fun `get events for today and add an all day event includes all day event, all day flag handled correctly`() =
        runTest {
            val today = LocalDate.now()

            val allDayEvent = Event(
                id = 4,
                title = "All Day Event",
                description = "Test Description",
                color = 3,
                location = "Test Location",
                startDate = today,
                endDate = today,
                startTime = LocalTime.of(0, 0),
                endTime = LocalTime.of(23, 59),
                repeatId = null,
                repeat = Repeat.NONE,
                repeatDifference = 1,
                repeatEndDate = today,
                repeatDays = emptyList(),
                allDay = true,
                subjectId = null,
                subjectName = null
            )

            fakeEventRepository.insertEvent(allDayEvent)

            val todayEvents = getEventsByDateUseCase(today).first()

            assertThat(todayEvents).contains(allDayEvent)
            assertThat(todayEvents.find { it.id == allDayEvent.id }?.allDay).isTrue()
        }
}