package com.kakos.uniAID.subjectAndStats.presentation.subject.subjects.util

import com.kakos.uniAID.core.domain.subject.domain.model.Subject

/**
 * Represents UI state for subjects screen.
 *
 * @property subjects The list of Subject objects shown on screen.
 * @property currentSemester The currently selected semester filter.
 * @property isLoading Indicates if data is currently being loaded.
 */
data class SubjectsState(
    val subjects: List<Subject> = emptyList(),
    val currentSemester: Int = 1,
    val isLoading: Boolean = true
)
