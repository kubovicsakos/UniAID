package com.kakos.uniAID.notes.domain.model

import androidx.room.Embedded
import androidx.room.Relation
import com.kakos.uniAID.core.domain.subject.domain.model.Subject

/**
 * Represents a Note entity with its associated Subject.
 *
 * Used for retrieving related entities in a single database query.
 *
 * @property note The embedded Note entity.
 * @property subject The associated Subject entity (nullable).
 */
data class NoteWithSubject(
    @Embedded val note: Note,
    @Relation(
        parentColumn = "subjectId",
        entityColumn = "id"
    )
    val subject: Subject?
)
