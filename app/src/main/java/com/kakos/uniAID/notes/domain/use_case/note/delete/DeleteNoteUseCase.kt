package com.kakos.uniAID.notes.domain.use_case.note.delete

import android.util.Log
import com.kakos.uniAID.notes.domain.model.Note
import com.kakos.uniAID.notes.domain.repository.NoteRepository

/**
 * Use case for deleting a note.
 *
 * Encapsulates the logic for validating the note's ID
 * and interacting with the repository to remove the data.
 *
 * @property repository The \[NoteRepository\] that manages note data.
 * @throws IllegalArgumentException When note ID is null or invalid.
 */
class DeleteNoteUseCase(
    private val repository: NoteRepository
) {
    private val tag = "DeleteNoteUseCase"

    suspend operator fun invoke(note: Note) {
        if (note.id == null) {
            Log.e(tag, "EXCEPTION: Note ID must be not null and it's null")
            throw IllegalArgumentException("Note with ID null not found")
        } else if (note.id <= 0) {
            Log.e(tag, "EXCEPTION: Note ID must be greater than 0 and it's ${note.id}")
            throw IllegalArgumentException("Note with ID ${note.id} not found")
        }

        repository.deleteNote(note)
    }
}