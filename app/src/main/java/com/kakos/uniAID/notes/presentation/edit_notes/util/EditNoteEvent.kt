package com.kakos.uniAID.notes.presentation.edit_notes.util

import androidx.compose.ui.focus.FocusState

/**
 * Sealed class representing events for note editing.
 *
 * Defines possible user interactions or system events
 * that can occur within the note editing feature.
 */
sealed class EditNoteEvent {
    data class EnteredTitle(val value: String) : EditNoteEvent()
    data class ChangeTitleFocus(val focusState: FocusState) : EditNoteEvent()
    data class EnteredContent(val value: String) : EditNoteEvent()
    data class ChangeContentFocus(val focusState: FocusState) : EditNoteEvent()
    data class SelectSubject(val subjectId: Int?) : EditNoteEvent()
    data class DeleteNote(val id: Int) : EditNoteEvent()
    data class GetNote(val id: Int) : EditNoteEvent()
    data object SaveNote : EditNoteEvent()
    data object ToggleDarkTheme : EditNoteEvent()
}