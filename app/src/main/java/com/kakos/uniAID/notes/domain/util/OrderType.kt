package com.kakos.uniAID.notes.domain.util

/**
 * Represents the direction of ordering.
 *
 * Encapsulates the two possible sorting directions:
 * - Ascending (A to Z, oldest to newest)
 * - Descending (Z to A, newest to oldest)
 */
sealed class OrderType {
    data object Ascending : OrderType()
    data object Descending : OrderType()
}