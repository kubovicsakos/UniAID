package com.kakos.uniAID.subjectAndStats.presentation.subject.subjects.util

/**
 * Sealed class representing events for subjects feature.
 *
 * Defines possible user interactions or system events
 * that can occur within the subjects feature.
 */
sealed class SubjectsEvent {
    data class SetSemester(val semester: Int) : SubjectsEvent()
}