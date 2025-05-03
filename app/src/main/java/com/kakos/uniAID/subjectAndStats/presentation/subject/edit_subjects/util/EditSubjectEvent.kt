package com.kakos.uniAID.subjectAndStats.presentation.subject.edit_subjects.util

/**
 * Sealed class representing events for subject editing.
 *
 * Defines possible user interactions or system events
 * that can occur within the subject editing feature.
 */
sealed class EditSubjectEvent {
    data class GetSubjectById(val subjectId: Int) : EditSubjectEvent()
    data object SaveSubject : EditSubjectEvent()
    data object ValidateEvent : EditSubjectEvent()
    data class EnteredTitle(val title: String) : EditSubjectEvent()
    data class EnteredDescription(val description: String) : EditSubjectEvent()
    data class EnteredSemester(val semester: Int?) : EditSubjectEvent()
    data class EnteredCredit(val credit: Int?) : EditSubjectEvent()
    data class EnteredFinalGrade(val finalGrade: Int?) : EditSubjectEvent()
}