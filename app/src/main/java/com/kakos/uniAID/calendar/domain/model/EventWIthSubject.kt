package com.kakos.uniAID.calendar.domain.model

import androidx.room.Embedded
import androidx.room.Relation
import com.kakos.uniAID.core.domain.subject.domain.model.Subject

/**
 * Represents [Event] - [Subject] relationship.
 */
data class EventWithSubject(
    @Embedded val event: Event,
    @Relation(
        parentColumn = "subjectId",
        entityColumn = "id"
    )
    val subject: Subject?
)
