package com.kakos.uniAID.notes.domain.use_case.note.read

import android.util.Log
import com.kakos.uniAID.notes.domain.model.Note
import com.kakos.uniAID.notes.domain.repository.NoteRepository

/**
 * Use case for retrieving a specific note by ID.
 *
 * Encapsulates the logic for validating the note ID
 * and interacting with the repository to fetch the data.
 *
 * @property repository The repository used for accessing note data.
 * @throws IllegalArgumentException When note ID is invalid (zero or negative).
 */
class GetNoteUseCase(
    private val repository: NoteRepository
) {
    private val tag = "GetNoteUseCase"

    suspend operator fun invoke(id: Int): Note? {
        Log.d(tag, "Getting note with ID $id")
        if (id <= 0) {
            Log.e(tag, "EXCEPTION: Note ID must be greater than 0 and it's $id")
            throw IllegalArgumentException("Note ID must be greater than 0 and it's $id")
        }
        Log.d(tag, "Note with ID $id retrieved successfully")
        return repository.getNoteById(id)
    }
}