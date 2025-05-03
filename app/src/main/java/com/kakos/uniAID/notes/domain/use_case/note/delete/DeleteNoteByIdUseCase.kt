package com.kakos.uniAID.notes.domain.use_case.note.delete

import android.util.Log
import com.kakos.uniAID.notes.domain.repository.NoteRepository

/**
 * Use case for deleting a note by its ID.
 *
 * Encapsulates the logic for validating the note ID and
 * interacting with the repository to remove the data.
 *
 * @property repository The NoteRepository instance used to delete note data.
 * @throws IllegalArgumentException When note ID is invalid (zero or negative).
 */
class DeleteNoteByIdUseCase(
    private val repository: NoteRepository
) {
    private val tag = "DeleteNoteByIdUseCase"

    suspend operator fun invoke(noteId: Int) {
        if (noteId <= 0) {
            Log.e(tag, "EXCEPTION: Note ID must be greater than 0 and it's $noteId")
            throw IllegalArgumentException("Note ID must be greater than 0 and it's $noteId")
        }
        repository.deleteNoteById(noteId)
        Log.d(tag, "Note with ID $noteId deleted successfully")
    }
}