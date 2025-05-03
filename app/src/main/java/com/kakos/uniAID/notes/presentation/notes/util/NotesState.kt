package com.kakos.uniAID.notes.presentation.notes.util

import com.kakos.uniAID.notes.domain.model.Note
import com.kakos.uniAID.notes.domain.util.NoteOrder
import com.kakos.uniAID.notes.domain.util.OrderType

/**
 * Represents UI state for notes screen.
 *
 * @property notes List of notes to display.
 * @property noteOrder Current ordering configuration for notes.
 * @property isOrderSectionVisible Whether the order selection UI is shown.
 * @property isLoading Whether notes data is currently loading.
 */
data class NotesState(
    val notes: List<Note> = emptyList(),
    val noteOrder: NoteOrder = NoteOrder.Date(OrderType.Descending),
    val isOrderSectionVisible: Boolean = false,
    val isLoading: Boolean = true
)
