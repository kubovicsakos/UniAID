package com.kakos.uniAID.notes.domain.use_case.note.update

import android.util.Log
import com.kakos.uniAID.notes.domain.model.InvalidNoteException
import com.kakos.uniAID.notes.domain.model.Note
import com.kakos.uniAID.notes.domain.repository.NoteRepository

/**
 * Use case for updating an existing note.
 *
 * Encapsulates the logic for validating the note's ID
 * and interacting with the repository to persist the changes.
 *
 * @property repository The repository used for updating note data.
 * @throws IllegalArgumentException When note ID is null or invalid.
 * @throws InvalidNoteException When title is blank.
 */
class UpdateNoteUseCase(
    private val repository: NoteRepository
) {
    private val tag = "UpdateNoteUseCase"

    suspend operator fun invoke(note: Note) {
        if (note.id == null) {
            Log.e(tag, "EXCEPTION: Note ID must be not null and it's null")
            throw IllegalArgumentException("Note ID must be not null")
        } else if (note.id <= 0) {
            Log.e(tag, "EXCEPTION: Note ID must be greater than 0 and it's ${note.id}")
            throw IllegalArgumentException("Note ID must be greater than 0 and it's ${note.id}")
        }
        if (note.title.isBlank()) {
            Log.e(tag, "EXCEPTION: Title of the note can't be empty")
            throw InvalidNoteException("Title of the note can't be empty")
        }

        repository.updateNote(note)
        Log.d(tag, "Note with ID ${note.id} updated successfully")
    }
}
