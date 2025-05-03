package com.kakos.uniAID.subjectAndStats.presentation.subject.subject_details

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakos.uniAID.calendar.domain.model.Repeat
import com.kakos.uniAID.core.domain.subject.domain.use_case.SubjectUseCases
import com.kakos.uniAID.subjectAndStats.presentation.subject.subject_details.util.SubjectDetailsEvent
import com.kakos.uniAID.subjectAndStats.presentation.subject.subject_details.util.SubjectDetailsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Subject Details screen.
 *
 * Manages state and business logic for the UI.
 * Interacts with use cases to perform data operations.
 *
 * @property subjectUseCases Use cases for interacting with subject-related data.
 */
@HiltViewModel
class SubjectDetailsViewModel @Inject constructor(
    private val subjectUseCases: SubjectUseCases
) : ViewModel() {

    private val tag = "SubjectDetailsViewModel"

    private val _state = mutableStateOf(SubjectDetailsState())
    val state: State<SubjectDetailsState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var currentSubjectId: Int? = null

    fun onEvent(event: SubjectDetailsEvent) {
        when (event) {
            is SubjectDetailsEvent.GetSubjectById -> {
                Log.d(tag, "GetSubjectById event received with subjectId: ${event.subjectId}")
                currentSubjectId = event.subjectId
                viewModelScope.launch {
                    if (currentSubjectId == null) {
                        _state.value = _state.value.copy(error = "Invalid subject id")
                        return@launch
                    } else {
                        try {
                            subjectUseCases.getSubjectById(currentSubjectId!!)?.also { subject ->
                                _state.value = _state.value.copy(subject = subject)
                            }
                            getNotesOfSubject(currentSubjectId!!)
                            getEventsOfSubject(currentSubjectId!!)
                            Log.d(tag, "Subject fetched: ${state.value.subject}")
                            Log.d(tag, "Notes fetched: ${state.value.notes}")
                            Log.d(tag, "Events fetched: ${state.value.events}")
                        } catch (e: IllegalArgumentException) {
                            Log.e(tag, "IllegalArgumentException: Invalid subject id", e)
                            _eventFlow.emit(UiEvent.ShowSnackbar("Failed to fetch subject details (Invalid subject id)"))
                            return@launch
                        } catch (e: Exception) {
                            Log.e(tag, "EXCEPTION: Failed to fetch subject details", e)
                            _eventFlow.emit(UiEvent.ShowSnackbar("Failed to fetch subject details"))
                            return@launch
                        } catch (e: Error) {
                            Log.e(tag, "ERROR: Failed to fetch subject details", e)
                            _eventFlow.emit(UiEvent.ShowSnackbar("Failed to fetch subject details"))
                            return@launch
                        }
                    }
                }
            }

            is SubjectDetailsEvent.DeleteSubject -> {
                Log.d(tag, "DeleteSubject event received with subject: ${event.subject}")
                viewModelScope.launch {
                    try {
                        subjectUseCases.deleteSubject(event.subject)
                        Log.d(tag, "Subject deleted with id: ${event.subject.id}")
                    } catch (e: IllegalArgumentException) {
                        Log.e(tag, "IllegalArgumentException: Invalid subject id", e)
                        _eventFlow.emit(UiEvent.ShowSnackbar("Failed to delete subject (Invalid subject id)"))
                        return@launch
                    } catch (e: Exception) {
                        Log.e(tag, "EXCEPTION: Failed to delete subject", e)
                        _eventFlow.emit(UiEvent.ShowSnackbar("Failed to delete subject"))
                        return@launch
                    } catch (e: Error) {
                        Log.e(tag, "ERROR: Failed to delete subject", e)
                        _eventFlow.emit(UiEvent.ShowSnackbar("Failed to delete subject"))
                        return@launch
                    }
                }
            }
        }
    }

    private fun getNotesOfSubject(subjectId: Int) {
        Log.d(tag, "Fetching notes for subjectId: $subjectId")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                subjectUseCases.getNotesOfSubject(subjectId)
                    .onEach { notes ->
                        Log.d("ViewModel", "Notes fetched: ${notes.size}")
                        _state.value = state.value.copy(notes = notes)
                    }
                    .launchIn(viewModelScope)
                Log.d(tag, "Notes fetched for subjectId: $subjectId")
            } catch (e: Exception) {
                Log.e(tag, "EXCEPTION: Failed to fetch notes", e)
                _eventFlow.emit(UiEvent.ShowSnackbar("Failed to fetch notes"))
            } catch (e: Error) {
                Log.e(tag, "ERROR: Failed to fetch notes", e)
                _eventFlow.emit(UiEvent.ShowSnackbar("Failed to fetch notes"))
            } catch (e: IllegalArgumentException) {
                Log.e(tag, "IllegalArgumentException: Invalid subject id", e)
                _eventFlow.emit(UiEvent.ShowSnackbar("Failed to fetch notes (Invalid subject id)"))
            }

        }
    }

    private fun getEventsOfSubject(subjectId: Int) {
        Log.d(tag, "Fetching events for subjectId: $subjectId")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                subjectUseCases.getEventsOfSubject(subjectId)
                    .onEach { allEvents ->
                        Log.d("ViewModel", "Events fetched: ${allEvents.size}")

                        val uniqueEvents = allEvents.groupBy {
                            if (it.repeat != Repeat.NONE) {
                                // Group repeating events by their title and repeat type
                                "${it.title}-${it.repeat}"
                            } else {
                                // Unique identifier for non-repeating events
                                it.id
                            }
                        }.map { group ->
                            group.value.first()
                        }
                        _state.value = state.value.copy(events = uniqueEvents)
                    }
                    .launchIn(viewModelScope)
                Log.d(tag, "Events fetched for subjectId: $subjectId")
            } catch (e: Exception) {
                Log.e(tag, "EXCEPTION: Failed to fetch notes", e)
                _eventFlow.emit(UiEvent.ShowSnackbar("Failed to fetch notes"))
            } catch (e: Error) {
                Log.e(tag, "ERROR: Failed to fetch notes", e)
                _eventFlow.emit(UiEvent.ShowSnackbar("Failed to fetch notes"))
            } catch (e: IllegalArgumentException) {
                Log.e(tag, "IllegalArgumentException: Invalid subject id", e)
                _eventFlow.emit(UiEvent.ShowSnackbar("Failed to fetch notes (Invalid subject id)"))
            }
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
    }
}