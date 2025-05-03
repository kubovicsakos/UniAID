package com.kakos.uniAID.calendar.presentation.event_details

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakos.uniAID.calendar.domain.use_case.EventUseCases
import com.kakos.uniAID.calendar.domain.util.DeleteOption
import com.kakos.uniAID.calendar.presentation.event_details.util.EventDetailsEvent
import com.kakos.uniAID.calendar.presentation.event_details.util.EventDetailsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for event details screen.
 *
 * Manages state and business logic for the UI.
 * Interacts with use cases to perform data operations
 * including fetching event details and handling deletion options.
 *
 * @property eventUseCases Repository for accessing and manipulating event data.
 */
@HiltViewModel
class EventDetailsViewModel @Inject constructor(
    private val eventUseCases: EventUseCases,
) : ViewModel() {

    private val tag = "EventDetailsViewModel"

    private val _state = mutableStateOf(EventDetailsState())
    val state: State<EventDetailsState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var currentEventId: Int? = null

    fun onEvent(event: EventDetailsEvent) {
        when (event) {
            is EventDetailsEvent.GetEventById -> {
                Log.d(tag, "GetEventById: ${event.eventId}")
                currentEventId = event.eventId
                viewModelScope.launch {
                    try {
                        eventUseCases.getEventByIdUseCase(event.eventId)?.also { event ->
                            _state.value = state.value.copy(event = event)
                        }
                    } catch (e: Exception) {
                        Log.e(tag, "EXCEPTION: An Exception occurred", e)
                    } catch (e: Error) {
                        Log.e(tag, "ERROR: An Error occurred", e)
                    }
                }
            }

            is EventDetailsEvent.DeleteEvent -> {
                when (event.deleteOption) {
                    DeleteOption.THIS -> {
                        viewModelScope.launch {
                            Log.d(
                                tag,
                                "DeleteEvent with delete option: ${event.deleteOption} with id: ${event.event.id}"
                            )
                            try {
                                eventUseCases.deleteEventUseCase(event.event)
                            } catch (e: IllegalArgumentException) {
                                Log.e(tag, "IllegalArgumentException: ${e.message}", e)
                                _eventFlow.emit(UiEvent.ShowSnackbar("An illegal argument: ${e.message}"))
                            } catch (e: Exception) {
                                Log.e(tag, "EXCEPTION: An Exception occurred ${e.message}", e)
                                _eventFlow.emit(UiEvent.ShowSnackbar("An exception occurred while deleting the event"))
                            } catch (e: Error) {
                                Log.e(tag, "ERROR: An Error occurred ${e.message}", e)
                                _eventFlow.emit(UiEvent.ShowSnackbar("An error occurred while deleting the event"))
                            }
                        }
                    }

                    DeleteOption.THIS_AND_FUTURE -> {
                        viewModelScope.launch {
                            Log.d(
                                tag,
                                "DeleteEvent with delete option: ${event.deleteOption} with repeatId: ${event.event.repeatId} " +
                                        "and startDate: ${event.event.startDate}"
                            )
                            try {
                                eventUseCases.deleteRepeatFromDateUseCase(
                                    event.event.repeatId!!,
                                    event.event.startDate
                                )
                            } catch (e: IllegalArgumentException) {
                                Log.e(tag, "IllegalArgumentException: ${e.message}", e)
                                _eventFlow.emit(UiEvent.ShowSnackbar("An illegal argument: ${e.message}"))
                            } catch (e: Exception) {
                                Log.e(tag, "EXCEPTION: An Exception occurred ${e.message}", e)
                                _eventFlow.emit(UiEvent.ShowSnackbar("An exception occurred while deleting the event"))
                            } catch (e: Error) {
                                Log.e(tag, "ERROR: An Error occurred ${e.message}", e)
                                _eventFlow.emit(UiEvent.ShowSnackbar("An error occurred while deleting the event"))
                            }
                        }
                    }

                    DeleteOption.ALL -> {
                        viewModelScope.launch {
                            Log.d(
                                tag,
                                "DeleteEvent with delete option: ${event.deleteOption} with repeatId: ${event.event.repeatId}"
                            )
                            try {
                                eventUseCases.deleteAllRepeatUseCase(event.event.repeatId!!)
                            } catch (e: IllegalArgumentException) {
                                Log.e(tag, "IllegalArgumentException: ${e.message}", e)
                                _eventFlow.emit(UiEvent.ShowSnackbar("An illegal argument: ${e.message}"))
                            } catch (e: Exception) {
                                Log.e(tag, "EXCEPTION: An Exception occurred ${e.message}", e)
                                _eventFlow.emit(UiEvent.ShowSnackbar("An exception occurred while deleting the event"))
                            } catch (e: Error) {
                                Log.e(tag, "ERROR: An Error occurred ${e.message}", e)
                                _eventFlow.emit(UiEvent.ShowSnackbar("An error occurred while deleting the event"))
                            }
                        }
                    }
                }
            }
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
    }
}