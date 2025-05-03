package com.kakos.uniAID.core.domain.subject.domain.model

import androidx.room.Embedded
import androidx.room.Relation
import com.kakos.uniAID.notes.domain.model.Note

/**
 * Represents a Subject along with its associated Notes. THIS
 *
 * This relational model connects a Subject with its related Notes.
 *
 * @property subject The embedded Subject entity.
 * @property notes List of Note objects related to the subject.
 */
data class SubjectWithNotes(
    @Embedded val subject: Subject,
    @Relation(
        parentColumn = "id",
        entityColumn = "subjectId"
    )
    val notes: List<Note>
)
