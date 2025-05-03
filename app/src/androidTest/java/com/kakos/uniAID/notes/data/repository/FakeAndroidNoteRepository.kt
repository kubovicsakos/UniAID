package com.kakos.uniAID.notes.data.repository

import com.kakos.uniAID.notes.domain.model.Note
import com.kakos.uniAID.notes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDateTime

class FakeAndroidNoteRepository : NoteRepository {

    private var noteItems = MutableStateFlow<List<Note>>(emptyList())

    fun shouldHaveFilledList(shouldHaveFilledList: Boolean) {
        noteItems = if (shouldHaveFilledList) {
            MutableStateFlow(
                listOf(
                    Note(
                        id = 1,
                        title = "Note 1",
                        content = "Content 1",
                        creationTime = LocalDateTime.now(),
                        lastModified = LocalDateTime.now(),
                        darkTheme = true
                    ),
                    Note(
                        id = 2,
                        title = "Note 2",
                        content = "Content 2",
                        creationTime = LocalDateTime.now(),
                        lastModified = LocalDateTime.now(),
                        darkTheme = true
                    ),
                    Note(
                        id = 3,
                        title = "Note 3",
                        content = "Content 3",
                        creationTime = LocalDateTime.now(),
                        lastModified = LocalDateTime.now(),
                        darkTheme = true
                    )
                )
            )
        } else {
            MutableStateFlow(emptyList())
        }
    }

    override fun getNotes(): Flow<List<Note>> { // Get all notes
        return noteItems
    }

    override suspend fun getNoteById(id: Int): Note? { // Get a note by id
        return noteItems.value.find { it.id == id }
    }

    override suspend fun insertNote(note: Note) { // Insert a note
        noteItems.value += note
    }

    override suspend fun deleteNote(note: Note) { // Delete a note
        noteItems.value -= note
    }

    override suspend fun updateNote(note: Note) { // Update a note
        noteItems.value = noteItems.value.map {
            if (it.id == note.id) {
                note
            } else {
                it
            }
        }
    }

    override suspend fun deleteNoteById(id: Int) { // Delete a note by id
        noteItems.value = noteItems.value.filter { it.id != id }
    }
}