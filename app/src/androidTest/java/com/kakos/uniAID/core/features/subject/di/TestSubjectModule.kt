package com.kakos.uniAID.core.features.subject.di

import com.kakos.uniAID.core.domain.subject.domain.repository.SubjectRepository
import com.kakos.uniAID.core.domain.subject.domain.use_case.SubjectUseCases
import com.kakos.uniAID.core.domain.subject.domain.use_case.create.AddSubjectUseCase
import com.kakos.uniAID.core.domain.subject.domain.use_case.delete.DeleteSubjectByIdUseCase
import com.kakos.uniAID.core.domain.subject.domain.use_case.delete.DeleteSubjectUseCase
import com.kakos.uniAID.core.domain.subject.domain.use_case.read.GetAllSubjectsUseCase
import com.kakos.uniAID.core.domain.subject.domain.use_case.read.GetEventsOfSubjectUseCase
import com.kakos.uniAID.core.domain.subject.domain.use_case.read.GetNotesOfSubjectUseCase
import com.kakos.uniAID.core.domain.subject.domain.use_case.read.GetSubjectByIdUseCase
import com.kakos.uniAID.core.domain.subject.domain.use_case.read.GetSubjectsBySemesterUseCase
import com.kakos.uniAID.core.domain.subject.domain.use_case.update.UpdateSubjectUseCase
import com.kakos.uniAID.core.domain.subject.domain.use_case.validate.ValidateSubjectTitleUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestSubjectModule {

    @Provides
    @Singleton
    fun provideSubjectUseCases(repository: SubjectRepository): SubjectUseCases {
        // Create a fake or minimal implementation of SubjectUseCases
        return SubjectUseCases(
            addSubject = AddSubjectUseCase(repository),
            updateSubject = UpdateSubjectUseCase(repository),
            deleteSubject = DeleteSubjectUseCase(repository),
            deleteSubjectById = DeleteSubjectByIdUseCase(repository),
            getSubjectById = GetSubjectByIdUseCase(repository),
            getAllSubjects = GetAllSubjectsUseCase(repository),
            getSubjectsBySemester = GetSubjectsBySemesterUseCase(repository),
            getEventsOfSubject = GetEventsOfSubjectUseCase(repository),
            getNotesOfSubject = GetNotesOfSubjectUseCase(repository),
            validateTitle = ValidateSubjectTitleUseCase()
        )
    }
}