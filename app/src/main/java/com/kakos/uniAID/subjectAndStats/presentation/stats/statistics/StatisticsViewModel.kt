package com.kakos.uniAID.subjectAndStats.presentation.stats.statistics

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakos.uniAID.core.domain.subject.domain.model.InvalidSubjectException
import com.kakos.uniAID.core.domain.subject.domain.model.Subject
import com.kakos.uniAID.core.domain.subject.domain.use_case.SubjectUseCases
import com.kakos.uniAID.subjectAndStats.presentation.stats.statistics.util.StatisticsEvent
import com.kakos.uniAID.subjectAndStats.presentation.stats.statistics.util.StatisticsState
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
 * ViewModel for statistics screen.
 *
 * Manages state and business logic for the statistics UI.
 * Interacts with use cases to perform data operations.
 *
 * @property subjectUseCases Use cases for interacting with subjects.
 * @property dataStore DataStore for storing user preferences.
 */
@HiltViewModel
open class StatisticsViewModel @Inject constructor(
    private val subjectUseCases: SubjectUseCases,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val tag = "StatisticsViewModel"

    private val currentSemesterKey = intPreferencesKey("current_semester")

    private val _state = mutableStateOf(StatisticsState())
    val state: State<StatisticsState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var getSubjectsJob: Job? = null
    private val _allSubjects = mutableStateOf<List<Subject>>(emptyList())

    init {
        Log.d(tag, "Initializing")
        Log.d(tag, "Loading current semester")
        loadCurrentSemester()
        Log.d(tag, "Current semester loaded: ${_state.value.currentSemester}")
        Log.d(tag, "Getting all subjects")
        getAllSubjects()
        Log.d(tag, "All subjects retrieved: ${_allSubjects.value}")
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
            Log.d(tag, "loadCurrentSemester done")
        }
    }

    fun onEvent(event: StatisticsEvent) {
        when (event) {
            is StatisticsEvent.SetSemester -> {
                viewModelScope.launch {
                    if (event.semester < 1) {
                        Log.d(tag, "Invalid semester value: ${event.semester}")
                        _eventFlow.emit(UiEvent.ShowSnackbar("Invalid semester value"))
                        return@launch
                    }
                    Log.d(tag, "SetSemester called with semester: ${event.semester}")
                    dataStore.edit { preferences ->
                        preferences[currentSemesterKey] = event.semester
                    }
                    _state.value = _state.value.copy(currentSemester = event.semester)
                    filterSubjectsBySemester(event.semester)
                }
            }

            is StatisticsEvent.SetGrade -> {
                viewModelScope.launch {
                    try {
                        Log.d(
                            tag,
                            "SetGrade called with subjectId: ${event.subjectId} and grade: ${event.grade}"
                        )
                        val subject = _allSubjects.value.find { it.id == event.subjectId }
                        subject?.let {
                            val updatedSubject = it.copy(grade = event.grade)
                            subjectUseCases.updateSubject(updatedSubject)
                            getAllSubjects()
                        }
                    } catch (e: InvalidSubjectException) {
                        Log.e(tag, "InvalidSubjectException: Cannot update grade:", e)
                        _eventFlow.tryEmit(UiEvent.ShowSnackbar("Failed to set grade"))
                    } catch (e: Exception) {
                        Log.e(tag, "EXCEPTION: Failed to set grade", e)
                        _eventFlow.tryEmit(UiEvent.ShowSnackbar("Failed to set grade"))
                    } catch (e: Error) {
                        Log.e(tag, "ERROR: Failed to set grade", e)
                        _eventFlow.tryEmit(UiEvent.ShowSnackbar("Failed to set grade"))
                    }
                }
            }
        }
    }

    private fun getAllSubjects() {
        Log.d(tag, "getAllSubjects called")
        getSubjectsJob?.cancel()
        try {
            getSubjectsJob = subjectUseCases.getAllSubjects().onEach { allSubjects ->
                _allSubjects.value = allSubjects
                filterSubjectsBySemester(_state.value.currentSemester)
                _state.value = _state.value.copy(isLoading = false)
            }.launchIn(viewModelScope)
            Log.d(tag, "getAllSubjects done")
        } catch (e: Exception) {
            Log.e(tag, "EXCEPTION: Failed to get all subjects", e)
            _eventFlow.tryEmit(UiEvent.ShowSnackbar("Failed to get all subjects"))
        } catch (e: Error) {
            Log.e(tag, "ERROR: Failed to get all subjects", e)
            _eventFlow.tryEmit(UiEvent.ShowSnackbar("Failed to get all subjects"))
        }
    }

    private fun filterSubjectsBySemester(semester: Int) {
        Log.d(tag, "filterSubjectsBySemester called with semester: $semester")
        val filteredSubjects = _allSubjects.value.filter { it.semester == semester }
        val stats = calculateStats(filteredSubjects)
        _state.value = _state.value.copy(
            subjects = filteredSubjects,
            currentSemester = semester,
            ci = stats.ci,
            cci = stats.cci,
            weightedAverage = stats.weightedAverage,
            completedCredit = stats.completedCredit,
            committedCredit = stats.committedCredit
        )
        Log.d(tag, "filterSubjectsBySemester done")
    }

    private data class StatisticsResult(
        val ci: Float,
        val cci: Float,
        val weightedAverage: Float,
        val completedCredit: Int,
        val committedCredit: Int
    )

    private fun calculateStats(subjects: List<Subject>): StatisticsResult {
        Log.d(tag, "calculateStats called with subjects: $subjects")
        var completedCredit = 0
        var committedCredit = 0
        var sumCompletedCreditGrade = 0
        var sumGradeCredit = 0

        subjects.forEach { subject ->
            // Skip subjects with null grade entirely
            val grade = subject.grade ?: return@forEach
            val credit = subject.credit ?: 0

            // Only count subjects with non-null grades in committed credits
            committedCredit += credit

            if (grade > 1) {
                completedCredit += credit
                sumCompletedCreditGrade += credit * grade
                sumGradeCredit += credit * grade
            }
        }

        val ci = if (committedCredit == 0) {
            0f
        } else {
            sumCompletedCreditGrade.toFloat() / 30f
        }

        val cci = if (committedCredit == 0) {
            0f
        } else {
            ci * (completedCredit.toFloat() / committedCredit.toFloat())
        }

        val weightedAverage = if (completedCredit == 0) {
            0f
        } else {
            sumGradeCredit.toFloat() / completedCredit.toFloat()
        }

        Log.d(tag, "calculateStats done")

        return StatisticsResult(
            ci,
            cci,
            weightedAverage,
            completedCredit,
            committedCredit
        )
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
    }
}