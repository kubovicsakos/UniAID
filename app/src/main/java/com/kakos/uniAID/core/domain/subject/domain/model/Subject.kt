package com.kakos.uniAID.core.domain.subject.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a university subject entity. THIS
 *
 * Used across multiple features for managing course information and academic tracking.
 *
 * @property id Unique identifier of the subject.
 * @property title Name of the subject.
 * @property description Detailed explanation of the subject content.
 * @property semester Academic semester when the subject is taught.
 * @property credit Number of credit points awarded for the subject.
 * @property finalGrade Official final grade recorded for the subject.
 * @property grade Internal grade used by statistics feature (distinct from finalGrade).
 */
@Entity(tableName = "subjects")
data class Subject(
    @PrimaryKey val id: Int? = null,
    val title: String,
    val description: String = "",
    val semester: Int? = null,
    val credit: Int? = null,
    val finalGrade: Int? = null,
    val grade: Int? = null
)

/**
 * Exception thrown when subject data is invalid.
 *
 * Used for validation error handling during subject operations.
 *
 * @param message Description of the validation error.
 */
class InvalidSubjectException(message: String) : Exception(message)