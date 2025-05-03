package com.kakos.uniAID.notes.di

import com.kakos.uniAID.notes.domain.repository.NoteRepository
import com.kakos.uniAID.notes.domain.use_case.NoteUseCases
import com.kakos.uniAID.notes.domain.use_case.note.create.AddNoteUseCase
import com.kakos.uniAID.notes.domain.use_case.note.delete.DeleteNoteByIdUseCase
import com.kakos.uniAID.notes.domain.use_case.note.delete.DeleteNoteUseCase
import com.kakos.uniAID.notes.domain.use_case.note.read.GetNoteUseCase
import com.kakos.uniAID.notes.domain.use_case.note.read.GetNotesUseCase
import com.kakos.uniAID.notes.domain.use_case.note.update.UpdateNoteUseCase
import com.kakos.uniAID.notes.domain.use_case.note.validate.ValidateNoteTitleUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module for providing note-related dependencies.
 *
 * Installed in SingletonComponent, provides NoteUseCases and related dependencies.
 *
 * Responsibilities:
 * - Provides instances of note use cases
 * - Ensures singleton instances where needed
 * - Injects dependencies into components
 */
@Module
@InstallIn(SingletonComponent::class)
object NoteModule {

    @Provides
    @Singleton
    fun provideNoteUseCases( // NoteUseCases provider
        repository: NoteRepository
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
}