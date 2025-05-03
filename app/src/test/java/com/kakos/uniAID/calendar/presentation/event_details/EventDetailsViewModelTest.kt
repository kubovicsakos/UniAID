package com.kakos.uniAID.calendar.presentation.event_details

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.kakos.uniAID.MainDispatcherRule
import com.kakos.uniAID.calendar.data.repository.FakeEventRepository
import com.kakos.uniAID.calendar.domain.model.Event
import com.kakos.uniAID.calendar.domain.model.Repeat
import com.kakos.uniAID.calendar.domain.use_case.EventUseCases
import com.kakos.uniAID.calendar.domain.use_case.fakeEventUseCases
import com.kakos.uniAID.calendar.domain.util.DeleteOption
import com.kakos.uniAID.calendar.presentation.event_details.util.EventDetailsEvent
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
class EventDetailsViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: EventDetailsViewModel
    private lateinit var fakeEventRepository: FakeEventRepository
    private lateinit var eventUseCases: EventUseCases

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.d(any(), any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        fakeEventRepository = FakeEventRepository()
        eventUseCases = fakeEventUseCases(fakeEventRepository)
        fakeEventRepository.shouldHaveFilledList(false)

        viewModel = EventDetailsViewModel(eventUseCases)
    }

    // Initial state test
    @Test
    fun `initial state has correct default values`() = runTest {
        val event = Event(
            id = -1,
            title = "Default event",
            description = "Description forDefault event",
            startDate = LocalDate.now(),
            startTime = LocalTime.of(12, 0),
            endDate = LocalDate.now(),
            endTime = LocalTime.of(13, 0),
            subjectId = 1,
            repeat = Repeat.NONE,
            location = "Location for Default event",
            color = 0,
            repeatEndDate = LocalDate.now().plusWeeks(1)
        )

        assertThat(viewModel.state.value.event).isEqualTo(event)
    }

    // GetEventById tests
    @Test
    fun `loading existing event populates state correctly`() = runTest {
        fakeEventRepository.shouldHaveFilledList(true)
        val testEvent = fakeEventRepository.getAllEvents().first().first()

        viewModel.onEvent(EventDetailsEvent.GetEventById(testEvent.id!!))
        advanceUntilIdle()

        assertThat(viewModel.state.value.event).isNotNull()
        assertThat(viewModel.state.value.event.id).isEqualTo(testEvent.id)
        assertThat(viewModel.state.value.event.title).isEqualTo(testEvent.title)
    }

    @Test
    fun `loading non-existent event ID loads default`() = runTest {

        val event = Event(
            id = -1,
            title = "Default event",
            description = "Description forDefault event",
            startDate = LocalDate.now(),
            startTime = LocalTime.of(12, 0),
            endDate = LocalDate.now(),
            endTime = LocalTime.of(13, 0),
            subjectId = 1,
            repeat = Repeat.NONE,
            location = "Location for Default event",
            color = 0,
            repeatEndDate = LocalDate.now().plusWeeks(1)
        )

        viewModel.onEvent(EventDetailsEvent.GetEventById(999))
        advanceUntilIdle()

        assertThat(viewModel.state.value.event).isEqualTo(event)
    }

    // DeleteEvent tests
    @Test
    fun `delete single event removes event and navigates back`() = runTest {
        fakeEventRepository.shouldHaveFilledList(true)
        val testEvent = fakeEventRepository.getAllEvents().first().first()

        viewModel.onEvent(EventDetailsEvent.GetEventById(testEvent.id!!))
        advanceUntilIdle()

        viewModel.onEvent(EventDetailsEvent.DeleteEvent(testEvent, DeleteOption.THIS))
        advanceUntilIdle()

        // Verify event is deleted
        val eventAfterDelete = fakeEventRepository.getEventById(testEvent.id!!)
        assertThat(eventAfterDelete).isNull()
    }

    @Test
    fun `delete all future events removes only future events`() = runTest {
        // Set up recurring events (one past, one today, one future)
        val repeatId = 123
        val pastEvent = Event(
            id = 1,
            title = "Default event",
            description = "Description forDefault event",
            startDate = LocalDate.now().minusDays(1),
            startTime = LocalTime.of(12, 0),
            endDate = LocalDate.now().minusDays(1),
            endTime = LocalTime.of(13, 0),
            subjectId = 1,
            subjectName = "Math",
            repeat = Repeat.NONE,
            location = "Location for Default event",
            color = Event.eventColors.indexOf(Event.eventColors.random()),
            repeatEndDate = LocalDate.now().plusWeeks(1),
            repeatId = repeatId
        )
        val todayEvent = pastEvent.copy(
            id = 2,
            startDate = LocalDate.now(),
            endDate = LocalDate.now()
        )
        val futureEvent = pastEvent.copy(
            id = 3,
            startDate = LocalDate.now().plusDays(5),
            endDate = LocalDate.now().plusDays(5)
        )

        fakeEventRepository.insertEvent(pastEvent)
        fakeEventRepository.insertEvent(todayEvent)
        fakeEventRepository.insertEvent(futureEvent)

        viewModel.onEvent(EventDetailsEvent.GetEventById(todayEvent.id!!))
        advanceUntilIdle()

        viewModel.onEvent(EventDetailsEvent.DeleteEvent(todayEvent, DeleteOption.THIS_AND_FUTURE))
        advanceUntilIdle()

        val eventsAfterDelete = fakeEventRepository.getAllEvents().first()
        assertThat(eventsAfterDelete).isNotEmpty()
        assertThat(eventsAfterDelete).doesNotContain(todayEvent)
        assertThat(eventsAfterDelete).doesNotContain(futureEvent)
        assertThat(eventsAfterDelete).containsExactly(pastEvent)
    }

    @Test
    fun `delete all events with repeat ID deletes all related events`() = runTest {
        // Set up recurring events
        val repeatId = 123
        val events = listOf(
            Event(
                id = 1,
                title = "Default event",
                description = "Description forDefault event",
                startDate = LocalDate.now().minusDays(1),
                startTime = LocalTime.of(12, 0),
                endDate = LocalDate.now().minusDays(1),
                endTime = LocalTime.of(13, 0),
                subjectId = 1,
                subjectName = "Math",
                repeat = Repeat.DAILY,
                location = "Location for Default event",
                color = 3,
                repeatEndDate = LocalDate.now().plusDays(2),
                repeatId = repeatId
            ),
            Event(
                id = 2,
                title = "Default event",
                description = "Description forDefault event",
                startDate = LocalDate.now().minusDays(1),
                startTime = LocalTime.of(12, 0),
                endDate = LocalDate.now().minusDays(1),
                endTime = LocalTime.of(13, 0),
                subjectId = 1,
                subjectName = "Math",
                repeat = Repeat.DAILY,
                location = "Location for Default event",
                color = 3,
                repeatEndDate = LocalDate.now().plusDays(2),
                repeatId = repeatId
            ),
            Event(
                id = 3,
                title = "Default event",
                description = "Description forDefault event",
                startDate = LocalDate.now().minusDays(1),
                startTime = LocalTime.of(12, 0),
                endDate = LocalDate.now().minusDays(1),
                endTime = LocalTime.of(13, 0),
                subjectId = 1,
                subjectName = "Math",
                repeat = Repeat.DAILY,
                location = "Location for Default event",
                color = 3,
                repeatEndDate = LocalDate.now().plusDays(2),
                repeatId = repeatId
            )
        )

        // Add another event not in series
        val unrelatedEvent = Event(
            id = 4,
            title = "Default event",
            description = "Description forDefault event",
            startDate = LocalDate.now(),
            startTime = LocalTime.of(12, 0),
            endDate = LocalDate.now(),
            endTime = LocalTime.of(13, 0),
            subjectId = 1,
            repeat = Repeat.NONE,
            location = "Location for Default event",
            color = Event.eventColors.indexOf(Event.eventColors.random()),
            repeatEndDate = LocalDate.now().plusWeeks(1)
        )

        events.forEach { fakeEventRepository.insertEvent(it) }
        fakeEventRepository.insertEvent(unrelatedEvent)

        val eventToDelete = events[1]

        viewModel.onEvent(EventDetailsEvent.DeleteEvent(eventToDelete, DeleteOption.ALL))
        advanceUntilIdle()

        // All events with repeatId should be deleted
        events.forEach { event ->
            assertThat(fakeEventRepository.getEventById(event.id!!)).isNull()
        }

        // Unrelated event should still exist
        assertThat(fakeEventRepository.getEventById(unrelatedEvent.id!!)).isNotNull()
    }
}