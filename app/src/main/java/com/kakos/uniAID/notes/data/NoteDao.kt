package com.kakos.uniAID.notes.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.kakos.uniAID.notes.domain.model.Note
import com.kakos.uniAID.notes.domain.model.NoteWithSubject
import kotlinx.coroutines.flow.Flow


/**
 * Data Access Object for Note entities.
 *
 * Provides CRUD operations for the notes table.
 */
@Dao
interface NoteDao {

    // Get all notes
    @Query("SELECT * FROM notes")
    fun getNotes(): Flow<List<Note>>

    // Get a note by id
    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Int): Note?

    // Insert a note
    @Insert
    fun insertNote(note: Note)

    // Delete a note
    @Delete
    fun deleteNote(note: Note)

    // Update a note
    @Update
    suspend fun updateNote(note: Note)

    // Delete a note by id
    @Query("DELETE FROM notes WHERE id = :id")
    fun deleteNoteById(id: Int)

    // Get all notes with subjects
    @Transaction
    @Query("SELECT * FROM notes")
    fun getNotesWithSubjects(): Flow<List<NoteWithSubject>>

    // Clear the subject from all notes if the subject is deleted
    @Query("UPDATE notes SET subjectId = null, subjectName = null WHERE subjectId = :subjectId")
    suspend fun clearSubjectFromNotes(subjectId: Int)
}