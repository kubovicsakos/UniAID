package com.kakos.uniAID.subjectAndStats.presentation.subject.subject_details.util

import com.kakos.uniAID.core.domain.subject.domain.model.Subject

/**
 * Sealed class representing events for subject details feature.
 *
 * Defines possible user interactions or system events
 * that can occur within the subject details feature.
 */
sealed class SubjectDetailsEvent {
    data class GetSubjectById(val subjectId: Int) : SubjectDetailsEvent()
    data class DeleteSubject(val subject: Subject) : SubjectDetailsEvent()
}