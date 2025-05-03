package com.kakos.uniAID.subjectAndStats.presentation.subject.edit_subjects

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakos.uniAID.core.domain.subject.domain.model.InvalidSubjectException
import com.kakos.uniAID.core.domain.subject.domain.model.Subject
import com.kakos.uniAID.core.domain.subject.domain.use_case.SubjectUseCases
import com.kakos.uniAID.subjectAndStats.presentation.subject.edit_subjects.util.EditSubjectEvent
import com.kakos.uniAID.subjectAndStats.presentation.subject.edit_subjects.util.EditSubjectState
import com.kakos.uniAID.subjectAndStats.presentation.subject.edit_subjects.util.SubjectTextFieldState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Edit Subject screen.
 *
 * Manages state and business logic for the UI.
 * Interacts with use cases to perform data operations.
 *
 * @property subjectUseCases Instance for accessing subject-related business logic.
 * @property dataStore Instance for accessing application preferences.
 */
@HiltViewModel
class EditSubjectViewModel @Inject constructor(
    private val subjectUseCases: SubjectUseCases,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val tag = "EditSubjectViewModel"

    private val _state = mutableStateOf(EditSubjectState())
    val state: State<EditSubjectState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var currentSubjectId: Int? = null
    private val currentSemesterKey = intPreferencesKey("current_semester")

    fun onEvent(event: EditSubjectEvent) {
        when (event) {
            is EditSubjectEvent.GetSubjectById -> {
                Log.d(tag, "GetSubjectById called with: ${event.subjectId}")
                currentSubjectId = if (event.subjectId == -1) null else event.subjectId
                if (currentSubjectId != null) {
                    Log.d(tag, "currentSubjectId is not nul, fetching subject")
                    viewModelScope.launch {
                        try {
                            subjectUseCases.getSubjectById(currentSubjectId!!)?.also { subject ->
                                _state.value = EditSubjectState(
                                    title = SubjectTextFieldState(text = subject.title),
                                    description = subject.description,
                                    semester = subject.semester,
                                    credit = subject.credit,
                                    finalGrade = subject.finalGrade,
                                    grade = subject.grade
                                )
                            }
                            validateEvent()
                        } catch (e: IllegalArgumentException) {
                            Log.e(tag, "EXCEPTION: Invalid subject id $currentSubjectId", e)
                            _eventFlow.emit(UiEvent.ShowSnackbar("Exception while fetching subject to edit"))
                        } catch (e: Exception) {
                            Log.e(tag, "EXCEPTION: Failed to fetch subject details", e)
                            _eventFlow.emit(UiEvent.ShowSnackbar("Failed to fetch subject to edit"))
                        } catch (e: Error) {
                            Log.e(tag, "ERROR: Failed to fetch subject details", e)
                            _eventFlow.emit(UiEvent.ShowSnackbar("Failed to fetch subject to edit"))
                        }
                    }
                } else {
                    Log.d(tag, "currentSubjectId is null, fetching current semester")
                    viewModelScope.launch {
                        val currentSemester = dataStore.data.first()[currentSemesterKey] ?: 1
                        _state.value = _state.value.copy(semester = currentSemester)
                    }
                }
            }

            is EditSubjectEvent.SaveSubject -> {
                Log.d(tag, "SaveSubject called")
                viewModelScope.launch {
                    validateEvent()
                    if (!_state.value.isFormValid) {
                        Log.e(tag, "INVALID: Form is not valid, not saving")
                        _eventFlow.emit(UiEvent.ShowSnackbar("Please fix the errors before saving"))
                        return@launch
                    }
                    if (currentSubjectId == null) {
                        try {
                            Log.d(tag, "currentSubjectId is null, adding new subject")
                            subjectUseCases.addSubject(
                                Subject(
                                    title = _state.value.title.text,
                                    description = _state.value.description,
                                    semester = _state.value.semester,
                                    credit = _state.value.credit,
                                    finalGrade = _state.value.finalGrade,
                                    grade = _state.value.grade,
                                    id = null
                                )
                            )
                            _eventFlow.emit(UiEvent.SaveSubject)
                        } catch (e: Exception) {
                            Log.e(tag, "EXCEPTION: Failed to save subject", e)
                            _eventFlow.emit(UiEvent.ShowSnackbar("Failed to save subject"))
                        } catch (e: Error) {
                            Log.e(tag, "ERROR: Failed to save subject", e)
                            _eventFlow.emit(UiEvent.ShowSnackbar("Failed to save subject"))
                        } catch (e: InvalidSubjectException) {
                            Log.e(
                                tag,
                                "InvalidSubjectException: Failed to save subject because title is empty",
                                e
                            )
                            _eventFlow.emit(UiEvent.ShowSnackbar("Failed to save subject, title cannot be empty"))
                        }
                    } else {
                        try {
                            Log.d(
                                tag,
                                "currentSubjectId is not null, updating subject with id: $currentSubjectId"
                            )
                            subjectUseCases.updateSubject(
                                Subject(
                                    title = _state.value.title.text,
                                    description = _state.value.description,
                                    semester = _state.value.semester,
                                    credit = _state.value.credit,
                                    finalGrade = _state.value.finalGrade,
                                    grade = _state.value.grade,
                                    id = currentSubjectId
                                )
                            )
                            _eventFlow.emit(UiEvent.SaveSubject)
                        } catch (e: Exception) {
                            Log.e(tag, "EXCEPTION: Failed to save subject", e)
                            _eventFlow.emit(UiEvent.ShowSnackbar("Failed to save subject"))
                        } catch (e: Error) {
                            Log.e(tag, "ERROR: Failed to save subject", e)
                            _eventFlow.emit(UiEvent.ShowSnackbar("Failed to save subject"))
                        } catch (e: InvalidSubjectException) {
                            Log.e(
                                tag,
                                "InvalidSubjectException: Failed to save subject because title is empty",
                                e
                            )
                            _eventFlow.emit(UiEvent.ShowSnackbar("Failed to save subject, title cannot be empty"))
                        }
                    }
                }
            }

            is EditSubjectEvent.EnteredTitle -> {
                _state.value = _state.value.copy(
                    title = _state.value.title.copy(text = event.title)
                )
                validateEvent()
                Log.d(tag, "EnteredTitle called with: ${event.title}")
            }

            is EditSubjectEvent.EnteredDescription -> {
                _state.value = _state.value.copy(description = event.description)
                Log.d(tag, "EnteredDescription called with: ${event.description}")
            }

            is EditSubjectEvent.EnteredSemester -> {
                _state.value = _state.value.copy(semester = event.semester)
                Log.d(tag, "EnteredSemester called with: ${event.semester}")
            }

            is EditSubjectEvent.EnteredCredit -> {
                _state.value = _state.value.copy(credit = event.credit)
                Log.d(tag, "EnteredCredit called with: ${event.credit}")
            }

            is EditSubjectEvent.EnteredFinalGrade -> {
                _state.value = _state.value.copy(finalGrade = event.finalGrade)
                Log.d(tag, "EnteredFinalGrade called with: ${event.finalGrade}")
            }

            is EditSubjectEvent.ValidateEvent -> {
                Log.d(tag, "ValidateEvent called")
                validateEvent()
            }
        }
    }

    private fun validateEvent() {
        Log.d(tag, "validateEvent called")
        val newState = _state.value.copy(
            title = _state.value.title.copy(error = null),
            isFormValid = false
        )

        // Title validation
        val titleResult = subjectUseCases.validateTitle.execute(newState.title.text)
        val updatedTitle = newState.title.copy(
            error = titleResult.errorMessage.takeIf { !titleResult.successful }
        )

        // Check if all required fields are filled
        val isFormValid = updatedTitle.error == null &&
                newState.title.text.isNotBlank() &&
                newState.semester != null &&
                newState.credit != null

        _state.value = newState.copy(
            title = updatedTitle,
            isFormValid = isFormValid
        )
        Log.d(tag, "validateEvent finished with state: ${_state.value}")
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        data object SaveSubject : UiEvent()
    }
}