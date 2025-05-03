package com.kakos.uniAID.core.domain.subject.domain.repository

import com.kakos.uniAID.core.domain.subject.domain.model.Subject
import com.kakos.uniAID.core.domain.subject.domain.model.SubjectWithEvents
import com.kakos.uniAID.core.domain.subject.domain.model.SubjectWithNotes
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Subject entities. THIS
 *
 * Defines data operations for managing subjects and their relationships.
 */
interface SubjectRepository {
    suspend fun insert(subject: Subject): Long
    suspend fun update(subject: Subject)
    suspend fun delete(subject: Subject)
    suspend fun deleteSubjectById(subjectId: Int)
    suspend fun getSubjectById(subjectId: Int): Subject?
    fun getAllSubjects(): Flow<List<Subject>>
    fun getSubjectsBySemester(semester: Int): Flow<List<Subject>>
    suspend fun getSubjectWithNotes(subjectId: Int): SubjectWithNotes
    suspend fun getSubjectWithEvents(subjectId: Int): SubjectWithEvents
}