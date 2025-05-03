package com.kakos.uniAID.core.domain.subject.domain.use_case.read

import android.util.Log
import com.kakos.uniAID.calendar.domain.model.Event
import com.kakos.uniAID.core.domain.subject.domain.repository.SubjectRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * Use case for retrieving events of a subject.
 *
 * Encapsulates the logic for fetching events associated with a subject,
 * handling exceptions and interacting with the repository.
 *
 * @property repository The SubjectRepository used for data operations.
 * @throws IllegalArgumentException When the subject ID is invalid or negative.
 */
class GetEventsOfSubjectUseCase(
    private val repository: SubjectRepository
) {
    private val tag = "GetEventsOfSubjectUseCase"
    operator fun invoke(subjectId: Int): Flow<List<Event>> = flow {
        if (subjectId <= 0) {
            Log.e(tag, "EXCEPTION: Invalid subject id: $subjectId")
            throw IllegalArgumentException("Invalid subject id: $subjectId")
        }
        try {
            val events = repository.getSubjectWithEvents(subjectId).events
            Log.d(tag, "Events retrieved for subject with id: $subjectId")
            emit(events)
        } catch (e: Exception) {
            Log.e(tag, "EXCEPTION: ", e)
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)
}