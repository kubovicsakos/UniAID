package com.kakos.uniAID.calendar.domain.util

/**
 * Sealed class representing delete operation options.
 *
 * Encapsulates the possible deletion strategies for recurring events,
 * defining the scope of deletion operations.
 */
enum class DeleteOption {
    THIS, THIS_AND_FUTURE, ALL
}