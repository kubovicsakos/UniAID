package com.kakos.uniAID.calendar.domain.use_case.event.update

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.kakos.uniAID.calendar.data.repository.FakeEventRepository
import com.kakos.uniAID.calendar.domain.model.Event
import com.kakos.uniAID.calendar.domain.model.InvalidEventException
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

class UpdateFutureRepeatingEventsUseCaseTest {
    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var fakeEventRepository: FakeEventRepository
    private lateinit var updateFutureRepeatingEventsUseCase: UpdateFutureRepeatingEventsUseCase

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        fakeEventRepository = FakeEventRepository()
        updateFutureRepeatingEventsUseCase = UpdateFutureRepeatingEventsUseCase(fakeEventRepository)
        fakeEventRepository.shouldHaveFilledList(true)
    }

    @Test
    fun `update future repeating events updates events on or after specified date with new properties, earlier events unchanged`() =
        runTest {
            // Create repeating events to test with
            val repeatId = 100
            val today = LocalDate.now()
            val tomorrow = today.plusDays(1)
            val dayAfterTomorrow = today.plusDays(2)

            val event1 = Event(
                id = 10,
                title = "Repeating Event",
                description = "Original Description",
                color = 1,
                location = "Original Location",
                startDate = today,
                endDate = today,
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(11, 0),
                repeatId = repeatId,
                repeat = Repeat.DAILY,
                repeatDifference = 1,
                repeatEndDate = dayAfterTomorrow,
                repeatDays = emptyList(),
                allDay = false,
                subjectId = null,
                subjectName = null
            )

            val event2 = event1.copy(
                id = 11,
                startDate = tomorrow,
                endDate = tomorrow
            )

            val event3 = event1.copy(
                id = 12,
                startDate = dayAfterTomorrow,
                endDate = dayAfterTomorrow
            )

            fakeEventRepository.insertEvent(event1)
            fakeEventRepository.insertEvent(event2)
            fakeEventRepository.insertEvent(event3)

            // Update from tomorrow with new properties
            val updatedEvent = event2.copy(
                title = "Updated Title",
                description = "Updated Description",
                color = 3,
                location = "Updated Location"
            )

            updateFutureRepeatingEventsUseCase(updatedEvent)

            // Check events after update
            val eventsAfterUpdate = fakeEventRepository.getAllEvents().first()

            // First event should remain unchanged
            val firstEvent = eventsAfterUpdate.find { it.id == event1.id }
            assertThat(firstEvent?.title).isEqualTo("Repeating Event")
            assertThat(firstEvent?.description).isEqualTo("Original Description")
            assertThat(firstEvent?.color).isEqualTo(1)
            assertThat(firstEvent?.location).isEqualTo("Original Location")

            // Second and third events should be updated
            val laterEvents = eventsAfterUpdate.filter { it.startDate >= tomorrow }
            assertThat(laterEvents.all { it.title == "Updated Title" }).isTrue()
            assertThat(laterEvents.all { it.description == "Updated Description" }).isTrue()
            assertThat(laterEvents.all { it.color == 3 }).isTrue()
            assertThat(laterEvents.all { it.location == "Updated Location" }).isTrue()
        }

    @Test
    fun `update future repeating events with changed repeat parameters regenerates events from start date, earlier events preserved`() =
        runTest {
            // Create initial repeating events
            val repeatId = 100
            val today = LocalDate.now()
            val tomorrow = today.plusDays(1)
            val dayAfterTomorrow = today.plusDays(2)

            val initialEvent = Event(
                id = 10,
                title = "Daily Event",
                description = "Test Description",
                color = 1,
                location = "Test Location",
                startDate = today,
                endDate = today,
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(11, 0),
                repeatId = repeatId,
                repeat = Repeat.DAILY,
                repeatDifference = 1,
                repeatEndDate = dayAfterTomorrow,
                repeatDays = emptyList(),
                allDay = false,
                subjectId = null,
                subjectName = null
            )

            // Generate and insert initial events
            fakeEventRepository.insertEvent(initialEvent)
            fakeEventRepository.insertEvent(
                initialEvent.copy(
                    id = 11,
                    startDate = tomorrow,
                    endDate = tomorrow
                )
            )
            fakeEventRepository.insertEvent(
                initialEvent.copy(
                    id = 12,
                    startDate = dayAfterTomorrow,
                    endDate = dayAfterTomorrow
                )
            )

            // Update from tomorrow to weekly instead of daily, and extend end date
            val updatedEvent = initialEvent.copy(
                id = 11,
                startDate = tomorrow,
                endDate = tomorrow,
                repeat = Repeat.WEEKLY,
                repeatDifference = 1,
                repeatEndDate = tomorrow.plusWeeks(2),
                repeatDays = listOf(tomorrow.dayOfWeek)
            )

            updateFutureRepeatingEventsUseCase(updatedEvent)

            // Check updated events
            val eventsAfterUpdate = fakeEventRepository.getAllEvents().first()

            // First event should remain unchanged with old pattern
            val firstEvent = eventsAfterUpdate.find { it.id == initialEvent.id }
            assertThat(firstEvent?.repeat).isEqualTo(Repeat.DAILY)
            assertThat(firstEvent?.startDate).isEqualTo(today)

            // Later events should follow weekly pattern
            val futureEvents =
                eventsAfterUpdate.filter { it.startDate >= tomorrow }.sortedBy { it.startDate }
            assertThat(futureEvents.size).isGreaterThan(1)
            assertThat(futureEvents[0].repeat).isEqualTo(Repeat.WEEKLY)

            // Verify weekly spacing (7 days apart)
            if (futureEvents.size > 1) {
                for (i in 0 until futureEvents.size - 1) {
                    if (i > 0) { // Skip first event which may not follow pattern
                        assertThat(futureEvents[i].startDate.plusDays(7)).isEqualTo(futureEvents[i + 1].startDate)
                    }
                }
            }
        }

    @Test
    fun `update future repeating events with invalid event throws InvalidEventException, repository unchanged`() =
        runTest {
            val initialEvents = fakeEventRepository.getAllEvents().first()
            println(initialEvents)
            val repeatId = 100
            val today = LocalDate.now()

            // Create some repeating events first
            val validEvent = Event(
                id = 10,
                title = "Repeating Event",
                description = "Test Description",
                color = 1,
                location = "Test Location",
                startDate = today,
                endDate = today,
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(11, 0),
                repeatId = repeatId,
                repeat = Repeat.DAILY,
                repeatDifference = 1,
                repeatEndDate = today.plusDays(5),
                repeatDays = emptyList(),
                allDay = false,
                subjectId = null,
                subjectName = null
            )

            fakeEventRepository.insertEvent(validEvent)

            // Try to update with an invalid event (blank title)
            val invalidEvent = validEvent.copy(
                title = ""
            )

            var exceptionThrown = false
            try {
                updateFutureRepeatingEventsUseCase(invalidEvent)
            } catch (e: InvalidEventException) {
                exceptionThrown = true
                assertThat(e.message).contains("title")
            }

            assertThat(exceptionThrown).isTrue()

            // Verify repository hasn't changed
            val eventsAfterAttempt =
                fakeEventRepository.getAllEvents().first().filter { it.repeatId == repeatId }
            assertThat(eventsAfterAttempt.find { it.id == validEvent.id }?.title).isEqualTo("Repeating Event")
        }

    @Test
    fun `update future repeating events with non-existing repeatId throws InvalidEventException`() =
        runTest {
            val initialEvents = fakeEventRepository.getAllEvents().first()
            val nonExistingRepeatId = 999

            val eventWithNonExistingRepeatId = Event(
                id = 100,
                title = "Non-existing Event",
                description = "Test Description",
                color = 1,
                location = "Test Location",
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(11, 0),
                repeatId = nonExistingRepeatId,
                repeat = Repeat.DAILY,
                repeatDifference = 1,
                repeatEndDate = LocalDate.now().plusDays(5),
                repeatDays = emptyList(),
                allDay = false,
                subjectId = null,
                subjectName = null
            )

            var exceptionThrown = false
            try {
                updateFutureRepeatingEventsUseCase(eventWithNonExistingRepeatId)
            } catch (e: InvalidEventException) {
                exceptionThrown = true
                assertThat(e.message).contains("No existing events found")
            }

            val updatedEvents = fakeEventRepository.getAllEvents().first()

            assertThat(exceptionThrown).isTrue()
            assertThat(updatedEvents).isEqualTo(initialEvents)
        }

    @Test
    fun `update future repeating events preserves event IDs for existing events`() = runTest {
        // Create repeating events to test with
        val repeatId = 100
        val today = LocalDate.now()
        val tomorrow = today.plusDays(1)
        val dayAfterTomorrow = today.plusDays(2)

        val event1 = Event(
            id = 10,
            title = "Repeating Event",
            description = "Original Description",
            color = 1,
            location = "Original Location",
            startDate = today,
            endDate = today,
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(11, 0),
            repeatId = repeatId,
            repeat = Repeat.DAILY,
            repeatDifference = 1,
            repeatEndDate = dayAfterTomorrow,
            repeatDays = emptyList(),
            allDay = false,
            subjectId = null,
            subjectName = null
        )

        val event2 = event1.copy(id = 11, startDate = tomorrow, endDate = tomorrow)

        fakeEventRepository.insertEvent(event1)
        fakeEventRepository.insertEvent(event2)

        // Update from tomorrow with new properties but same dates
        val updatedEvent = event2.copy(title = "Updated Title")

        updateFutureRepeatingEventsUseCase(updatedEvent)

        // Check events after update
        val eventsAfterUpdate = fakeEventRepository.getAllEvents().first()

        // Verify IDs are preserved
        val existingEvent = eventsAfterUpdate.find { it.startDate == tomorrow }
        assertThat(existingEvent?.id).isEqualTo(11)
        assertThat(existingEvent?.title).isEqualTo("Updated Title")
    }

    @Test
    fun `update future repeating events with invalid parameters throws InvalidEventException`() =
        runTest {
            val repeatId = 100
            val today = LocalDate.now()

            // Create some repeating events first
            val validEvent = Event(
                id = 10,
                title = "Repeating Event",
                description = "Test Description",
                color = 1,
                location = "Test Location",
                startDate = today,
                endDate = today,
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(11, 0),
                repeatId = repeatId,
                repeat = Repeat.DAILY,
                repeatDifference = 1,
                repeatEndDate = today.plusDays(5),
                repeatDays = emptyList(),
                allDay = false,
                subjectId = null,
                subjectName = null
            )

            fakeEventRepository.insertEvent(validEvent)
            val initialEvents = fakeEventRepository.getAllEvents().first()

            // Try to update with invalid repeat ID
            val invalidTitleEvent = validEvent.copy(
                title = ""
            )
            val invalidDateEvent = validEvent.copy(
                startDate = today.plusDays(1),
                endDate = today
            )
            val invalidWeeklyEvent = validEvent.copy(
                repeat = Repeat.WEEKLY,
                repeatDays = emptyList()
            )
            var exceptionForTitleThrown = false
            var exceptionForDateThrown = false
            var exceptionForWeeklyThrown = false

            try {
                updateFutureRepeatingEventsUseCase(invalidTitleEvent)
            } catch (e: InvalidEventException) {
                exceptionForTitleThrown = true
                assertThat(e.message).contains("The title of the event")
            }
            assertThat(exceptionForTitleThrown).isTrue()

            try {
                updateFutureRepeatingEventsUseCase(invalidDateEvent)
            } catch (e: InvalidEventException) {
                exceptionForDateThrown = true
                assertThat(e.message).contains("The start date of the event")
            }
            assertThat(exceptionForDateThrown).isTrue()

            try {
                updateFutureRepeatingEventsUseCase(invalidWeeklyEvent)
            } catch (e: InvalidEventException) {
                exceptionForWeeklyThrown = true
                assertThat(e.message).contains("For weekly repeating events")
            }
            assertThat(exceptionForWeeklyThrown).isTrue()

            val updatedEvents = fakeEventRepository.getAllEvents().first()

            assertThat(updatedEvents).isEqualTo(initialEvents)
        }
}