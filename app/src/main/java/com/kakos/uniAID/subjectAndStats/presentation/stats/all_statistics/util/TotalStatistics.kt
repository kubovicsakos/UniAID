package com.kakos.uniAID.subjectAndStats.presentation.stats.all_statistics.util

/**
 * Represents the total statistics for a collection of data.
 *
 * @property ci The confidence interval (CI) value. Represents a range of values
 * within which a population parameter is likely to lie.
 * @property cci The combined confidence interval (CCI) value. Similar to CI but potentially
 * calculated using a different method or representing a combined interval from multiple sources.
 * @property weightedAverage The weighted average of some underlying data. This value
 * is calculated by assigning different weights to different data points.
 * @property totalCompleted The total number of items that have been completed.
 * @property totalCommitted The total number of items that have been committed.
 */
data class TotalStatistics(
    val ci: Float = 0f,
    val cci: Float = 0f,
    val weightedAverage: Float = 0f,
    val totalCompleted: Int = 0,
    val totalCommitted: Int = 0
)
