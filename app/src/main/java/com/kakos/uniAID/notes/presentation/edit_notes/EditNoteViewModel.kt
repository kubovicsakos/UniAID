package com.kakos.uniAID.notes.presentation.edit_notes

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakos.uniAID.core.domain.subject.domain.use_case.SubjectUseCases
import com.kakos.uniAID.notes.domain.model.InvalidNoteException
import com.kakos.uniAID.notes.domain.model.Note
import com.kakos.uniAID.notes.domain.use_case.NoteUseCases
import com.kakos.uniAID.notes.presentation.edit_notes.util.EditNoteEvent
import com.kakos.uniAID.notes.presentation.edit_notes.util.EditNoteState
import com.kakos.uniAID.notes.presentation.edit_notes.util.NoteTextFieldState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * ViewModel for edit note screen.
 *
 * Manages state and business logic for the UI.
 * Interacts with use cases to perform data operations.
 *
 * @property noteUseCases Provides operations for note management.
 * @property subjectUseCases Provides operations for subject retrieval.
 * @property savedStateHandle Handles note ID persistence across configuration changes.
 * @property dataStore Stores user preferences like current semester.
 */
@HiltViewModel
class EditNoteViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases,
    private val subjectUseCases: SubjectUseCases,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val tag = "EditNoteViewModel"
    private val currentSemesterKey = intPreferencesKey("current_semester")

    private val _state = mutableStateOf(EditNoteState())
    val state: State<EditNoteState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var currentNoteId: Int? = null

    init {
        Log.d(tag, "Get current semester")
        viewModelScope.launch {
            _state.value = _state.value.copy(
                currentSemester = getCurrentSemester(),
            )
        }
        fetchSubjects()
        Log.d(tag, "Fetch subjects with current semester: ${_state.value.currentSemester}")
        Log.d(tag, "Initialization done")
    }

    fun onEvent(event: EditNoteEvent) {
        when (event) {
            is EditNoteEvent.GetNote -> {
                viewModelScope.launch {
                    try {
                        if (event.id != -1) {
                            noteUseCases.getNoteUseCase(event.id)?.also { note ->
                                Log.d(tag, "Loaded darkTheme=${note.darkTheme}")
                                currentNoteId = note.id
                                _state.value = _state.value.copy(
                                    title = NoteTextFieldState(
                                        text = note.title,
                                        isHintVisible = false
                                    ),
                                    content = NoteTextFieldState(
                                        text = note.content,
                                        isHintVisible = false
                                    ),
                                    isDarkTheme = note.darkTheme,
                                    isNewNote = false,
                                    subjectId = note.subjectId,
                                    subjectName = note.subjectName,
                                    creationTime = note.creationTime,
                                    lastModified = note.lastModified
                                )
                            }
                        } else {
                            Log.d(tag, "Note with id: ${event.id} not found, creating new note")
                        }
                    } catch (e: IllegalArgumentException) {
                        Log.e(
                            tag,
                            "IllegalArgumentException: While fetching note with id: ${event.id}. ${e.message}"
                        )
                        _eventFlow.emit(UiEvent.ShowSnackbar("Cant load note with id: ${event.id}"))
                    } catch (e: Exception) {
                        Log.e(
                            tag,
                            "EXCEPTION: While fetching note with id: ${event.id}. ${e.message}, ${e.cause}"
                        )
                        _eventFlow.emit(UiEvent.ShowSnackbar("An exception occurred, ${e.message}, ${e.cause}"))
                    } catch (e: Error) {
                        Log.e(
                            tag,
                            "ERROR: While fetching note with id: ${event.id}. ${e.message}, ${e.cause}"
                        )
                        _eventFlow.emit(UiEvent.ShowSnackbar("An error occurred, ${e.message}, ${e.cause}"))
                    }
                }
            }

            is EditNoteEvent.EnteredTitle -> {
                _state.value = _state.value.copy(
                    title = _state.value.title.copy(text = event.value)
                )
                validateForm()
                Log.d(tag, "Entered title: ${_state.value.title.text}")
            }

            is EditNoteEvent.ChangeTitleFocus -> {
                _state.value = _state.value.copy(
                    title = _state.value.title.copy(
                        isHintVisible = !event.focusState.isFocused &&
                                _state.value.title.text.isBlank()
                    )
                )
                Log.d(tag, "Change title focus: ${_state.value.title.isHintVisible}")
            }

            is EditNoteEvent.EnteredContent -> {
                _state.value = _state.value.copy(
                    content = _state.value.content.copy(text = event.value)
                )
                validateForm()
                Log.d(tag, "Entered content: ${_state.value.content.text}")
            }

            is EditNoteEvent.ChangeContentFocus -> {
                _state.value = _state.value.copy(
                    content = _state.value.content.copy(
                        isHintVisible = !event.focusState.isFocused &&
                                _state.value.content.text.isBlank()
                    )
                )
                Log.d(tag, "Change content focus: ${_state.value.content.isHintVisible}")
            }

            is EditNoteEvent.ToggleDarkTheme -> {
                _state.value = _state.value.copy(
                    isDarkTheme = !_state.value.isDarkTheme
                )
                Log.d(tag, "Toggle dark theme: ${_state.value.isDarkTheme}")
            }

            is EditNoteEvent.SelectSubject -> {
                fetchSubjects()
                val subject = _state.value.subjects.find { it.id == event.subjectId }
                _state.value = _state.value.copy(
                    subjectId = event.subjectId,
                    subjectName = subject?.title
                )
                Log.d(tag, "Selected subject: ${_state.value.subjectName}")
            }

            is EditNoteEvent.DeleteNote -> {
                viewModelScope.launch {
                    try {
                        noteUseCases.deleteNoteByIdUseCase(event.id)
                        Log.d(tag, "Deleted note with id: ${event.id}")
                    } catch (e: IllegalArgumentException) {
                        Log.d(
                            tag,
                            "IllegalArgumentException: While deleting note with id: ${event.id}. ${e.message}"
                        )
                        _eventFlow.emit(UiEvent.ShowSnackbar("Couldn't delete note"))
                    } catch (e: Exception) {
                        _eventFlow.emit(UiEvent.ShowSnackbar("An exception occurred, ${e.message}, ${e.cause}"))
                        Log.d(tag, "EXCEPTION: ${e.message}, ${e.cause}")
                    } catch (e: Error) {
                        _eventFlow.emit(UiEvent.ShowSnackbar("An error occurred, ${e.message}, ${e.cause}"))
                        Log.d(tag, "ERROR: ${e.message}, ${e.cause}")
                    }

                }
            }

            is EditNoteEvent.SaveNote -> {
                Log.d(tag, "Saving note called")
                viewModelScope.launch {
                    validateForm()
                    if (!_state.value.isFormValid) {
                        _eventFlow.emit(UiEvent.ShowSnackbar("Title can't be empty"))
                        return@launch
                    }
                    try {
                        val note = Note(
                            title = _state.value.title.text,
                            content = _state.value.content.text,
                            creationTime = _state.value.creationTime,
                            lastModified = LocalDateTime.now(),
                            darkTheme = _state.value.isDarkTheme,
                            subjectId = _state.value.subjectId,
                            subjectName = _state.value.subjectName,
                            id = currentNoteId
                        )

                        if (_state.value.isNewNote) {
                            noteUseCases.addNoteUseCase(note)
                            Log.d(tag, "Saving darkTheme=${note.darkTheme}_add_note")
                        } else {
                            noteUseCases.updateNoteUseCase(note)
                            Log.d(tag, "Saving darkTheme=${note.darkTheme}_update_note")
                        }
                        _eventFlow.emit(UiEvent.SaveNote)
                    } catch (e: InvalidNoteException) {
                        _eventFlow.emit(
                            UiEvent.ShowSnackbar(
                                message = e.message ?: "Couldn't save note"
                            )
                        )
                        Log.d(tag, "EXCEPTION: ${e.message ?: "Couldn't save note"}")
                    } catch (e: Exception) {
                        _eventFlow.emit(UiEvent.ShowSnackbar("An exception occurred, ${e.message}, ${e.cause}"))
                        Log.d(tag, "EXCEPTION: ${e.message}, ${e.cause}")
                    } catch (e: Error) {
                        _eventFlow.emit(UiEvent.ShowSnackbar("An error occurred, ${e.message}, ${e.cause}"))
                        Log.d(tag, "ERROR: ${e.message}, ${e.cause}")
                    }
                }
            }
        }
    }

    private fun fetchSubjects() {
        Log.d(tag, "Fetching subjects")
        viewModelScope.launch {
            subjectUseCases.getAllSubjects().collect { allSubjects ->
                // Get current subject ID from state
                val currentSubjectId = _state.value.subjectId

                // Always include the existing subject in filtered results
                val semesterFiltered =
                    allSubjects.filter { it.semester == _state.value.currentSemester }
                val currentSubject = allSubjects.find { it.id == currentSubjectId }

                val combinedSubjects = semesterFiltered.toMutableList().apply {
                    // Add current subject if not already in list
                    currentSubject?.takeIf { !contains(it) }?.let { add(it) }
                }

                _state.value = _state.value.copy(
                    subjects = allSubjects,
                    filteredSubjects = combinedSubjects
                )
            }
            Log.d(tag, "Fetched subjects: ${_state.value.subjects}")
        }
    }

    private fun validateForm() {
        Log.d(tag, "Validating form")

        val titleResult = noteUseCases.validateNoteTitleUseCase.execute(_state.value.title.text)

        _state.value = _state.value.copy(
            isFormValid = titleResult.successful,
        )

        Log.d(tag, "Is form valid?: ${_state.value.isFormValid}")
    }

    private suspend fun getCurrentSemester(): Int {
        Log.d(tag, "Getting current semester")
        return dataStore.data.first()[currentSemesterKey] ?: 1
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        data object SaveNote : UiEvent()
    }
}
