package com.kakos.uniAID.core.domain.subject.domain.use_case.read

import android.util.Log
import com.kakos.uniAID.core.domain.subject.domain.repository.SubjectRepository
import com.kakos.uniAID.notes.domain.model.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * Use case for retrieving notes of a subject.
 *
 * Encapsulates the logic for fetching notes associated with a subject,
 * handling exceptions and interacting with the repository.
 *
 * @property repository The SubjectRepository used for data operations.
 * @throws IllegalArgumentException When the subject ID is invalid or negative.
 */
class GetNotesOfSubjectUseCase(
    private val repository: SubjectRepository
) {
    private val tag = "GetNotesOfSubjectUseCase"
    operator fun invoke(subjectId: Int): Flow<List<Note>> = flow {
        if (subjectId <= 0) {
            Log.e(tag, "EXCEPTION: Invalid subject id: $subjectId")
            throw IllegalArgumentException("Invalid subject id: $subjectId")
        }
        try {
            val notes = repository.getSubjectWithNotes(subjectId).notes
            Log.d(tag, "Notes retrieved for subject with id: $subjectId")
            emit(notes)
        } catch (e: Exception) {
            Log.e(tag, "Error", e)
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)
}

