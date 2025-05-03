package com.kakos.uniAID.core.domain.subject.domain.use_case.read

import android.util.Log
import com.kakos.uniAID.core.domain.subject.domain.model.Subject
import com.kakos.uniAID.core.domain.subject.domain.repository.SubjectRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for retrieving all subjects.
 *
 * Encapsulates the logic for fetching all subjects from the database,
 * and interacting with the repository.
 *
 * @property repository The SubjectRepository used for data operations.
 */
class GetAllSubjectsUseCase(
    private val repository: SubjectRepository
) {
    private val tag = "GetAllSubjectsUseCase"
    operator fun invoke(): Flow<List<Subject>> {
        Log.d(tag, "Subjects retrieved")
        return repository.getAllSubjects()
    }
}