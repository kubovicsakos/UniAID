package com.kakos.uniAID.calendar.domain.use_case.event.update

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.kakos.uniAID.calendar.data.repository.FakeEventRepository
import com.kakos.uniAID.calendar.domain.model.Event
import com.kakos.uniAID.calendar.domain.model.InvalidEventException
import com.kakos.uniAID.calendar.domain.model.Repeat
import com.kakos.uniAID.calendar.domain.util.generateRepeatingEvents
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class UpdateAllRepeatingEventsUseCaseTest {
    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var fakeEventRepository: FakeEventRepository
    private lateinit var updateAllRepeatingEventsUseCase: UpdateAllRepeatingEventsUseCase

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        fakeEventRepository = FakeEventRepository()
        updateAllRepeatingEventsUseCase = UpdateAllRepeatingEventsUseCase(fakeEventRepository)
        fakeEventRepository.shouldHaveFilledList(true)
    }

    @Test
    fun `update all repeating events changes all events with the same repeatId, all events are updated`() =
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

            // Update one event with new properties
            val updatedEvent = event1.copy(
                title = "Updated Repeating Event",
                description = "Updated Description",
                color = 3,
                location = "Updated Location"
            )

            updateAllRepeatingEventsUseCase(updatedEvent)

            val eventsAfterUpdate = fakeEventRepository.getAllEvents().first()
                .filter { it.repeatId == repeatId }

            // All events should be updated with the new properties
            assertThat(eventsAfterUpdate.all { it.title == "Updated Repeating Event" }).isTrue()
            assertThat(eventsAfterUpdate.all { it.description == "Updated Description" }).isTrue()
            assertThat(eventsAfterUpdate.all { it.color == 3 }).isTrue()
            assertThat(eventsAfterUpdate.all { it.location == "Updated Location" }).isTrue()
            assertThat(eventsAfterUpdate.size).isEqualTo(3)
        }

    @Test
    fun `update all repeating events with changed repeat parameters regenerates events, old events replaced with new pattern`() =
        runTest {
            // Create initial repeating events
            val repeatId = 100
            val today = LocalDate.now()

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
                repeatEndDate = today.plusDays(5),
                repeatDays = emptyList(),
                allDay = false,
                subjectId = null,
                subjectName = null
            )

            // Generate and insert initial events
            val initialEvents = generateRepeatingEvents(initialEvent)
            for (event in initialEvents) {
                fakeEventRepository.insertEvent(event)
            }

            // Verify initial state
            val initialRepoEvents = fakeEventRepository.getAllEvents().first()
                .filter { it.repeatId == repeatId }

            assertThat(initialRepoEvents.size).isEqualTo(6) // Today plus 5 days

            // Update to weekly instead of daily, and extend end date
            val updatedEvent = initialEvent.copy(
                repeat = Repeat.WEEKLY,
                repeatDifference = 1,
                repeatEndDate = today.plusWeeks(3),
                repeatDays = listOf(today.dayOfWeek)
            )

            updateAllRepeatingEventsUseCase(updatedEvent)

            // Check updated events
            val updatedRepoEvents = fakeEventRepository.getAllEvents().first()
                .filter { it.repeatId == repeatId }
                .sortedBy { it.startDate }

            // Should have events 1 week apart
            assertThat(updatedRepoEvents.size).isEqualTo(4) // Initial plus 3 weeks

            // Verify the dates are weekly
            for (i in 0 until updatedRepoEvents.size - 1) {
                assertThat(updatedRepoEvents[i].startDate.plusDays(7))
                    .isEqualTo(updatedRepoEvents[i + 1].startDate)
            }
        }

    @Test
    fun `update all repeating events with modified event properties maintains date structure, dates unchanged but properties updated`() =
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

            // Get the events before updating
            val eventDatesBefore = fakeEventRepository.getAllEvents().first()
            print("Events before update: ")
            eventDatesBefore.forEach { println(it) }

            // Update with new time but same repeat parameters
            val updatedEvent = event1.copy(
                startTime = LocalTime.of(14, 0),
                endTime = LocalTime.of(15, 0),
                location = "New Location"
            )

            updateAllRepeatingEventsUseCase(updatedEvent)

            // Get events after update
            val eventsAfterUpdate = fakeEventRepository.getAllEvents().first()
                .filter { it.repeatId == repeatId }
            print("Events after update: ")
            eventsAfterUpdate.forEach { println(it) }

            // Verify all events have new time but same dates
            for (event in eventsAfterUpdate) {
                assertThat(event.startTime).isEqualTo(LocalTime.of(14, 0))
                assertThat(event.endTime).isEqualTo(LocalTime.of(15, 0))
                assertThat(event.location).isEqualTo("New Location")
            }
            assertThat(eventsAfterUpdate.size).isEqualTo(3)
        }

    @Test
    fun `update all repeating events with non-existing repeatId throws InvalidEventException, repository unchanged`() =
        runTest {
            val initialEvents = fakeEventRepository.getAllEvents().first()
            print("Initial events: ")
            initialEvents.forEach { println(it) }
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
                updateAllRepeatingEventsUseCase(eventWithNonExistingRepeatId)
            } catch (e: InvalidEventException) {
                assertThat(e.message).contains("No existing events found by repeat ID")
                exceptionThrown = true
            }


            val eventsAfterUpdate = fakeEventRepository.getAllEvents().first()
            print("Events after update: ")
            eventsAfterUpdate.forEach { println(it) }

            // Repository should remain unchanged
            assertThat(eventsAfterUpdate).isEqualTo(initialEvents)
            assertThat(eventsAfterUpdate.size).isEqualTo(initialEvents.size)
            assertThat(exceptionThrown).isTrue()
        }

    @Test
    fun `update all repeating events with invalid event throws InvalidEventException, repository unchanged`() =
        runTest {
            val initialEvents = fakeEventRepository.getAllEvents().first()
            print("Initial events: ")
            initialEvents.forEach { println(it) }
            val repeatId = 100

            // Create some repeating events first
            val validEvent = Event(
                id = 10,
                title = "Repeating Event",
                description = "Test Description",
                color = 1,
                location = "Test Location",
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(11, 0),
                repeatId = repeatId,
                repeat = Repeat.DAILY,
                repeatDifference = 1,
                repeatEndDate = LocalDate.now().plusDays(5),
                repeatDays = emptyList(),
                allDay = false,
                subjectId = null,
                subjectName = null
            )

            fakeEventRepository.insertEvent(validEvent)

            // Try to update with an invalid event (blank title)
            val invalidTitleEvent = validEvent.copy(
                title = ""
            )

            val invalidDateEvent = validEvent.copy(
                startDate = LocalDate.now(),
                endDate = LocalDate.now().minusDays(1)
            )

            val invalidWeeklyEvent = validEvent.copy(
                repeat = Repeat.WEEKLY,
                repeatDays = emptyList()
            )

            var exceptionForTitleThrown = false
            try {
                updateAllRepeatingEventsUseCase(invalidTitleEvent)
            } catch (e: InvalidEventException) {
                exceptionForTitleThrown = true
                assertThat(e.message).contains("The title of the event")
            }

            assertThat(exceptionForTitleThrown).isTrue()

            var exceptionForDateThrown = false
            try {
                updateAllRepeatingEventsUseCase(invalidDateEvent)
            } catch (e: InvalidEventException) {
                exceptionForDateThrown = true
                assertThat(e.message).contains("The start date of the event")
            }

            assertThat(exceptionForDateThrown).isTrue()

            var exceptionForWeeklyThrown = false
            try {
                updateAllRepeatingEventsUseCase(invalidWeeklyEvent)
            } catch (e: InvalidEventException) {
                exceptionForWeeklyThrown = true
                assertThat(e.message).contains("Weekly events must")
            }

            assertThat(exceptionForWeeklyThrown).isTrue()

            val eventsAfterAttempt = fakeEventRepository.getAllEvents().first()
            assertThat(eventsAfterAttempt.find { it.id == validEvent.id }?.title).isEqualTo("Repeating Event")
            assertThat(eventsAfterAttempt.find { it.id == validEvent.id }?.startDate).isEqualTo(
                LocalDate.now()
            )
            assertThat(eventsAfterAttempt.find { it.id == validEvent.id }?.endDate).isEqualTo(
                LocalDate.now()
            )
            assertThat(eventsAfterAttempt.find { it.id == validEvent.id }?.repeat).isEqualTo(Repeat.DAILY)
            assertThat(eventsAfterAttempt.find { it.id == validEvent.id }?.repeatDays).isEmpty()
        }

    @Test
    fun `update all repeating events with null repeatId throws InvalidEventException, repository unchanged`() =
        runTest {
            val initialEvents = fakeEventRepository.getAllEvents().first()

            val eventWithNullRepeatId = Event(
                id = 10,
                title = "Non-repeating Event",
                description = "Test Description",
                color = 1,
                location = "Test Location",
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(11, 0),
                repeatId = null,
                repeat = Repeat.DAILY,
                repeatDifference = 1,
                repeatEndDate = LocalDate.now().plusDays(5),
                repeatDays = emptyList(),
                allDay = false,
                subjectId = null,
                subjectName = null
            )

            val eventWithZeroRepeatId = eventWithNullRepeatId.copy(
                repeatId = 0
            )

            var exceptionNullThrown = false
            try {
                updateAllRepeatingEventsUseCase(eventWithNullRepeatId)
            } catch (e: InvalidEventException) {
                exceptionNullThrown = true
                assertThat(e.message).contains("Valid repeat ID is required for updating")
            }

            var exceptionZeroThrown = false
            try {
                updateAllRepeatingEventsUseCase(eventWithZeroRepeatId)
            } catch (e: InvalidEventException) {
                exceptionZeroThrown = true
                assertThat(e.message).contains("Valid repeat ID is required for updating")
            }

            assertThat(exceptionNullThrown).isTrue()
            assertThat(exceptionZeroThrown).isTrue()

            val eventsAfterAttempt = fakeEventRepository.getAllEvents().first()
            assertThat(eventsAfterAttempt).isEqualTo(initialEvents)
        }
}