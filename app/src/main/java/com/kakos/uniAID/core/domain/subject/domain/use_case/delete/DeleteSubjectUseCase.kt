package com.kakos.uniAID.core.domain.subject.domain.use_case.delete

import android.util.Log
import com.kakos.uniAID.core.domain.subject.domain.model.InvalidSubjectException
import com.kakos.uniAID.core.domain.subject.domain.model.Subject
import com.kakos.uniAID.core.domain.subject.domain.repository.SubjectRepository

/**
 * Use case for deleting a subject.
 *
 * Encapsulates the logic for removing subjects from the database, handling validation
 * and interacting with the repository.
 *
 * @property repository The SubjectRepository used for data operations.
 * @throws IllegalArgumentException When the subject ID is invalid or subject is null under that id.
 */
class DeleteSubjectUseCase(
    private val repository: SubjectRepository
) {
    private val tag = "DeleteSubjectUseCase"

    @Throws(InvalidSubjectException::class)
    suspend operator fun invoke(subject: Subject) {
        if (subject.id == null) {
            Log.e(tag, "EXCEPTION: Invalid subject id")
            throw IllegalArgumentException("Invalid subject id null")
        } else if (subject.id <= 0) {
            Log.e(tag, "EXCEPTION: Invalid subject id")
            throw IllegalArgumentException("Invalid subject id ${subject.id}")
        }
        Log.d(tag, "Subject deleted: $subject")
        repository.delete(subject)
    }
}