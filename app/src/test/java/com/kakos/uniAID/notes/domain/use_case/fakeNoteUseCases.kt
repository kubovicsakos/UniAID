package com.kakos.uniAID.notes.domain.use_case

import com.kakos.uniAID.notes.data.repository.FakeNoteRepository
import com.kakos.uniAID.notes.domain.use_case.note.create.AddNoteUseCase
import com.kakos.uniAID.notes.domain.use_case.note.delete.DeleteNoteByIdUseCase
import com.kakos.uniAID.notes.domain.use_case.note.delete.DeleteNoteUseCase
import com.kakos.uniAID.notes.domain.use_case.note.read.GetNoteUseCase
import com.kakos.uniAID.notes.domain.use_case.note.read.GetNotesUseCase
import com.kakos.uniAID.notes.domain.use_case.note.update.UpdateNoteUseCase
import com.kakos.uniAID.notes.domain.use_case.note.validate.ValidateNoteTitleUseCase

fun fakeNoteUseCases( // NoteUseCases provider
    repository: FakeNoteRepository
): NoteUseCases {
    return NoteUseCases(
        getNotesUseCase = GetNotesUseCase(repository),
        deleteNoteUseCase = DeleteNoteUseCase(repository),
        deleteNoteByIdUseCase = DeleteNoteByIdUseCase(repository),
        updateNoteUseCase = UpdateNoteUseCase(repository),
        addNoteUseCase = AddNoteUseCase(repository),
        getNoteUseCase = GetNoteUseCase(repository),
        validateNoteTitleUseCase = ValidateNoteTitleUseCase()
    )


}
