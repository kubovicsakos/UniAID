package com.kakos.uniAID.notes.presentation.edit_notes.util

import com.kakos.uniAID.core.domain.subject.domain.model.Subject
import java.time.LocalDateTime

/**
 * Represents UI state for note editing screen.
 *
 * @property title The state of the title text field.
 * @property content The state of the content text field.
 * @property isDarkTheme Whether the UI uses dark mode.
 * @property isNewNote Whether creating a new note or editing existing one.
 * @property creationTime When the note was initially created.
 * @property lastModified When the note was last modified.
 * @property validationError Optional validation error message.
 * @property isFormValid Whether current form data is valid.
 * @property subjectId ID of associated subject or null if none.
 * @property subjectName Name of associated subject or null if none.
 * @property subjects List of all available subjects.
 * @property filteredSubjects List of filtered subjects based on criteria.
 * @property currentSemester Active semester of the user.
 */
data class EditNoteState(
    val title: NoteTextFieldState = NoteTextFieldState(hint = "Enter title..."),
    val content: NoteTextFieldState = NoteTextFieldState(hint = "Enter the body of your note..."),
    val isDarkTheme: Boolean = true,
    val isNewNote: Boolean = true,
    val creationTime: LocalDateTime = LocalDateTime.now(),
    val lastModified: LocalDateTime = LocalDateTime.now(),
    val isFormValid: Boolean = true,
    val subjectId: Int? = null,
    val subjectName: String? = null,
    val subjects: List<Subject> = emptyList(),
    val filteredSubjects: List<Subject> = emptyList(),
    val currentSemester: Int? = 1,
)