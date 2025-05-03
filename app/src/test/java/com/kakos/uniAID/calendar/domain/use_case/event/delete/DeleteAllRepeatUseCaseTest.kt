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

class DeleteAllRepeatUseCaseTest {
    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var fakeEventRepository: FakeEventRepository
    private lateinit var deleteEventUseCase: DeleteAllRepeatUseCase

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        fakeEventRepository = FakeEventRepository()
        deleteEventUseCase = DeleteAllRepeatUseCase(fakeEventRepository)
        fakeEventRepository.shouldHaveFilledList(true)
    }

    @Test
    fun `delete events with valid repeatId, all events with that repeatId removed from repository`() =
        runTest {
            // Add repeating events with same repeatId
            val repeatId = 100
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

            // Verify events were added
            val eventsBeforeDelete = fakeEventRepository.getAllEvents().first()
            println("Events before delete:")
            eventsBeforeDelete.forEach { println(it) }
            val repeatingEventsBeforeDelete = eventsBeforeDelete.filter { it.repeatId == repeatId }
            println("Repeating events before delete:")
            repeatingEventsBeforeDelete.forEach { println(it) }
            assertThat(repeatingEventsBeforeDelete.size).isEqualTo(2)

            // Execute use case
            deleteEventUseCase(repeatId)

            // Verify events were removed
            val eventsAfterDelete = fakeEventRepository.getAllEvents().first()
            println("Events after delete:")
            eventsAfterDelete.forEach { println(it) }
            val repeatingEventsAfterDelete = eventsAfterDelete.filter { it.repeatId == repeatId }
            println("Repeating events after delete:")
            repeatingEventsAfterDelete.forEach { println(it) }
            assertThat(repeatingEventsAfterDelete).isEmpty()
        }

    @Test
    fun `delete events with repeatId leaves other events untouched, only targeted events removed`() =
        runTest {
            val repeatId1 = 100
            val repeatId2 = 200

            // Add events with repeatId1
            val event1 = Event(
                id = 10,
                title = "Repeating Event Series 1",
                description = "Test Description",
                color = 1,
                location = "Test Location",
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(11, 0),
                repeatId = repeatId1,
                repeat = Repeat.DAILY,
                repeatDifference = 1,
                repeatEndDate = LocalDate.now().plusDays(5),
                repeatDays = emptyList(),
                allDay = false,
                subjectId = null,
                subjectName = null
            )

            // Add events with repeatId2
            val event2 = event1.copy(
                id = 11,
                title = "Repeating Event Series 2",
                repeatId = repeatId2
            )

            fakeEventRepository.insertEvent(event1)
            fakeEventRepository.insertEvent(event2)

            val initialEvents = fakeEventRepository.getAllEvents().first()
            println("Initial events:")
            initialEvents.forEach { println(it) }
            val initialSize = initialEvents.size

            // Execute use case to delete only repeatId1 events
            deleteEventUseCase(repeatId1)

            val eventsAfterDelete = fakeEventRepository.getAllEvents().first()
            println("Events after delete:")
            eventsAfterDelete.forEach { println(it) }

            // Verify only events with repeatId1 were removed
            assertThat(eventsAfterDelete.any { it.repeatId == repeatId1 }).isFalse()
            assertThat(eventsAfterDelete.any { it.repeatId == repeatId2 }).isTrue()

            // Original events without repeatId1 are still there
            assertThat(eventsAfterDelete.size).isEqualTo(initialSize - 1)
        }

    @Test
    fun `delete events with non-existing repeatId has no effect, repository unchanged`() = runTest {
        val initialEvents = fakeEventRepository.getAllEvents().first()
        val nonExistingRepeatId = 999

        deleteEventUseCase(nonExistingRepeatId)

        val eventsAfterDelete = fakeEventRepository.getAllEvents().first()

        // Repository should remain unchanged
        assertThat(eventsAfterDelete).isEqualTo(initialEvents)
        assertThat(eventsAfterDelete.size).isEqualTo(initialEvents.size)
    }

    @Test
    fun `delete with zero repeatId throws IllegalArgumentException, repository unchanged`() =
        runTest {
            val initialEvents = fakeEventRepository.getAllEvents().first()
            val invalidRepeatId = 0

            var exceptionThrown = false
            try {
                deleteEventUseCase(invalidRepeatId)
            } catch (e: IllegalArgumentException) {
                exceptionThrown = true
                assertThat(e.message).contains("Invalid repeatId")
            }

            assertThat(exceptionThrown).isTrue()

            val eventsAfterAttempt = fakeEventRepository.getAllEvents().first()
            assertThat(eventsAfterAttempt).isEqualTo(initialEvents)
        }

    @Test
    fun `delete with negative repeatId throws IllegalArgumentException, repository unchanged`() =
        runTest {
            val initialEvents = fakeEventRepository.getAllEvents().first()
            val invalidRepeatId = -5

            var exceptionThrown = false
            try {
                deleteEventUseCase(invalidRepeatId)
            } catch (e: IllegalArgumentException) {
                exceptionThrown = true
                assertThat(e.message).contains("Invalid repeatId")
            }

            assertThat(exceptionThrown).isTrue()

            val eventsAfterAttempt = fakeEventRepository.getAllEvents().first()
            assertThat(eventsAfterAttempt).isEqualTo(initialEvents)
        }

    @Test
    fun `delete multiple events with same repeatId, all matching events removed`() = runTest {
        val repeatId = 100

        // Create multiple events with the same repeatId

        val repeatingEvent = Event(
            id = null,
            title = "Repeating Event ",
            description = "Test Description",
            color = 3,
            location = "Test Location",
            startDate = LocalDate.now().plusDays(1),
            endDate = LocalDate.now().plusDays(1),
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

        // Insert all events
        for (i in 1..5) {
            fakeEventRepository.insertEvent(repeatingEvent)
        }

        val initialEvents = fakeEventRepository.getAllEvents().first()
        println("Initial events:")
        initialEvents.forEach { println(it) }
        val repeatingEventsCount = initialEvents.count { it.repeatId == repeatId }
        assertThat(repeatingEventsCount).isEqualTo(5)

        // Execute use case
        deleteEventUseCase(repeatId)

        val eventsAfterDelete = fakeEventRepository.getAllEvents().first()
        println("Events after delete:")
        eventsAfterDelete.forEach { println(it) }
        val remainingRepeatingEvents = eventsAfterDelete.filter { it.repeatId == repeatId }

        // All events with the specified repeatId should be gone
        assertThat(remainingRepeatingEvents).isEmpty()
    }
}