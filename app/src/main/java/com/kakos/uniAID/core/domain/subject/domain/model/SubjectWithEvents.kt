package com.kakos.uniAID.core.domain.subject.domain.model

import androidx.room.Embedded
import androidx.room.Relation
import com.kakos.uniAID.calendar.domain.model.Event

/**
 * Represents a Subject along with its associated Events. THIS
 *
 * This relational model connects a Subject with its related Events.
 *
 * @property subject The embedded Subject entity.
 * @property events List of Event objects related to the subject.
 */
data class SubjectWithEvents(
    @Embedded val subject: Subject,
    @Relation(
        parentColumn = "id",
        entityColumn = "subjectId"
    )
    val events: List<Event>
)
