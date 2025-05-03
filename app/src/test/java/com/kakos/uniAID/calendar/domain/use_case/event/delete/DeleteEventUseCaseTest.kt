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

class DeleteEventUseCaseTest {
    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var fakeEventRepository: FakeEventRepository
    private lateinit var deleteEventUseCase: DeleteEventUseCase

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        fakeEventRepository = FakeEventRepository()
        deleteEventUseCase = DeleteEventUseCase(fakeEventRepository)
        fakeEventRepository.shouldHaveFilledList(true)
    }

    @Test
    fun `delete existing event removes it from repository, event not present after deletion`() =
        runTest {
            val eventToDelete = fakeEventRepository.getAllEvents().first()[0]
            val initialSize = fakeEventRepository.getAllEvents().first().size

            deleteEventUseCase(eventToDelete)

            val eventsAfterDelete = fakeEventRepository.getAllEvents().first()
            assertThat(eventsAfterDelete).doesNotContain(eventToDelete)
            assertThat(eventsAfterDelete.size).isEqualTo(initialSize - 1)
        }

    @Test
    fun `delete event with null id throws IllegalArgumentException, repository unchanged`() =
        runTest {
            val initialEvents = fakeEventRepository.getAllEvents().first()
            val eventWithNullId = Event(
                id = null,
                title = "Invalid Event",
                description = "Test Description",
                color = 1,
                location = "Test Location",
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(11, 0),
                repeatId = null,
                repeat = Repeat.NONE,
                repeatDifference = 1,
                repeatEndDate = LocalDate.now().plusDays(5),
                repeatDays = emptyList(),
                allDay = false,
                subjectId = null,
                subjectName = null
            )

            var exceptionThrown = false
            try {
                deleteEventUseCase(eventWithNullId)
            } catch (e: IllegalArgumentException) {
                exceptionThrown = true
                assertThat(e.message).contains("Event id is invalid")
            }

            assertThat(exceptionThrown).isTrue()
            val eventsAfterAttempt = fakeEventRepository.getAllEvents().first()
            assertThat(eventsAfterAttempt).isEqualTo(initialEvents)
        }

    @Test
    fun `delete non-existing event has no effect on repository, repository unchanged`() = runTest {
        val initialEvents = fakeEventRepository.getAllEvents().first()
        val nonExistingEvent = Event(
            id = 999,
            title = "Non-existent Event",
            description = "Test Description",
            color = 1,
            location = "Test Location",
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(11, 0),
            repeatId = null,
            repeat = Repeat.NONE,
            repeatDifference = 1,
            repeatEndDate = LocalDate.now().plusDays(5),
            repeatDays = emptyList(),
            allDay = false,
            subjectId = null,
            subjectName = null
        )

        deleteEventUseCase(nonExistingEvent)

        val eventsAfterDelete = fakeEventRepository.getAllEvents().first()
        assertThat(eventsAfterDelete).isEqualTo(initialEvents)
        assertThat(eventsAfterDelete.size).isEqualTo(initialEvents.size)
    }

    @Test
    fun `delete event with negative id throws IllegalArgumentException, repository unchanged`() =
        runTest {
            val initialEvents = fakeEventRepository.getAllEvents().first()
            val eventWithNegativeId = Event(
                id = -5,
                title = "Invalid Event",
                description = "Test Description",
                color = 1,
                location = "Test Location",
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(11, 0),
                repeatId = null,
                repeat = Repeat.NONE,
                repeatDifference = 1,
                repeatEndDate = LocalDate.now().plusDays(5),
                repeatDays = emptyList(),
                allDay = false,
                subjectId = null,
                subjectName = null
            )

            var exceptionThrown = false
            try {
                deleteEventUseCase(eventWithNegativeId)
            } catch (e: IllegalArgumentException) {
                exceptionThrown = true
                assertThat(e.message).contains("Event id is invalid")
            }

            assertThat(exceptionThrown).isTrue()
            val eventsAfterAttempt = fakeEventRepository.getAllEvents().first()
            assertThat(eventsAfterAttempt).isEqualTo(initialEvents)
        }

    @Test
    fun `delete event with zero id throws IllegalArgumentException, repository unchanged`() =
        runTest {
            val initialEvents = fakeEventRepository.getAllEvents().first()
            val eventWithZeroId = Event(
                id = 0,
                title = "Invalid Event",
                description = "Test Description",
                color = 1,
                location = "Test Location",
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(11, 0),
                repeatId = null,
                repeat = Repeat.NONE,
                repeatDifference = 1,
                repeatEndDate = LocalDate.now().plusDays(5),
                repeatDays = emptyList(),
                allDay = false,
                subjectId = null,
                subjectName = null
            )

            var exceptionThrown = false
            try {
                deleteEventUseCase(eventWithZeroId)
            } catch (e: IllegalArgumentException) {
                exceptionThrown = true
                assertThat(e.message).contains("Event id is invalid")
            }

            assertThat(exceptionThrown).isTrue()
            val eventsAfterAttempt = fakeEventRepository.getAllEvents().first()
            assertThat(eventsAfterAttempt).isEqualTo(initialEvents)
        }

    @Test
    fun `delete event with valid repeatId removes only that specific event, other events with same repeatId remain`() =
        runTest {
            val repeatId = 100

            // Create two events with same repeatId
            val event1 = Event(
                id = 10,
                title = "Repeating Event 1",
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

            val event2 = event1.copy(
                id = 11,
                startDate = LocalDate.now().plusDays(1)
            )

            fakeEventRepository.insertEvent(event1)
            fakeEventRepository.insertEvent(event2)

            // Delete only the first event
            deleteEventUseCase(event1)

            val eventsAfterDelete = fakeEventRepository.getAllEvents().first()

            // First event should be gone, second should remain
            assertThat(eventsAfterDelete).doesNotContain(event1)
            assertThat(eventsAfterDelete).contains(event2)

            // Events with the same repeatId should now be 1
            val repeatingEventsAfterDelete = eventsAfterDelete.filter { it.repeatId == repeatId }
            assertThat(repeatingEventsAfterDelete.size).isEqualTo(1)
        }

    @Test
    fun `delete last event of repository results in empty repository, repository size is zero`() =
        runTest {
            fakeEventRepository.shouldHaveFilledList(false)

            val event = Event(
                id = 1,
                title = "Single Event",
                description = "Test Description",
                color = 1,
                location = "Test Location",
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(11, 0),
                repeatId = null,
                repeat = Repeat.NONE,
                repeatDifference = 1,
                repeatEndDate = LocalDate.now().plusDays(5),
                repeatDays = emptyList(),
                allDay = false,
                subjectId = null,
                subjectName = null
            )

            fakeEventRepository.insertEvent(event)
            assertThat(fakeEventRepository.getAllEvents().first().size).isEqualTo(1)

            deleteEventUseCase(event)

            assertThat(fakeEventRepository.getAllEvents().first()).isEmpty()
        }
}