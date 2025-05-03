package com.kakos.uniAID.calendar.domain.model

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kakos.uniAID.core.domain.subject.domain.model.Subject
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

/**
 * Represents a calendar event entity.
 *
 * @property id Unique identifier for the event, auto-generated when null.
 * @property title Title of the event, cannot be empty.
 * @property description Additional details about the event, empty by default.
 * @property color Index of the event's color in the eventColors list.
 * @property location Physical or virtual location where the event takes place.
 * @property startDate Date when the event begins.
 * @property startTime Time when the event begins.
 * @property endDate Date when the event ends.
 * @property endTime Time when the event ends.
 * @property repeat Type of repetition pattern (NONE, DAILY, WEEKLY, MONTHLY, YEARLY).
 * @property repeatDifference Interval between repetitions (1=every day/week/month, 2=every other, etc.).
 * @property repeatEndDate Final date until which the event repeats.
 * @property repeatId Identifier grouping related recurring events for batch operations.
 * @property repeatDays Days of week when the event repeats (for WEEKLY repeat type).
 * @property allDay Whether the event spans the entire day, ignoring time values.
 * @property subjectId Foreign key reference to an associated Subject.
 * @property subjectName Name of the associated subject, cached to reduce database queries.
 */
@Entity(
    tableName = "events",
    foreignKeys = [ForeignKey(
        entity = Subject::class,
        parentColumns = ["id"],
        childColumns = ["subjectId"],
        onDelete = ForeignKey.SET_NULL
    )],
    indices = [Index("subjectId")]
)
data class Event(
    @PrimaryKey val id: Int? = null, // Nullable, auto-generated
    val title: String, // Title of the event can't be empty
    val description: String = "", // Description of the event, by default empty string
    val color: Int = 0, // Index of the color in the list of eventColors, by default 0
    val location: String? = null, // Location of the event, nullable
    val startDate: LocalDate, // Start date of the event
    val startTime: LocalTime, // Start time of the event
    val endDate: LocalDate, // End date of the event
    val endTime: LocalTime, // End time of the event
    val repeat: Repeat = Repeat.NONE, // Repeat type of the event by default enum class Repeat NONE
    val repeatDifference: Long = 1, // Time unit difference between repeating events for eg. daily: 1: every day, 2: every other day etc...
    val repeatEndDate: LocalDate, // Nullable, end date of the repeating event
    val repeatId: Int? = null, // Id to group repeating events together, for updating and deleting
    val repeatDays: List<DayOfWeek> = emptyList(), // Days of the week to repeat the event on, if the event is weekly repeating, empty list otherwise
    val allDay: Boolean = false, // If the event is an all-day event -> doesn't show the time
    val subjectId: Int? = null, // Nullable, foreign key to SubjectEntity
    val subjectName: String? = null // Name of the subject, its for not always having to query the database
) {
    // Check if the event is valid, the start date and time can't be after the end date and time
    fun isValidEvent(): Boolean {
        val startDateTime = startDate.atTime(startTime)
        val endDateTime = endDate.atTime(endTime)
        return !(startDateTime.isAfter(endDateTime) && !allDay) // If the event is all-day, the time doesn't matter -> error persists if it turned false again
    }

    // Possible colors for the events (can be expanded)
    companion object {
        private val greenBright = Color(0xFF1EB980)
        private val greenDark = Color(0xFF0D5940)
        private val blueBright = Color(0xFF00509D)
        private val blueDark = Color(0xFF002D5C)
        private val orangeBright = Color(0xFFD9822B)
        private val orangeDark = Color(0xFF8C4E1A)
        private val redBright = Color(0xFFD82B2B)
        private val redDark = Color(0xFF8C1A1A)
        private val purpleBright = Color(0xFF6B2BD8)
        private val purpleDark = Color(0xFF3E1A8C)
        private val pinkBright = Color(0xFFD82B82)
        private val pinkDark = Color(0xFF8C1A4D)
        private val yellowBright = Color(0xFFD8D82B)
        private val yellowDark = Color(0xFF8C8C1A)
        private val tealBright = Color(0xFF2BD8D0)
        private val tealDark = Color(0xFF1A8C8C)

        val eventColors = listOf(
            greenBright to "Green Bright",
            greenDark to "Green Dark",
            blueBright to "Blue Bright",
            blueDark to "Blue Dark",
            orangeBright to "Orange Bright",
            orangeDark to "Orange Dark",
            redBright to "Red Bright",
            redDark to "Red Dark",
            purpleBright to "Purple Bright",
            purpleDark to "Purple Dark",
            pinkBright to "Pink Bright",
            pinkDark to "Pink Dark",
            yellowBright to "Yellow Bright",
            yellowDark to "Yellow Dark",
            tealBright to "Teal Bright",
            tealDark to "Teal Dark"
        )
    }
}

/**
 * Sealed class representing events for *event repetition*.
 *
 * Defines possible repetition types that can occur for calendar events.
 */
enum class Repeat {
    NONE, DAILY, WEEKLY, MONTHLY, YEARLY
}


/**
 * Exception for *event validation errors*.
 *
 * @property message Message is a string which defines the issue.
 */
class InvalidEventException(message: String) : Exception(message)