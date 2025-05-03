package com.kakos.uniAID.core.domain.subject.domain.use_case.delete

import android.util.Log
import com.kakos.uniAID.core.domain.subject.domain.repository.SubjectRepository

/**
 * Use case for deleting a subject by its ID.
 *
 * Encapsulates the logic for removing subjects from the database, handling validation
 * and interacting with the repository.
 *
 * @property repository The SubjectRepository used for data operations.
 * @throws IllegalArgumentException if subjectId is 0 or negative
 */
class DeleteSubjectByIdUseCase(
    private val repository: SubjectRepository
) {
    private val tag = "DeleteSubjectByIdUseCase"

    suspend operator fun invoke(subjectId: Int) {
        if (subjectId <= 0) {
            Log.e(tag, "EXCEPTION: Invalid subject id: $subjectId")
            throw IllegalArgumentException("Invalid subject id: $subjectId")
        }
        Log.d(tag, "Subject deleted with id: $subjectId")
        repository.deleteSubjectById(subjectId)
    }
}