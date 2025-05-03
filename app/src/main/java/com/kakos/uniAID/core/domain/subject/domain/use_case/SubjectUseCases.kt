package com.kakos.uniAID.core.domain.subject.domain.use_case

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

/**
 * Data class that encapsulates all the use cases related to the Subject entity. THIS
 *
 * Acts as a central point for accessing and managing various subject-related operations.
 *
 * @property addSubject Use case for adding a new subject.
 * @property updateSubject Use case for updating an existing subject.
 * @property deleteSubject Use case for deleting a subject.
 * @property deleteSubjectById Use case for deleting a subject by its ID.
 * @property getSubjectById Use case for retrieving a subject by its ID.
 * @property getAllSubjects Use case for retrieving all subjects.
 * @property getSubjectsBySemester Use case for retrieving subjects filtered by a specific semester.
 * @property getEventsOfSubject Use case for retrieving all events associated with a specific subject.
 * @property getNotesOfSubject Use case for retrieving all notes associated with a specific subject.
 * @property validateTitle Use case for validating the title of a subject.
 */
data class SubjectUseCases(
    val addSubject: AddSubjectUseCase,
    val updateSubject: UpdateSubjectUseCase,
    val deleteSubject: DeleteSubjectUseCase,
    val deleteSubjectById: DeleteSubjectByIdUseCase,
    val getSubjectById: GetSubjectByIdUseCase,
    val getAllSubjects: GetAllSubjectsUseCase,
    val getSubjectsBySemester: GetSubjectsBySemesterUseCase,
    val getEventsOfSubject: GetEventsOfSubjectUseCase,
    val getNotesOfSubject: GetNotesOfSubjectUseCase,
    val validateTitle: ValidateSubjectTitleUseCase,
)