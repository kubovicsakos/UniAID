package com.kakos.uniAID.subjectAndStats.presentation.subject.subject_details.util

import com.kakos.uniAID.calendar.domain.model.Event
import com.kakos.uniAID.core.domain.subject.domain.model.Subject
import com.kakos.uniAID.notes.domain.model.Note

/**
 * Represents UI state for subject details screen.
 *
 * @property subject The Subject containing details of the selected subject, or null if not loaded.
 * @property notes List of Notes associated with the subject.
 * @property events List of Events related to the subject.
 * @property error Error message if any occurred during data loading.
 */
data class SubjectDetailsState(
    val subject: Subject? = null,
    val notes: List<Note> = emptyList(),
    val events: List<Event> = emptyList(),
    val error: String = ""
)
