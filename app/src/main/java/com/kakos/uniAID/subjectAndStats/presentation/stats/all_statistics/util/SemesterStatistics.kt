package com.kakos.uniAID.subjectAndStats.presentation.stats.all_statistics.util

/**
 * Represents the statistics for a single semester.
 *
 * This data class holds information about a student's academic performance
 * in a specific semester.
 *
 * @property semester The semester number.
 * @property ci The Cumulative Index (CI) for the semester.
 * @property cci The Cumulative Cumulative Index (CCI).
 * @property weightedAverage The weighted average grade for the courses taken in this semester.
 * @property completedCredit The total number of credit hours successfully completed in this semester.
 * @property committedCredit The total number of credit hours the student committed to taking in this semester.
 */
data class SemesterStatistics(
    val semester: Int,
    val ci: Float,
    val cci: Float,
    val weightedAverage: Float,
    val completedCredit: Int,
    val committedCredit: Int
)
