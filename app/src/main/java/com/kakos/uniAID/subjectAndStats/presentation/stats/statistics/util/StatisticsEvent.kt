package com.kakos.uniAID.subjectAndStats.presentation.stats.statistics.util

/**
 * Sealed class representing events for statistics feature.
 *
 * Defines possible user interactions or system events
 * that can occur within the statistics feature.
 */
sealed class StatisticsEvent {
    data class SetSemester(val semester: Int) : StatisticsEvent()
    data class SetGrade(val subjectId: Int, val grade: Int?) : StatisticsEvent()
}