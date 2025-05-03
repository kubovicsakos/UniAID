package com.kakos.uniAID.core.domain.subject.domain.use_case.read

import android.util.Log
import com.kakos.uniAID.core.domain.subject.domain.model.InvalidSubjectException
import com.kakos.uniAID.core.domain.subject.domain.model.Subject
import com.kakos.uniAID.core.domain.subject.domain.repository.SubjectRepository

/**
 * Use case for retrieving a subject by ID.
 *
 * Encapsulates the logic for fetching a subject by its ID, handling validation
 * and interacting with the repository.
 *
 * @property repository The SubjectRepository used for data operations.
 */
class GetSubjectByIdUseCase(
    private val repository: SubjectRepository
) {
    private val tag = "GetSubjectByIdUseCase"

    @Throws(InvalidSubjectException::class)
    suspend operator fun invoke(subjectId: Int): Subject? {
        if (subjectId <= 0) {
            Log.e(tag, "EXCEPTION: Invalid subject id $subjectId")
            throw IllegalArgumentException("Invalid subject id $subjectId")
        }
        Log.d(tag, "Subject retrieved with id: $subjectId")
        return repository.getSubjectById(subjectId)
    }
}