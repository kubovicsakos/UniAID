package com.kakos.uniAID.calendar.presentation.edit_event

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakos.uniAID.calendar.domain.model.Event
import com.kakos.uniAID.calendar.domain.model.InvalidEventException
import com.kakos.uniAID.calendar.domain.model.Repeat
import com.kakos.uniAID.calendar.domain.use_case.EventUseCases
import com.kakos.uniAID.calendar.domain.util.SaveOption
import com.kakos.uniAID.calendar.presentation.edit_event.util.EditCalendarEventEvent
import com.kakos.uniAID.calendar.presentation.edit_event.util.EditEventState
import com.kakos.uniAID.calendar.presentation.edit_event.util.EventTextFieldState
import com.kakos.uniAID.core.domain.subject.domain.use_case.SubjectUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

/**
 * ViewModel for the Edit Event screen.
 *
 * Manages state and business logic for creating and editing calendar events,
 * handling form input validation, data persistence, and integration with
 * event and subject repositories.
 *
 * @property eventUseCases Use cases for event operations (creating, updating, retrieving).
 * @property subjectUseCases Use cases for subject-related operations.
 * @property dataStore Persistent storage for preferences and configuration data.
 */
@HiltViewModel
class EditEventViewModel @Inject constructor(
    private val eventUseCases: EventUseCases,
    private val subjectUseCases: SubjectUseCases,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    private val tag = "EditEventViewModel"
    private val currentSemesterKey = intPreferencesKey("current_semester")
    private val semesterEndDatesKey = stringPreferencesKey("semester_end_dates")
    private val defaultEventColorKey = intPreferencesKey("event_color")

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _defaultEventColor = mutableIntStateOf(0)


    private val _state = mutableStateOf(EditEventState())
    val state: State<EditEventState> = _state

    private var currentEventId: Int? = null
    private var currentRepeatId: Int? = null
    var initialRepeatState: Repeat = Repeat.NONE

    init {
        Log.d(tag, "Initializing")
        Log.d(tag, "Fetching subjects")
        fetchSubjects()
        Log.d(tag, "Initialization done")
    }

    fun onEvent(event: EditCalendarEventEvent) {
        when (event) {
            is EditCalendarEventEvent.GetEventById -> {
                Log.d(tag, "Getting event by ID: ${event.eventId}")
                currentEventId = if (event.eventId == -1) null else event.eventId
                if (currentEventId != null) {
                    viewModelScope.launch {
                        eventUseCases.getEventByIdUseCase(currentEventId!!)?.also { event ->
                            _state.value = EditEventState(
                                title = EventTextFieldState(text = event.title),
                                allDay = event.allDay,
                                startTime = event.startTime,
                                endTime = event.endTime,
                                startDate = event.startDate,
                                endDate = event.endDate,
                                repeat = event.repeat,
                                selectedDays = event.repeatDays,
                                repeatDifference = event.repeatDifference,
                                repeatEndDate = event.repeatEndDate,
                                subjectId = event.subjectId,
                                subjectName = event.subjectName,
                                color = event.color,
                                location = EventTextFieldState(text = event.location ?: ""),
                                description = EventTextFieldState(text = event.description),
                            )
                            initialRepeatState = event.repeat
                            currentRepeatId = event.repeatId
                        }
                        fetchSubjects()
                        validateEvent()
                        Log.d(tag, "Event fetched with ID: $currentEventId")
                    }
                } else {
                    Log.d(tag, "Event ID is null, new event is in creation")
                    viewModelScope.launch {
                        Log.d(tag, "Fetching subjects, default event color and semester end date")
                        fetchSubjects()
                        loadDefaultEventColor()
                        Log.d(
                            tag, "Fetching done with values: subjects: ${_state.value.subjects}, " +
                                    "defaultEventColor: ${_defaultEventColor.intValue}"
                        )
                    }
                }
            }

            is EditCalendarEventEvent.SaveEvent -> {
                Log.d(tag, "Saving event")
                viewModelScope.launch {
                    validateEvent()
                    if (!_state.value.isFormValid) {
                        Log.e(tag, "Event is not valid, returning")
                        _eventFlow.emit(UiEvent.ShowSnackbar("Please fix the form errors"))
                        return@launch
                    }
                    if (currentEventId != null) {
                        when (event.saveOption) {
                            SaveOption.THIS -> {
                                Log.d(
                                    tag,
                                    "Saving event with ID: $currentEventId SaveOption: ${event.saveOption}"
                                )
                                try {
                                    eventUseCases.updateEventUseCase(
                                        Event(
                                            id = currentEventId,
                                            title = _state.value.title.text,
                                            allDay = _state.value.allDay,
                                            startTime = _state.value.startTime,
                                            endTime = _state.value.endTime,
                                            startDate = _state.value.startDate,
                                            endDate = _state.value.endDate,
                                            repeat = _state.value.repeat,
                                            repeatDays = _state.value.selectedDays,
                                            repeatId = currentRepeatId,
                                            repeatDifference = _state.value.repeatDifference,
                                            repeatEndDate = _state.value.repeatEndDate,
                                            subjectId = _state.value.subjectId,
                                            subjectName = if (_state.value.subjectId == null) null else _state.value.subjectName,
                                            color = _state.value.color,
                                            location = _state.value.location.text,
                                            description = _state.value.description.text
                                        ),
                                    )
                                    _eventFlow.emit(UiEvent.SaveEvent)
                                } catch (e: InvalidEventException) {
                                    _eventFlow.emit(
                                        UiEvent.ShowSnackbar(
                                            e.message ?: "InvalidEventException occurred"
                                        )
                                    )
                                } catch (e: Exception) {
                                    _eventFlow.emit(UiEvent.ShowSnackbar("An Exception occurred"))
                                } catch (e: Error) {
                                    _eventFlow.emit(UiEvent.ShowSnackbar("An Error occurred"))
                                }
                            }

                            SaveOption.THIS_AND_FUTURE -> {
                                Log.d(
                                    tag,
                                    "Saving event with ID: $currentEventId SaveOption: ${event.saveOption}"
                                )
                                try {
                                    eventUseCases.updateFutureRepeatingEventsUseCase(
                                        Event(
                                            id = currentEventId,
                                            title = _state.value.title.text,
                                            allDay = _state.value.allDay,
                                            startTime = _state.value.startTime,
                                            endTime = _state.value.endTime,
                                            startDate = _state.value.startDate,
                                            endDate = _state.value.endDate,
                                            repeat = _state.value.repeat,
                                            repeatDays = _state.value.selectedDays,
                                            repeatId = currentRepeatId,
                                            repeatDifference = _state.value.repeatDifference,
                                            repeatEndDate = _state.value.repeatEndDate,
                                            subjectId = _state.value.subjectId,
                                            subjectName = _state.value.subjectName,
                                            color = _state.value.color,
                                            location = _state.value.location.text,
                                            description = _state.value.description.text
                                        ),
                                    )
                                    _eventFlow.emit(UiEvent.SaveEvent)
                                } catch (e: InvalidEventException) {
                                    _eventFlow.emit(
                                        UiEvent.ShowSnackbar(
                                            e.message ?: "InvalidEventException occurred"
                                        )
                                    )
                                } catch (e: Exception) {
                                    _eventFlow.emit(UiEvent.ShowSnackbar("An Exception occurred"))
                                } catch (e: Error) {
                                    _eventFlow.emit(UiEvent.ShowSnackbar("An Error occurred"))
                                }
                            }

                            SaveOption.ALL -> {
                                Log.d(
                                    tag,
                                    "Saving event with ID: $currentEventId SaveOption: ${event.saveOption}"
                                )
                                try {
                                    eventUseCases.updateAllRepeatingEventsUseCase(
                                        Event(
                                            id = currentEventId,
                                            title = _state.value.title.text,
                                            allDay = _state.value.allDay,
                                            startTime = _state.value.startTime,
                                            endTime = _state.value.endTime,
                                            startDate = _state.value.startDate,
                                            endDate = _state.value.endDate,
                                            repeat = _state.value.repeat,
                                            repeatDays = _state.value.selectedDays,
                                            repeatId = currentRepeatId,
                                            repeatDifference = _state.value.repeatDifference,
                                            repeatEndDate = _state.value.repeatEndDate,
                                            subjectId = _state.value.subjectId,
                                            subjectName = _state.value.subjectName,
                                            color = _state.value.color,
                                            location = _state.value.location.text,
                                            description = _state.value.description.text
                                        ),
                                    )
                                    _eventFlow.emit(UiEvent.SaveEvent)
                                } catch (e: InvalidEventException) {
                                    _eventFlow.emit(
                                        UiEvent.ShowSnackbar(
                                            e.message ?: "InvalidEventException occurred"
                                        )
                                    )
                                } catch (e: Exception) {
                                    _eventFlow.emit(UiEvent.ShowSnackbar("An Exception occurred"))
                                } catch (e: Error) {
                                    _eventFlow.emit(UiEvent.ShowSnackbar("An Error occurred"))
                                }
                            }
                        }
                    } else {
                        validateEvent()
                        if (!_state.value.isFormValid) {
                            Log.e(tag, "Event is not valid, returning")
                            _eventFlow.emit(UiEvent.ShowSnackbar("Please fix the form errors"))
                            return@launch
                        }
                        try {
                            Log.d(
                                tag,
                                "Saving event with ID: null With add event use case"
                            )
                            eventUseCases.addEventUseCase(
                                Event(
                                    id = null,
                                    title = _state.value.title.text,
                                    allDay = _state.value.allDay,
                                    startTime = _state.value.startTime,
                                    endTime = _state.value.endTime,
                                    startDate = _state.value.startDate,
                                    endDate = _state.value.endDate,
                                    repeat = _state.value.repeat,
                                    repeatDays = _state.value.selectedDays,
                                    repeatId = currentRepeatId,
                                    repeatDifference = _state.value.repeatDifference,
                                    repeatEndDate = _state.value.repeatEndDate,
                                    subjectId = _state.value.subjectId,
                                    subjectName = _state.value.subjectName,
                                    color = _state.value.color,
                                    location = _state.value.location.text,
                                    description = _state.value.description.text
                                ),
                            )
                            _eventFlow.emit(UiEvent.SaveEvent)
                        } catch (e: InvalidEventException) {
                            _eventFlow.emit(
                                UiEvent.ShowSnackbar(
                                    e.message ?: "InvalidEventException occurred"
                                )
                            )
                        } catch (e: Exception) {
                            _eventFlow.emit(UiEvent.ShowSnackbar("An Exception occurred"))
                        } catch (e: Error) {
                            _eventFlow.emit(UiEvent.ShowSnackbar("An Error occurred"))
                        }
                    }
                }
            }

            is EditCalendarEventEvent.EnteredTitle -> {
                _state.value = _state.value.copy(
                    title = _state.value.title.copy(text = event.value)
                )
                validateEvent()
                Log.d(tag, "Title entered: ${_state.value.title.text}")
            }

            is EditCalendarEventEvent.EnteredAllDay -> {
                _state.value = _state.value.copy(allDay = event.value)
                validateEvent()
                Log.d(tag, "All-day entered: ${_state.value.allDay}")
            }

            is EditCalendarEventEvent.EnteredStartTime -> {
                _state.value = _state.value.copy(
                    startTime = event.value,
                    endTime = event.value.plusHours(_state.value.repeatDifference)
                )
                validateEvent()
                Log.d(tag, "Start time entered: ${_state.value.startTime}")
            }

            is EditCalendarEventEvent.EnteredEndTime -> {
                _state.value = _state.value.copy(
                    endTime = event.value
                )
                validateEvent()
                Log.d(tag, "End time entered: ${_state.value.endTime}")
            }

            is EditCalendarEventEvent.EnteredStartDate -> {
                _state.value = _state.value.copy(
                    startDate = event.value,
                    endDate = event.value.plusDays(_state.value.repeatDifference)
                )
                validateEvent()
                Log.d(tag, "Start date entered: ${_state.value.startDate}")
            }

            is EditCalendarEventEvent.EnteredEndDate -> {
                _state.value = _state.value.copy(
                    endDate = event.value
                )
                validateEvent()
                Log.d(tag, "End date entered: ${_state.value.endDate}")
            }

            is EditCalendarEventEvent.EnteredRepeat -> {
                _state.value = _state.value.copy(repeat = event.value)
                validateEvent()
                Log.d(tag, "Repeat entered: ${_state.value.repeat}")
            }

            is EditCalendarEventEvent.EnteredSelectedDays -> {
                _state.value = _state.value.copy(selectedDays = event.value)
                validateEvent()
                Log.d(tag, "Selected days entered: ${_state.value.selectedDays}")
            }

            is EditCalendarEventEvent.EnteredRepeatDifference -> {
                _state.value = _state.value.copy(repeatDifference = event.value)
                validateEvent()
                Log.d(tag, "Repeat difference entered: ${_state.value.repeatDifference}")
            }

            is EditCalendarEventEvent.EnteredRepeatEndDate -> {
                _state.value = _state.value.copy(repeatEndDate = event.value)
                validateEvent()
                Log.d(tag, "Repeat end date entered: ${_state.value.repeatEndDate}")
            }

            is EditCalendarEventEvent.EnteredSubject -> {
                fetchSubjects()
                val subject = _state.value.subjects.find { it.id == event.value }
                _state.value = _state.value.copy(
                    subjectId = event.value,
                    subjectName = subject?.title
                )
                Log.d(
                    tag,
                    "Subject entered: id: ${_state.value.subjectId}, name: ${_state.value.subjectName} "
                )
            }

            is EditCalendarEventEvent.EnteredColor -> {
                _state.value = _state.value.copy(color = event.value)
                Log.d(tag, "Color entered: ${_state.value.color}")
            }

            is EditCalendarEventEvent.EnteredLocation -> {
                _state.value =
                    _state.value.copy(location = _state.value.location.copy(text = event.value))
                Log.d(tag, "Location entered: ${_state.value.location.text}")
            }

            is EditCalendarEventEvent.EnteredDescription -> {
                _state.value =
                    _state.value.copy(description = _state.value.description.copy(text = event.value))
                Log.d(tag, "Description entered: ${_state.value.description.text}")
            }

            is EditCalendarEventEvent.ValidateEvent -> {
                validateEvent()
                Log.d(tag, "Event validated")
            }

            is EditCalendarEventEvent.FetchSubjects -> {
                fetchSubjects()
                Log.d(tag, "Subjects fetched")
            }

            is EditCalendarEventEvent.GetDefaultEventColor -> {
                viewModelScope.launch {
                    loadDefaultEventColor()
                    Log.d(tag, "Default event color loaded")
                }
            }

            // Initialize date and time fields, same as 'Entered' but without validation
            is EditCalendarEventEvent.InitializeStartDate -> {
                _state.value = _state.value.copy(
                    startDate = event.value,
                    endDate = event.value.plusDays(_state.value.repeatDifference)
                )
                Log.d(tag, "Start date initialized: ${_state.value.startDate}")
            }

            is EditCalendarEventEvent.InitializeEndDate -> {
                _state.value = _state.value.copy(
                    endDate = event.value
                )
                Log.d(tag, "End date initialized: ${_state.value.endDate}")
            }

            is EditCalendarEventEvent.SetCurrentSemesterEndDate -> {
                viewModelScope.launch {
                    setCurrentSemesterEndDate()
                    Log.d(tag, "Semester end date set to current: ${_state.value.repeatEndDate}")
                }
            }
        }
    }

    private fun fetchSubjects() {
        Log.d(tag, "Fetching subjects")
        viewModelScope.launch {
            try {
                subjectUseCases.getAllSubjects().collect { allSubjects ->

                    val currentSemester = dataStore.data.first()[currentSemesterKey] ?: 1

                    // Get current subject ID from state
                    val currentSubjectId = _state.value.subjectId

                    // Always include the existing subject in filtered results
                    val semesterFiltered =
                        allSubjects.filter { it.semester == currentSemester }
                    val currentSubject = allSubjects.find { it.id == currentSubjectId }

                    val combinedSubjects = semesterFiltered.toMutableList().apply {
                        // Add current subject if not already in list
                        currentSubject?.takeIf { !contains(it) }?.let { add(it) }
                    }

                    _state.value = _state.value.copy(
                        subjects = allSubjects,
                        filteredSubjects = combinedSubjects
                    )
                    Log.d(
                        tag, "Subjects fetched with values: ${_state.value.subjects}, " +
                                "filteredSubjects: ${_state.value.filteredSubjects}"
                    )
                }
            } catch (e: Exception) {
                Log.e(tag, "Error fetching subjects", e)
                _eventFlow.emit(UiEvent.ShowSnackbar("Error fetching subjects"))
            } catch (e: Error) {
                Log.e(tag, "Error fetching subjects", e)
                _eventFlow.emit(UiEvent.ShowSnackbar("Error fetching subjects"))
            }
        }
    }

    private fun validateEvent() {
        Log.d(tag, "Validating event")
        val newState = _state.value.copy(
            dateTimeError = null,
            repeatError = null,
            title = _state.value.title.copy(error = null),
            isFormValid = false
        )

        // Title validation
        val titleResult = eventUseCases.validateTitleUseCase.execute(newState.title.text)
        val updatedTitle = newState.title.copy(
            error = titleResult.errorMessage.takeIf { !titleResult.successful }
        )

        // DateTime validation
        val dateTimeResult = eventUseCases.validateDateTimeUseCase.execute(
            newState.startDate,
            newState.endDate,
            newState.startTime,
            newState.endTime,
            newState.allDay
        )

        // Repeat validation
        val repeatResult = eventUseCases.validateRepeatUseCase.execute(
            newState.startDate, newState.repeatEndDate
        )

        // Repeat difference validation
        val repeatDifferenceValid = newState.repeat == Repeat.NONE || newState.repeatDifference >= 1
        val repeatDifferenceError =
            if (!repeatDifferenceValid) "Interval must be at least 1" else null

        _state.value = newState.copy(
            title = updatedTitle,
            dateTimeError = dateTimeResult.errorMessage,
            repeatError = repeatResult.errorMessage ?: repeatDifferenceError,
            isFormValid = updatedTitle.error == null &&
                    dateTimeResult.successful &&
                    (repeatResult.successful && repeatDifferenceValid) &&
                    newState.title.text.isNotBlank()
        )
        Log.d(
            tag,
            "Event validated with values: title: ${_state.value.title}, dateTimeError: ${_state.value.dateTimeError}, " +
                    "repeatError: ${_state.value.repeatError}, isFormValid: ${_state.value.isFormValid}"
        )
    }

    private suspend fun getCurrentSemesterEndDate(): LocalDate? {
        return try {
            val currentSemester = dataStore.data.first()[currentSemesterKey] ?: 1
            val semesterEndDatesStr = dataStore.data.first()[semesterEndDatesKey]

            Log.d(tag, "Current Semester: $currentSemester")
            Log.d(tag, "Semester End Dates String: $semesterEndDatesStr")

            semesterEndDatesStr?.split(",")?.associate {
                val (semesterStr, millisStr) = it.split(":")
                semesterStr.toInt() to millisStr.toLong()
            }?.get(currentSemester)?.let { millis ->
                Instant.ofEpochMilli(millis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .also { Log.d(tag, "Parsed End Date: $it") }
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting current semester end date", e)
            _eventFlow.emit(UiEvent.ShowSnackbar("Error getting current semester end date"))
            null
        }
    }

    private suspend fun setCurrentSemesterEndDate() {
        try {

            val newRepeatEndDate = getCurrentSemesterEndDate()
            val newRepeatPlusWeek = _state.value.endDate.plusWeeks(1)

            if (newRepeatEndDate == null || newRepeatEndDate.isAfter(_state.value.endDate) || newRepeatEndDate.isEqual(
                    _state.value.endDate
                )
            ) {
                _state.value = _state.value.copy(
                    repeatEndDate = newRepeatEndDate ?: _state.value.endDate.plusWeeks(1)
                )
            } else if (newRepeatEndDate.isBefore(_state.value.startDate)) {
                _state.value = _state.value.copy(repeatEndDate = newRepeatPlusWeek)
            } else {
                _state.value = _state.value.copy(repeatEndDate = newRepeatPlusWeek)
            }

            Log.d(tag, "Updated repeat end date: ${_state.value.repeatEndDate}")
        } catch (e: Exception) {
            Log.e(tag, "Error setting semester end date", e)
            Log.d(tag, "Setting repeat end date to default: ${_state.value.endDate.plusWeeks(1)}")
            _state.value = _state.value.copy(repeatEndDate = _state.value.endDate.plusWeeks(1))
            _eventFlow.emit(UiEvent.ShowSnackbar("Failed to update semester end date"))
        } catch (e: Error) {
            Log.e(tag, "Error setting semester end date", e)
            Log.d(tag, "Setting repeat end date to default: ${_state.value.endDate.plusWeeks(1)}")
            _state.value = _state.value.copy(repeatEndDate = _state.value.endDate.plusWeeks(1))
            _eventFlow.emit(UiEvent.ShowSnackbar("Failed to update semester end date"))
        }
    }

    private suspend fun loadDefaultEventColor() {
        // Change from map/collect to first() for immediate value
        val defaultColor = dataStore.data.map { preferences ->
            preferences[defaultEventColorKey] ?: 0
        }.first()
        _defaultEventColor.intValue = defaultColor
        _state.value = _state.value.copy(color = defaultColor)
        Log.d(tag, "Default event color loaded: ${_state.value.color}")
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        data object SaveEvent : UiEvent()
    }

}

