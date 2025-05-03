package com.kakos.uniAID.notes.domain.use_case

import com.kakos.uniAID.notes.domain.use_case.note.create.AddNoteUseCase
import com.kakos.uniAID.notes.domain.use_case.note.delete.DeleteNoteByIdUseCase
import com.kakos.uniAID.notes.domain.use_case.note.delete.DeleteNoteUseCase
import com.kakos.uniAID.notes.domain.use_case.note.read.GetNoteUseCase
import com.kakos.uniAID.notes.domain.use_case.note.read.GetNotesUseCase
import com.kakos.uniAID.notes.domain.use_case.note.update.UpdateNoteUseCase
import com.kakos.uniAID.notes.domain.use_case.note.validate.ValidateNoteTitleUseCase

/**
 * Use case container for note operations.
 *
 * Encapsulates all note-related use cases to simplify dependency injection
 * and provide centralized access to note functionalities.
 *
 * @property addNoteUseCase The use case for creating new notes.
 * @property deleteNoteUseCase The use case for removing notes by object.
 * @property deleteNoteByIdUseCase The use case for removing notes by ID.
 * @property updateNoteUseCase The use case for modifying existing notes.
 * @property getNotesUseCase The use case for retrieving all notes.
 * @property getNoteUseCase The use case for retrieving a specific note.
 */
data class NoteUseCases(
    val addNoteUseCase: AddNoteUseCase,
    val deleteNoteUseCase: DeleteNoteUseCase,
    val deleteNoteByIdUseCase: DeleteNoteByIdUseCase,
    val updateNoteUseCase: UpdateNoteUseCase,
    val getNotesUseCase: GetNotesUseCase,
    val getNoteUseCase: GetNoteUseCase,
    val validateNoteTitleUseCase: ValidateNoteTitleUseCase
)
