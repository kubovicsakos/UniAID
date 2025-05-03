package com.kakos.uniAID.subjectAndStats.presentation.stats.all_statistics

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakos.uniAID.core.domain.subject.domain.model.Subject
import com.kakos.uniAID.core.domain.subject.domain.use_case.SubjectUseCases
import com.kakos.uniAID.subjectAndStats.presentation.stats.all_statistics.util.AllStatisticsState
import com.kakos.uniAID.subjectAndStats.presentation.stats.all_statistics.util.SemesterStatistics
import com.kakos.uniAID.subjectAndStats.presentation.stats.all_statistics.util.TotalStatistics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * [AllStatisticsViewModel] is a ViewModel for the all statistics screen.
 *
 * Manages state and business logic for the UI.
 * Interacts with use cases to perform data operations.
 *
 * @property subjectUseCases The use cases for interacting with subject data.
 */
@HiltViewModel
class AllStatisticsViewModel @Inject constructor(
    private val subjectUseCases: SubjectUseCases
) : ViewModel() {

    private val tag = "AllStatisticsViewModel"

    private val _state = mutableStateOf(AllStatisticsState())
    val state: State<AllStatisticsState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        Log.d(tag, "Initialization")
        Log.d(tag, "Getting all semesters data")
        getAllSemestersData()
        Log.d(tag, "All semesters data retrieved, State: ${state.value}")
        Log.d(tag, "Initialization done")
    }

    fun getAllSemestersData() {
        Log.d(tag, "GettingAllSemestersData called")
        try {
            subjectUseCases.getAllSubjects().onEach { allSubjects ->
                val semesterMap = allSubjects.groupBy { it.semester ?: 0 }
                val semesterStats = semesterMap.map { (semester, subjects) ->
                    val stats = calculateStats(subjects)
                    SemesterStatistics(
                        semester = semester,
                        ci = stats.ci,
                        cci = stats.cci,
                        weightedAverage = stats.weightedAverage,
                        completedCredit = stats.completedCredit,
                        committedCredit = stats.committedCredit
                    )
                }.sortedBy { it.semester }
                Log.d(tag, "Semester stats calculated: $semesterStats")
                val totalStats = calculateStats(allSubjects)
                _state.value = AllStatisticsState(
                    semesterStats = semesterStats,
                    totalStats = TotalStatistics(
                        ci = totalStats.ci,
                        cci = totalStats.cci,
                        weightedAverage = totalStats.weightedAverage,
                        totalCompleted = totalStats.completedCredit,
                        totalCommitted = totalStats.committedCredit
                    )
                )
                Log.d(tag, "Total stats calculated: ${_state.value.totalStats}")
            }.launchIn(viewModelScope)
        } catch (e: Exception) {
            Log.e(tag, "EXCEPTION: Failed to get all subjects", e)
            _eventFlow.tryEmit(UiEvent.ShowSnackbar("Failed to get subjects"))
        } catch (e: Error) {
            Log.e(tag, "ERROR: Failed to get all subjects", e)
            _eventFlow.tryEmit(UiEvent.ShowSnackbar("Failed to get subjects"))
        }
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