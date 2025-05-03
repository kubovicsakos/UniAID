package com.kakos.uniAID.subjectAndStats.presentation.stats.all_statistics.util

/**
 * Represents UI state for all statistics screen.
 *
 * @property semesterStats A list of [SemesterStatistics] objects. Each object contains the statistics for a specific semester.
 * @property totalStats An instance of [TotalStatistics] containing the overall, aggregated statistics.
 */
data class AllStatisticsState(
    val semesterStats: List<SemesterStatistics> = emptyList(),
    val totalStats: TotalStatistics = TotalStatistics()
)
