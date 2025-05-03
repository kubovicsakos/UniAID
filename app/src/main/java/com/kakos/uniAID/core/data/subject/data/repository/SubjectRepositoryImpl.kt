package com.kakos.uniAID.core.data.subject.data.repository

import com.kakos.uniAID.calendar.data.EventDao
import com.kakos.uniAID.core.data.subject.data.SubjectDao
import com.kakos.uniAID.core.domain.subject.domain.model.Subject
import com.kakos.uniAID.core.domain.subject.domain.model.SubjectWithEvents
import com.kakos.uniAID.core.domain.subject.domain.model.SubjectWithNotes
import com.kakos.uniAID.core.domain.subject.domain.repository.SubjectRepository
import com.kakos.uniAID.notes.data.NoteDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Implementation of [SubjectRepository] interface. THIS
 *
 * Handles data operations through the underlying DAOs.
 *
 * @property dao Data Access Object for Subject entities.
 * @property eventDao Data Access Object for Event entities.
 * @property noteDao Data Access Object for Note entities.
 */
class SubjectRepositoryImpl(
    private val dao: SubjectDao,
    private val eventDao: EventDao,
    private val noteDao: NoteDao
) : SubjectRepository {

    override suspend fun insert(subject: Subject): Long {
        return dao.insert(subject)
    }

    override suspend fun update(subject: Subject) {
        dao.update(subject)
    }

    override suspend fun delete(subject: Subject) {
        withContext(Dispatchers.IO) {
            // Clear subjectId and subjectName from associated notes and events
            subject.id?.let {
                eventDao.clearSubjectFromEvents(it)
                noteDao.clearSubjectFromNotes(it)
            }
            // Delete the subject
            dao.delete(subject)
        }
    }

    override suspend fun deleteSubjectById(subjectId: Int) {

        subjectId.let {
            eventDao.clearSubjectFromEvents(it)
            noteDao.clearSubjectFromNotes(it)
        }
        dao.deleteSubjectById(subjectId)
    }

    override suspend fun getSubjectById(subjectId: Int): Subject? {
        return dao.getSubjectById(subjectId)
    }

    override fun getAllSubjects(): Flow<List<Subject>> {
        return dao.getAllSubjects()
    }

    override fun getSubjectsBySemester(semester: Int): Flow<List<Subject>> {
        return dao.getSubjectsBySemester(semester)
    }

    override suspend fun getSubjectWithNotes(subjectId: Int): SubjectWithNotes {
        return withContext(Dispatchers.IO) {
            dao.getSubjectWithNotes(subjectId)
        }
    }

    override suspend fun getSubjectWithEvents(subjectId: Int): SubjectWithEvents {
        return withContext(Dispatchers.IO) {
            dao.getSubjectWithEvents(subjectId)
        }
    }


}