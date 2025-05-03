package com.kakos.uniAID.notes.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kakos.uniAID.core.domain.subject.domain.model.Subject
import java.time.LocalDateTime

/**
 * Represents a note entity in the application.
 *
 * @property id Unique identifier for the note (auto-generated if null).
 * @property title Title of the note.
 * @property content Main text content of the note.
 * @property creationTime Timestamp when the note was created.
 * @property lastModified Timestamp when the note was last modified.
 * @property darkTheme Flag indicating whether the note uses dark theme.
 * @property subjectId ID of the associated subject (nullable).
 * @property subjectName Name of the associated subject (nullable).
 */
@Entity(
    tableName = "notes",
    foreignKeys = [ForeignKey(
        entity = Subject::class,
        parentColumns = ["id"],
        childColumns = ["subjectId"],
        onDelete = ForeignKey.SET_NULL
    )],
    indices = [Index("subjectId")]
)
data class Note(
    @PrimaryKey val id: Int? = null,
    val title: String,
    val content: String = "",
    val creationTime: LocalDateTime,
    val lastModified: LocalDateTime,
    val darkTheme: Boolean,
    val subjectId: Int? = null,
    val subjectName: String? = null,
)

/**
 * Exception thrown when a note violates business rules.
 *
 * @property message Description of the validation failure.
 */
class InvalidNoteException(message: String) : Exception(message)


