package com.kakos.uniAID.notes.domain.util

/**
 * Represents the ordering criteria for notes.
 *
 * Encapsulates different ways notes can be sorted:
 * - By title (alphabetically)
 * - By date (chronologically)
 * Each with ascending or descending direction.
 *
 * @property orderType The direction of ordering (ascending or descending).
 */
sealed class NoteOrder(val orderType: OrderType) {
    /**
     * Orders notes alphabetically by title.
     *
     * @property orderType The direction of ordering.
     */
    class Title(orderType: OrderType) : NoteOrder(orderType)

    /**
     * Orders notes chronologically by date.
     *
     * @property orderType The direction of ordering.
     */
    class Date(orderType: OrderType) : NoteOrder(orderType)


    /**
     * Creates a copy of this order with a new order type.
     *
     * @param orderType The new direction of ordering.
     * @return A new NoteOrder instance with the updated order type.
     */
    fun copy(orderType: OrderType): NoteOrder {

        return when (this) {
            is Title -> Title(orderType)
            is Date -> Date(orderType)
        }
    }
}