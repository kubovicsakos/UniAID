package com.kakos.uniAID.calendar.presentation.calendar

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.common.truth.Truth.assertThat
import com.kakos.uniAID.MainDispatcherRule
import com.kakos.uniAID.calendar.data.repository.FakeEventRepository
import com.kakos.uniAID.calendar.domain.use_case.EventUseCases
import com.kakos.uniAID.calendar.domain.use_case.fakeEventUseCases
import com.kakos.uniAID.calendar.presentation.calendar.util.CalendarEventsEvent
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: CalendarViewModel
    private lateinit var fakeEventRepository: FakeEventRepository
    private lateinit var eventUseCases: EventUseCases
    private lateinit var dataStore: DataStore<Preferences>

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
        dataStore = mockk(relaxed = true)
        every { dataStore.data } returns flowOf(
            androidx.datastore.preferences.core.preferencesOf(
                stringPreferencesKey("week_start_day") to DayOfWeek.MONDAY.name
            )
        )

        viewModel = CalendarViewModel(eventUseCases, dataStore)
    }

    // Initial state tests
    @Test
    fun `initial state has correct default values`() = runTest {
        advanceUntilIdle()

        assertThat(viewModel.state.value.events).isEmpty()

        assertThat(viewModel.state.value.selectedDate).isEqualTo(LocalDate.now())
        assertThat(viewModel.state.value.weekStartDay).isEqualTo(DayOfWeek.MONDAY)
        assertThat(viewModel.eventsByDate.value).isEmpty()
        assertThat(viewModel.state.value.isLoading).isFalse()
    }

    @Test
    fun `loading events by date populates state correctly`() = runTest {
        fakeEventRepository.shouldHaveFilledList(true)
        viewModel = CalendarViewModel(eventUseCases, dataStore)
        advanceUntilIdle()

        println("Events: ")
        for (event in viewModel.state.value.events) {
            println(event)
        }
        assertThat(viewModel.state.value.events).isNotEmpty()
        assertThat(viewModel.state.value.events.size).isEqualTo(3)
        assertThat(viewModel.state.value.isLoading).isFalse()
        assertThat(viewModel.state.value.selectedDate).isEqualTo(LocalDate.now())
        assertThat(viewModel.state.value.weekStartDay).isEqualTo(DayOfWeek.MONDAY)
    }

    // GetEventsByDate tests
    @Test
    fun `selecting date updates state and loads events for that date with empty day`() = runTest {
        fakeEventRepository.shouldHaveFilledList(true)
        val testDate = LocalDate.now().plusDays(1) // No events are on the next day

        viewModel.onEvent(CalendarEventsEvent.GetEventByDate(testDate))
        advanceUntilIdle()

        assertThat(viewModel.state.value.selectedDate).isEqualTo(testDate)
        assertThat(viewModel.state.value.events).isEmpty()
    }

    @Test
    fun `selecting date updates state and loads events for that date with not empty day`() =
        runTest {
            fakeEventRepository.shouldHaveFilledList(true)
            val testDate = LocalDate.now().plusDays(1) // No events are on the next day
            val newEvent = fakeEventRepository.getAllEvents().first()[0].copy(
                id = 4,
                title = "New event",
                startDate = testDate,
                endDate = testDate
            )
            fakeEventRepository.insertEvent(newEvent)
            advanceUntilIdle()

            viewModel.onEvent(CalendarEventsEvent.GetEventByDate(testDate))
            advanceUntilIdle()

            assertThat(viewModel.state.value.selectedDate).isEqualTo(testDate)
            assertThat(viewModel.state.value.events).isNotEmpty()
            assertThat(viewModel.state.value.events.size).isEqualTo(1)
            assertThat(viewModel.state.value.events).contains(newEvent)
        }

    // GetEventsByMonth tests
    @Test
    fun `loading events by month populates eventsByDate correctly`() = runTest {
        fakeEventRepository.shouldHaveFilledList(true)
        val newEvent = fakeEventRepository.getAllEvents().first()[0].copy(
            id = 4,
            title = "Updated title",
            subjectId = 1,
            subjectName = "subject name",
            startDate = LocalDate.now().plusDays(1),
            endDate = LocalDate.now().plusDays(2)
        )
        fakeEventRepository.insertEvent(newEvent)

        viewModel = CalendarViewModel(eventUseCases, dataStore)

        val testMonth = YearMonth.now()
        viewModel.onEvent(CalendarEventsEvent.GetEventsByMonth(testMonth))
        advanceUntilIdle()

        val events = viewModel.eventsByDate.value
        println("Events: ")
        events.forEach { (date, events) ->
            println("Date: $date, Events: $events")
        }
        assertThat(viewModel.eventsByDate.value).isNotEmpty()
        assertThat(viewModel.eventsByDate.value.size).isEqualTo(3) // 3 in 1 day, 1 in 2 days
    }

    @Test
    fun `loading events by month with empty repository, sets empty map`() = runTest {
        viewModel = CalendarViewModel(eventUseCases, dataStore)

        val testMonth = YearMonth.now()
        viewModel.onEvent(CalendarEventsEvent.GetEventsByMonth(testMonth))
        advanceUntilIdle()

        val events = viewModel.eventsByDate.value
        println("Events: ")
        events.forEach { (date, events) ->
            println("Date: $date, Events: $events")
        }
        assertThat(viewModel.eventsByDate.value).isEmpty()
        assertThat(viewModel.eventsByDate.value.size).isEqualTo(0) // 3 in 1 day, 1 in 2 days
    }

    // Custom week start day tests
    @Test
    fun `custom week start day is loaded from datastore`() = runTest {
        val testWeekStartDay = DayOfWeek.SUNDAY
        val testDataStore = mockk<DataStore<Preferences>>(relaxed = true)
        every { testDataStore.data } returns flowOf(
            androidx.datastore.preferences.core.preferencesOf(
                stringPreferencesKey("week_start_day") to testWeekStartDay.name
            )
        )

        viewModel = CalendarViewModel(eventUseCases, testDataStore)
        advanceUntilIdle()

        assertThat(viewModel.state.value.weekStartDay).isEqualTo(testWeekStartDay)
    }
}