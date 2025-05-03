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


class GetAllEventsUseCaseTest {
    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var fakeEventRepository: FakeEventRepository
    private lateinit var getAllEventsUseCase: GetAllEventsUseCase

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        fakeEventRepository = FakeEventRepository()
        getAllEventsUseCase = GetAllEventsUseCase(fakeEventRepository)
        fakeEventRepository.shouldHaveFilledList(true)
    }

    @Test
    fun `getting all events returns the complete list of events from repository, list matches repository content`() =
        runTest {
            val eventsFromUseCase = getAllEventsUseCase().first()
            val eventsFromRepository = fakeEventRepository.getAllEvents().first()

            assertThat(eventsFromUseCase).isEqualTo(eventsFromRepository)
            assertThat(eventsFromUseCase.size).isEqualTo(3)
        }

    @Test
    fun `getting all events from empty repository returns empty list, return is empty`() = runTest {
        fakeEventRepository.shouldHaveFilledList(false)

        val eventsFromUseCase = getAllEventsUseCase().first()

        assertThat(eventsFromUseCase).isEmpty()
    }

    @Test
    fun `getting all events after adding new event includes the new event, list contains new event`() =
        runTest {
            val initialEvents = getAllEventsUseCase().first()
            val initialSize = initialEvents.size

            val newEvent = Event(
                id = 4,
                title = "New Test Event",
                description = "Test Description",
                color = 3,
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

            fakeEventRepository.insertEvent(newEvent)

            val updatedEvents = getAllEventsUseCase().first()

            assertThat(updatedEvents.size).isEqualTo(initialSize + 1)
            assertThat(updatedEvents).contains(newEvent)
        }

    @Test
    fun `getting all events after removing event excludes the removed event, list does not contain removed event`() =
        runTest {
            val initialEvents = getAllEventsUseCase().first()
            val eventToRemove = initialEvents[0]
            val initialSize = initialEvents.size

            fakeEventRepository.deleteEvent(eventToRemove)

            val updatedEvents = getAllEventsUseCase().first()

            assertThat(updatedEvents.size).isEqualTo(initialSize - 1)
            assertThat(updatedEvents).doesNotContain(eventToRemove)
        }

    @Test
    fun `getting all events after updating event includes the updated event, list contains updated event properties`() =
        runTest {
            val initialEvents = getAllEventsUseCase().first()
            val eventToUpdate = initialEvents[0]

            val updatedEvent = eventToUpdate.copy(
                title = "Updated Title",
                description = "Updated Description",
                color = 5
            )

            fakeEventRepository.updateEvent(updatedEvent)

            val eventsAfterUpdate = getAllEventsUseCase().first()

            assertThat(eventsAfterUpdate).contains(updatedEvent)
            assertThat(eventsAfterUpdate.find { it.id == eventToUpdate.id }?.title).isEqualTo("Updated Title")
            assertThat(eventsAfterUpdate.find { it.id == eventToUpdate.id }?.description).isEqualTo(
                "Updated Description"
            )
        }
}