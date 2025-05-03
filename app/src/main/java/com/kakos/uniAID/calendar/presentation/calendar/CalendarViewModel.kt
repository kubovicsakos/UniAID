package com.kakos.uniAID.calendar.presentation.calendar


import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakos.uniAID.calendar.domain.model.Event
import com.kakos.uniAID.calendar.domain.use_case.EventUseCases
import com.kakos.uniAID.calendar.presentation.calendar.util.CalendarEventState
import com.kakos.uniAID.calendar.presentation.calendar.util.CalendarEventsEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

/**
 * ViewModel for calendar event management and state coordination.
 *
 * Orchestrates calendar data flow between the domain layer and UI components,
 * maintaining reactive state for event collections, date selection, and
 * configuration preferences for consistent calendar behavior.
 *
 * @property eventUseCases Access point for domain-level event operations.
 * @property dataStore Persistent storage for user calendar preferences.
 */
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val eventUseCases: EventUseCases,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    private val tag = "CalendarViewModel"
    private val _weekStartDayKey = stringPreferencesKey("week_start_day")

    // States
    private val _state = mutableStateOf(CalendarEventState())
    val state: State<CalendarEventState> = _state

    private val _eventsByDate = mutableStateOf<Map<LocalDate, List<Event>>>(emptyMap())
    val eventsByDate: State<Map<LocalDate, List<Event>>> = _eventsByDate

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var getEventsByDateJob: Job? = null

    init {
        Log.d(tag, "Initializing")
        val today = LocalDate.now()
        Log.d(tag, "Getting events by date: $today")
        getEventsByDate(today)
        Log.d(tag, "Getting week start day")
        getWeekStartDay()
        Log.d(tag, "New week start day: ${state.value.weekStartDay}")
        Log.d(tag, "Initialization done")
    }

    fun onEvent(event: CalendarEventsEvent) {
        Log.d(tag, "Event received: $event")
        when (event) {
            is CalendarEventsEvent.GetEventByDate -> {
                _state.value = state.value.copy(selectedDate = event.date) // Update selectedDate
                Log.d(tag, "Getting events by date: ${event.date}")
                getEventsByDate(event.date)
            }

            is CalendarEventsEvent.GetEventsByMonth -> {
                Log.d(tag, "Getting events by month: ${event.month}")
                getEventsByMonth(event.month)
                Log.d(tag, "Events by month: ${eventsByDate.value}")
            }
        }
    }

    private fun getEventsByMonth(month: YearMonth) {
        Log.d(tag, "getEventsByMonth called with $month")
        val startDate = month.atDay(1)
        val endDate = month.atEndOfMonth()
        try {
            eventUseCases.getEventsInRangeUseCase(startDate, endDate)
                .onEach { events ->
                    // Expand events to all dates they cover
                    val expandedEvents = events.flatMap { event ->
                        generateSequence(event.startDate) { it.plusDays(1) }
                            .takeWhile { !it.isAfter(event.endDate) }
                            .map { date -> date to event }
                    }
                    _eventsByDate.value = expandedEvents.groupBy({ it.first }, { it.second })
                }.launchIn(viewModelScope)
        } catch (e: IllegalArgumentException) {
            Log.e(tag, "IllegalArgumentException occurred", e)
            _eventFlow.tryEmit(UiEvent.ShowSnackbar("Invalid date range: $startDate - $endDate starting date is after end date"))
        } catch (e: Exception) {
            Log.e(tag, "Exception occurred", e)
            _eventFlow.tryEmit(UiEvent.ShowSnackbar("An exception occurred while fetching events"))
        } catch (e: Error) {
            Log.e(tag, "Error occurred", e)
            _eventFlow.tryEmit(UiEvent.ShowSnackbar("An error occurred while fetching events"))
        }

    }

    private fun getEventsByDate(date: LocalDate) {
        Log.d(tag, "getEventsByDate called with $date")
        getEventsByDateJob?.cancel()
        try {
            getEventsByDateJob = eventUseCases.getEventsByDateUseCase(date)
//                .catch { e ->
//                    Log.e(tag, "THROWN: While fetching events for date: $date", e)
//                    _eventFlow.emit(UiEvent.ShowSnackbar("An error occurred while fetching events for date: $date"))
//                }
                .onEach { events ->
                    // Sort events by start time (ascending)
                    _state.value = state.value.copy(
                        events = events.sortedWith(
                            compareBy<Event> { !it.allDay } // All-day events first (false < true)
                                .thenBy { it.startTime }    // Then sort by start time
                        ),
                    )
                }.launchIn(viewModelScope)
        } catch (e: Exception) {
            Log.e(tag, "EXCEPTION: While fetching events for date: $date", e)
            _eventFlow.tryEmit(UiEvent.ShowSnackbar("An exception occurred while fetching events for date: $date"))
        } catch (e: Error) {
            Log.e(tag, "ERROR: while fetching events for date: $date", e)
            _eventFlow.tryEmit(UiEvent.ShowSnackbar("An error occurred while fetching events for date: $date"))
        }

    }

    private fun getWeekStartDay() {
        Log.d(tag, "getWeekStartDay called")
        viewModelScope.launch {
            val weekStartDay = dataStore.data.map { preferences ->
                preferences[_weekStartDayKey]?.let { DayOfWeek.valueOf(it) } ?: DayOfWeek.MONDAY
            }
            _state.value = state.value.copy(
                weekStartDay = weekStartDay.first(),
                isLoading = false
            )
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
    }
}
