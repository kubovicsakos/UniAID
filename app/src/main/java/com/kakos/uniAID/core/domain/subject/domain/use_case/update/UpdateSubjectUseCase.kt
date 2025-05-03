package com.kakos.uniAID.core.domain.subject.domain.use_case.update

import android.util.Log
import com.kakos.uniAID.core.domain.subject.domain.model.InvalidSubjectException
import com.kakos.uniAID.core.domain.subject.domain.model.Subject
import com.kakos.uniAID.core.domain.subject.domain.repository.SubjectRepository

/**
 * Use case for updating a subject.
 *
 * Encapsulates the logic for updating a subject, handling validation
 * and interacting with the repository.
 *
 * @property repository The SubjectRepository used for data operations.
 * @throws InvalidSubjectException When the title is blank.
 */
class UpdateSubjectUseCase(
    private val repository: SubjectRepository
) {
    private val tag = "UpdateSubjectUseCase"

    @Throws(InvalidSubjectException::class)
    suspend operator fun invoke(subject: Subject) {
        if (subject.title.isBlank()) {
            Log.e(tag, "EXCEPTION: Title cannot be blank")
            throw InvalidSubjectException("Title cannot be blank")
        }
        if (subject.semester == null || subject.semester < 1) {
            Log.e(tag, "EXCEPTION: Semester cannot be null or negative")
            throw InvalidSubjectException("Semester cannot be null or negative")
        }
        if (subject.credit == null || subject.credit < 0) {
            Log.e(tag, "EXCEPTION: Credit cannot be null or negative")
            throw InvalidSubjectException("Credit cannot be null or negative")
        }
        if (subject.finalGrade != null && (subject.finalGrade < 1 || subject.finalGrade > 5)) {
            Log.e(tag, "EXCEPTION: Final grade must be between 1 and 5")
            throw InvalidSubjectException("Final grade must be between 1 and 5")
        }
        if (subject.grade != null && (subject.grade < 1 || subject.grade > 5)) {
            Log.e(tag, "EXCEPTION: Grade must be between 1 and 5")
            throw InvalidSubjectException("Grade must be between 1 and 5")
        }
        if (subject.id == null) {
            Log.e(tag, "EXCEPTION: Invalid subject id")
            throw InvalidSubjectException("Invalid subject id")
        } else if (subject.id <= 0) {
            Log.e(tag, "EXCEPTION: Invalid subject id")
            throw InvalidSubjectException("Invalid subject id")
        }

        Log.d(tag, "Subject updated: $subject")
        repository.update(subject)
    }
}