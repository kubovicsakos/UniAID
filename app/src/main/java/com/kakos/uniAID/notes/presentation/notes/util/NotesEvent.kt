package com.kakos.uniAID.notes.presentation.notes.util

import com.kakos.uniAID.notes.domain.model.Note
import com.kakos.uniAID.notes.domain.util.NoteOrder

/**
 * Sealed class representing events for notes feature.
 *
 * Defines possible user interactions or system events
 * that can occur within the notes management feature.
 */
sealed class NotesEvent {
    data class Order(val noteOrder: NoteOrder) : NotesEvent()
    data class DeleteNote(val note: Note) : NotesEvent()
    data object RestoreNote : NotesEvent()
    data object ToggleOrderSection : NotesEvent()
}