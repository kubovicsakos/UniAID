package com.kakos.uniAID.notes.presentation.notes

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakos.uniAID.notes.domain.model.InvalidNoteException
import com.kakos.uniAID.notes.domain.model.Note
import com.kakos.uniAID.notes.domain.use_case.NoteUseCases
import com.kakos.uniAID.notes.domain.util.NoteOrder
import com.kakos.uniAID.notes.domain.util.OrderType
import com.kakos.uniAID.notes.presentation.notes.util.NotesEvent
import com.kakos.uniAID.notes.presentation.notes.util.NotesState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for notes management.
 *
 * Manages state and business logic for the UI.
 * Interacts with use cases to perform data operations.
 *
 * @property noteUseCases Use cases for note operations (get, add, delete).
 */
@HiltViewModel
class NotesViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases
) : ViewModel() {

    private val tag = "NotesViewModel"

    // States
    private val _state = mutableStateOf(NotesState())
    val state: State<NotesState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var recentlyDeletedNote: Note? = null

    private var getNotesJob: Job? = null

    // Initialize the notes
    init {
        Log.d(tag, "Initializing")
        Log.d(tag, "Getting notes with order: ${NoteOrder.Date(OrderType.Descending)}")
        getNotes(NoteOrder.Date(OrderType.Descending))
        Log.d(tag, "Initialization done")
    }

    // Function to handle events
    fun onEvent(event: NotesEvent) {
        when (event) {
            is NotesEvent.Order -> { // Order the notes
                Log.d(tag, "Order event received")
                if (state.value.noteOrder::class == event.noteOrder::class &&
                    state.value.noteOrder.orderType == event.noteOrder.orderType
                ) {
                    Log.d(tag, "Order event ignored")
                    return
                }
                Log.d(tag, "Order event processed with order: ${event.noteOrder}")
                getNotes(event.noteOrder)
            }

            is NotesEvent.DeleteNote -> {
                Log.d(tag, "Delete event received")
                viewModelScope.launch {
                    try {
                        noteUseCases.deleteNoteUseCase(event.note)
                        recentlyDeletedNote = event.note
                        Log.d(tag, "Note deleted: ${event.note}")
                    } catch (e: IllegalArgumentException) {
                        Log.d(
                            tag,
                            "IllegalArgumentException: While fetching note with id: ${event.note.id}. ${e.message}"
                        )
                        _eventFlow.emit(UiEvent.ShowSnackbar("Cannot delete note, ID is invalid"))
                    } catch (e: Exception) {
                        _eventFlow.emit(UiEvent.ShowSnackbar("An exception occurred, ${e.message}, ${e.cause}"))
                        Log.d(tag, "EXCEPTION: ${e.message}, ${e.cause}")
                    } catch (e: Error) {
                        _eventFlow.emit(UiEvent.ShowSnackbar("An error occurred, ${e.message}, ${e.cause}"))
                        Log.d(tag, "ERROR: ${e.message}, ${e.cause}")
                    }
                }
            }

            is NotesEvent.RestoreNote -> { // Restore a recently deleted note
                viewModelScope.launch {
                    try {
                        Log.d(tag, "Restore event received")
                        noteUseCases.addNoteUseCase(recentlyDeletedNote ?: return@launch)
                        Log.d(tag, "Note restored: $recentlyDeletedNote")
                        recentlyDeletedNote = null
                    } catch (e: InvalidNoteException) {
                        _eventFlow.emit(
                            UiEvent.ShowSnackbar(
                                message = e.message ?: "Couldn't restore note"
                            )
                        )
                        Log.d(tag, "EXCEPTION: ${e.message ?: "Couldn't restore note"}")
                    } catch (e: Exception) {
                        _eventFlow.emit(UiEvent.ShowSnackbar("An exception occurred, ${e.message}, ${e.cause}"))
                        Log.d(tag, "EXCEPTION: ${e.message}, ${e.cause}")
                    } catch (e: Error) {
                        _eventFlow.emit(UiEvent.ShowSnackbar("An error occurred, ${e.message}, ${e.cause}"))
                        Log.d(tag, "ERROR: ${e.message}, ${e.cause}")
                    }
                }
            }

            is NotesEvent.ToggleOrderSection -> { // Toggle the order section
                Log.d(tag, "Toggle event received")
                _state.value = state.value.copy(
                    isOrderSectionVisible = !state.value.isOrderSectionVisible
                )
                Log.d(tag, "Order section toggled to ${state.value.isOrderSectionVisible}")
            }
        }
    }

    private fun getNotes(noteOrder: NoteOrder) { // Get the notes with the specified order
        Log.d(tag, "Get notes event received with order: $noteOrder")
        getNotesJob?.cancel()
        try {
            getNotesJob = noteUseCases.getNotesUseCase(noteOrder)
                .onEach { notes ->
                    _state.value = state.value.copy(
                        notes = notes,
                        noteOrder = noteOrder,
                        isLoading = false
                    )
                }
                .launchIn(viewModelScope)
            Log.d(tag, "Notes retrieved")
        } catch (e: Exception) {
            Log.d(tag, "EXCEPTION: ${e.message}, ${e.cause}")
            _eventFlow.tryEmit(UiEvent.ShowSnackbar("Exception occurred, while fetching notes"))
        } catch (e: Error) {
            Log.d(tag, "ERROR: ${e.message}, ${e.cause}")
            _eventFlow.tryEmit(UiEvent.ShowSnackbar("Error occurred, while fetching notes"))
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
    }
}