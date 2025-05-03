package com.kakos.uniAID.core.domain.subject.domain.use_case.read

import android.util.Log
import com.kakos.uniAID.core.domain.subject.domain.model.Subject
import com.kakos.uniAID.core.domain.subject.domain.repository.SubjectRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for retrieving subjects by semester.
 *
 * Encapsulates the logic for fetching subjects associated with a semester and
 * interacting with the repository.
 *
 * @property repository The SubjectRepository used for data operations.
 * @throws IllegalArgumentException When the semester is 0 or negative.
 */
class GetSubjectsBySemesterUseCase(
    private val repository: SubjectRepository
) {
    private val tag = "GetSubjectsBySemesterUseCase"
    operator fun invoke(semester: Int): Flow<List<Subject>> {
        if (semester <= 0) {
            Log.e(tag, "EXCEPTION: Invalid semester: $semester must be 1 or more")
            throw IllegalArgumentException("Invalid semester: $semester must be 1 or more")
        }
        Log.d(tag, "Subjects retrieved for semester: $semester")
        return repository.getSubjectsBySemester(semester)
    }
}