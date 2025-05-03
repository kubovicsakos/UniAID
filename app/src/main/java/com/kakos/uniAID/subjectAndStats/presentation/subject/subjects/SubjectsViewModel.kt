package com.kakos.uniAID.subjectAndStats.presentation.subject.subjects

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakos.uniAID.core.domain.subject.domain.model.Subject
import com.kakos.uniAID.core.domain.subject.domain.use_case.SubjectUseCases
import com.kakos.uniAID.subjectAndStats.presentation.subject.subjects.util.SubjectsEvent
import com.kakos.uniAID.subjectAndStats.presentation.subject.subjects.util.SubjectsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Subjects screen.
 *
 * Manages state and business logic for the UI.
 * Interacts with use cases to perform data operations.
 *
 * @property subjectUseCases Use cases for interacting with subject-related data.
 * @property dataStore DataStore for persisting user preferences.
 */
@HiltViewModel
class SubjectsViewModel @Inject constructor(
    private val subjectUseCases: SubjectUseCases,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val tag = "SubjectsViewModel"

    private val currentSemesterKey = intPreferencesKey("current_semester")

    private val _state = mutableStateOf(SubjectsState())
    val state: State<SubjectsState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var getSubjectsJob: Job? = null
    private val _allSubjects = mutableStateOf<List<Subject>>(emptyList())

    init {
        Log.d(tag, "Initializing")
        Log.d(tag, "Loading current semester")
        loadCurrentSemester()
        Log.d(tag, "Getting all subjects")
        getAllSubjects()
        Log.d(tag, "Initialization done")
    }

    private fun loadCurrentSemester() {
        Log.d(tag, "loadCurrentSemester called")
        viewModelScope.launch {
            dataStore.data
                .map { preferences ->
                    preferences[currentSemesterKey] ?: 1
                }
                .collect { semester ->
                    _state.value = _state.value.copy(currentSemester = semester)
                    filterSubjectsBySemester(semester)
                }
        }
        Log.d(tag, "Loaded current semester: ${_state.value.currentSemester}")
    }

    fun onEvent(event: SubjectsEvent) {
        when (event) {
            is SubjectsEvent.SetSemester -> {
                viewModelScope.launch {
                    if (event.semester < 1) {
                        Log.d(tag, "Invalid semester value: ${event.semester}")
                        _eventFlow.emit(UiEvent.ShowSnackbar("Invalid semester value"))
                        return@launch
                    }
                    Log.d(tag, "SetSemester event received: ${event.semester}")
                    _state.value = _state.value.copy(currentSemester = event.semester)
                    filterSubjectsBySemester(event.semester)
                }
            }
        }
    }

    private fun getAllSubjects() {
        Log.d(tag, "getAllSubjects called")
        getSubjectsJob?.cancel()
        try {
            getSubjectsJob = subjectUseCases.getAllSubjects()
                .onEach { allSubjects ->
                    _allSubjects.value = allSubjects
                    filterSubjectsBySemester(_state.value.currentSemester)
                    _state.value = _state.value.copy(isLoading = false)
                }.launchIn(viewModelScope)
            Log.d(tag, "All subjects loaded: ${_allSubjects.value}")
        } catch (e: Exception) {
            Log.e(tag, "EXCEPTION: While fetching subjects: ${e.message}")
            _eventFlow.tryEmit(UiEvent.ShowSnackbar("Exception while fetching subjects"))
        } catch (e: Error) {
            Log.e(tag, "ERROR: While fetching subjects: ${e.message}")
            _eventFlow.tryEmit(UiEvent.ShowSnackbar("Error while fetching subjects"))
        }
    }

    private fun filterSubjectsBySemester(semester: Int) {
        Log.d(tag, "filterSubjectsBySemester called with semester: $semester")
        val filteredSubjects = _allSubjects.value.filter {
            it.semester == semester
        }
        _state.value = _state.value.copy(subjects = filteredSubjects)
        Log.d(tag, "Filtered subjects: ${_state.value.subjects}")
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
    }
}