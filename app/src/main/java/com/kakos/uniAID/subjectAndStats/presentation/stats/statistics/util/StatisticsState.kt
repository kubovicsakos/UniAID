package com.kakos.uniAID.subjectAndStats.presentation.stats.statistics.util

import com.kakos.uniAID.core.domain.subject.domain.model.Subject

/**
 * Represents UI state for statistics screen.
 *
 * @property subjects List of user's enrolled subjects.
 * @property currentSemester The active semester number.
 * @property ci The Index Coefficient academic performance indicator.
 * @property cci The Index Consistency Coefficient academic indicator.
 * @property weightedAverage The weighted average of all grades.
 * @property completedCredit Total number of completed credits.
 * @property committedCredit Total number of enrolled credits.
 * @property isLoading Whether data is currently being loaded.
 */
data class StatisticsState(
    val subjects: List<Subject> = emptyList(),
    val currentSemester: Int = 1,
    val ci: Float = 0f,
    val cci: Float = 0f,
    val weightedAverage: Float = 0f,
    val completedCredit: Int = 0,
    val committedCredit: Int = 0,
    val isLoading: Boolean = true
)

