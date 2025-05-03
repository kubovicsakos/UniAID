package com.kakos.uniAID.core.features.subject.domain

import com.kakos.uniAID.core.features.subject.data.repository.FakeSubjectRepository
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

fun fakeSubjectUseCases(
    repository: FakeSubjectRepository
): SubjectUseCases {
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
        validateTitle = ValidateSubjectTitleUseCase(),
    )
}