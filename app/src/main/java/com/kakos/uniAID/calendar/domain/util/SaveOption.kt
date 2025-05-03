package com.kakos.uniAID.calendar.domain.util

/**
 * Sealed class representing save operation options.
 *
 * Encapsulates the possible update strategies for recurring events,
 * defining the scope of modification operations.
 */
enum class SaveOption {
    THIS, THIS_AND_FUTURE, ALL
}