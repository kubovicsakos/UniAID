package com.kakos.uniAID.notes.data.repository

import com.kakos.uniAID.notes.data.NoteDao
import com.kakos.uniAID.notes.domain.model.Note
import com.kakos.uniAID.notes.domain.repository.NoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Implementation of NoteRepository interface.
 *
 * Handles data operations through the underlying NoteDao.
 * Uses coroutines for asynchronous database operations.
 *
 * @property dao Data Access Object for notes database operations.
 */
class NoteRepositoryImpl(
    private val dao: NoteDao
) : NoteRepository {

    override fun getNotes(): Flow<List<Note>> { // Get all notes
        return dao.getNotes()
    }

    override suspend fun getNoteById(id: Int): Note? { // Get a note by id
        return dao.getNoteById(id)
    }

    override suspend fun insertNote(note: Note) { // Insert a note
        CoroutineScope(Dispatchers.IO).launch {
            dao.insertNote(note)
        }
    }

    override suspend fun deleteNote(note: Note) { // Delete a note
        CoroutineScope(Dispatchers.IO).launch {
            dao.deleteNote(note)
        }
    }

    override suspend fun updateNote(note: Note) { // Update a note
        CoroutineScope(Dispatchers.IO).launch {
            dao.updateNote(note)
        }
    }

    override suspend fun deleteNoteById(id: Int) { // Delete a note by id
        CoroutineScope(Dispatchers.IO).launch {
            dao.deleteNoteById(id)
        }
    }
}