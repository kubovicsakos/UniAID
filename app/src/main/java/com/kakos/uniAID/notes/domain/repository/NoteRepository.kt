package com.kakos.uniAID.notes.domain.repository

import com.kakos.uniAID.notes.domain.model.Note
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Note management.
 *
 * Defines operations for accessing and manipulating Note data
 * across different data sources.
 */
interface NoteRepository {

    // Get all notes
    fun getNotes(): Flow<List<Note>>

    // Get a note by id
    suspend fun getNoteById(id: Int): Note?

    // Insert a note
    suspend fun insertNote(note: Note)

    // Delete a note
    suspend fun deleteNote(note: Note)

    // Update a note
    suspend fun updateNote(note: Note)

    // Delete a note by id
    suspend fun deleteNoteById(id: Int)
}