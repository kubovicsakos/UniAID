package com.kakos.uniAID.calendar.domain.use_case.event.delete

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

class DeleteRepeatFromDateUseCaseTest {
    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var fakeEventRepository: FakeEventRepository
    private lateinit var deleteRepeatFromDateUseCase: DeleteRepeatFromDateUseCase

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        fakeEventRepository = FakeEventRepository()
        deleteRepeatFromDateUseCase = DeleteRepeatFromDateUseCase(fakeEventRepository)
        fakeEventRepository.shouldHaveFilledList(true)
    }

    @Test
    fun `delete events from date with valid repeatId, only events on or after date are removed`() =
        runTest {
            val repeatId = 100
            val today = LocalDate.now()
            val tomorrow = today.plusDays(1)
            val dayAfterTomorrow = today.plusDays(2)

            // Create events with same repeatId but different dates
            val event1 = Event(
                id = 10,
                title = "Repeating Event 1",
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

            val initialEvents = fakeEventRepository.getAllEvents().first()
            println("Initial events:")
            initialEvents.forEach { println(it) }

            // Delete events from tomorrow onwards
            deleteRepeatFromDateUseCase(repeatId, tomorrow)

            val eventsAfterDelete = fakeEventRepository.getAllEvents().first()
            println("Events after delete:")
            eventsAfterDelete.forEach { println(it) }

            // Only event1 should remain (today)
            assertThat(eventsAfterDelete).contains(event1)
            assertThat(eventsAfterDelete).doesNotContain(event2)
            assertThat(eventsAfterDelete).doesNotContain(event3)
        }

    @Test
    fun `delete events from date with non-existing repeatId has no effect, repository unchanged`() =
        runTest {
            val initialEvents = fakeEventRepository.getAllEvents().first()
            val nonExistingRepeatId = 999
            val startDate = LocalDate.now()

            deleteRepeatFromDateUseCase(nonExistingRepeatId, startDate)

            val eventsAfterDelete = fakeEventRepository.getAllEvents().first()

            // Repository should remain unchanged
            assertThat(eventsAfterDelete).isEqualTo(initialEvents)
            assertThat(eventsAfterDelete.size).isEqualTo(initialEvents.size)
        }

    @Test
    fun `delete events with zero repeatId throws IllegalArgumentException, repository unchanged`() =
        runTest {
            val initialEvents = fakeEventRepository.getAllEvents().first()
            val invalidRepeatId = 0
            val startDate = LocalDate.now()

            var exceptionThrown = false
            try {
                deleteRepeatFromDateUseCase(invalidRepeatId, startDate)
            } catch (e: IllegalArgumentException) {
                exceptionThrown = true
                assertThat(e.message).contains("Event id is invalid")
            }

            assertThat(exceptionThrown).isTrue()

            val eventsAfterAttempt = fakeEventRepository.getAllEvents().first()
            assertThat(eventsAfterAttempt).isEqualTo(initialEvents)
        }

    @Test
    fun `delete events with negative repeatId throws IllegalArgumentException, repository unchanged`() =
        runTest {
            val initialEvents = fakeEventRepository.getAllEvents().first()
            val invalidRepeatId = -5
            val startDate = LocalDate.now()

            var exceptionThrown = false
            try {
                deleteRepeatFromDateUseCase(invalidRepeatId, startDate)
            } catch (e: IllegalArgumentException) {
                exceptionThrown = true
                assertThat(e.message).contains("Event id is invalid")
            }

            assertThat(exceptionThrown).isTrue()

            val eventsAfterAttempt = fakeEventRepository.getAllEvents().first()
            assertThat(eventsAfterAttempt).isEqualTo(initialEvents)
        }

    @Test
    fun `delete events from future date retains all events, all events remain when date is in future`() =
        runTest {
            val repeatId = 100
            val today = LocalDate.now()
            val futureDate = today.plusYears(1)

            // Create repeating events all occurring before the future date
            val event1 = Event(
                id = 10,
                title = "Repeating Event 1",
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

            val event2 = event1.copy(
                id = 11,
                startDate = today.plusDays(1),
                endDate = today.plusDays(1)
            )

            fakeEventRepository.insertEvent(event1)
            fakeEventRepository.insertEvent(event2)

            val initialEvents = fakeEventRepository.getAllEvents().first()

            // Delete events from a date far in the future
            deleteRepeatFromDateUseCase(repeatId, futureDate)

            val eventsAfterDelete = fakeEventRepository.getAllEvents().first()

            // All events should remain since they occur before the future date
            assertThat(eventsAfterDelete).contains(event1)
            assertThat(eventsAfterDelete).contains(event2)
            assertThat(eventsAfterDelete.size).isEqualTo(initialEvents.size)
        }

    @Test
    fun `delete events from past date removes all matching events, no events remain when date is in past`() =
        runTest {
            val repeatId = 100
            val pastDate = LocalDate.now().minusYears(1)
            val today = LocalDate.now()

            // Create repeating events occurring after the past date
            val event1 = Event(
                id = 10,
                title = "Repeating Event 1",
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

            val event2 = event1.copy(
                id = 11,
                startDate = today.plusDays(1),
                endDate = today.plusDays(1)
            )

            fakeEventRepository.insertEvent(event1)
            fakeEventRepository.insertEvent(event2)

            // Delete events from a date in the past
            deleteRepeatFromDateUseCase(repeatId, pastDate)

            val eventsAfterDelete = fakeEventRepository.getAllEvents().first()

            // All events should be removed since they occur after the past date
            assertThat(eventsAfterDelete.filter { it.repeatId == repeatId }).isEmpty()
        }

    @Test
    fun `delete events from date with multiple repeat groups, only targeted repeat group affected`() =
        runTest {
            val repeatId1 = 100
            val repeatId2 = 200
            val today = LocalDate.now()

            // Create events with repeatId1
            val event1 = Event(
                id = 10,
                title = "Repeating Event Group 1",
                description = "Test Description",
                color = 1,
                location = "Test Location",
                startDate = today,
                endDate = today,
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(11, 0),
                repeatId = repeatId1,
                repeat = Repeat.DAILY,
                repeatDifference = 1,
                repeatEndDate = today.plusDays(5),
                repeatDays = emptyList(),
                allDay = false,
                subjectId = null,
                subjectName = null
            )

            // Create events with repeatId2
            val event2 = event1.copy(
                id = 11,
                title = "Repeating Event Group 2",
                repeatId = repeatId2
            )

            fakeEventRepository.insertEvent(event1)
            fakeEventRepository.insertEvent(event2)

            // Delete only repeatId1 events from today
            deleteRepeatFromDateUseCase(repeatId1, today)

            val eventsAfterDelete = fakeEventRepository.getAllEvents().first()

            // Verify only events with repeatId1 were removed
            assertThat(eventsAfterDelete.none { it.repeatId == repeatId1 }).isTrue()
            assertThat(eventsAfterDelete.any { it.repeatId == repeatId2 }).isTrue()
        }
}