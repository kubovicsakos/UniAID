package com.kakos.uniAID.notes.domain.use_case.note.create

import android.util.Log
import com.kakos.uniAID.notes.domain.model.InvalidNoteException
import com.kakos.uniAID.notes.domain.model.Note
import com.kakos.uniAID.notes.domain.repository.NoteRepository

/**
 * Use case for adding a new note.
 *
 * Encapsulates the logic for validating note content and title,
 * and interacting with the repository to persist the data.
 *
 * @property repository The repository used for persisting note data.
 * @throws InvalidNoteException When title is blank or last modified date is before creation date.
 */
class AddNoteUseCase(
    private val repository: NoteRepository
) {
    private val tag = "AddNoteUseCase"

    @Throws(InvalidNoteException::class)
    suspend operator fun invoke(note: Note) {
        if (note.title.isBlank()) { // If the title is empty, throw an exception
            Log.e(tag, "EXCEPTION: Title of the note can't be empty")
            throw InvalidNoteException("Title of the note can't be empty")
        }
        if (note.lastModified.isBefore(note.creationTime)) {
            Log.e(tag, "EXCEPTION: Last modified date can't be before creation date")
            throw InvalidNoteException("Last modified date can't be before creation date")
        }
        repository.insertNote(note)
        Log.d(tag, "Note added successfully ${note.id} with title ${note.title}")
    }

}