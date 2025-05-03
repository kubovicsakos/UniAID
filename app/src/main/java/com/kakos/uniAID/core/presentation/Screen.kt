package com.kakos.uniAID.core.presentation

import kotlinx.serialization.Serializable

/**
 * Represents the different screens in the application.
 *
 * Each screen is represented as a data class or object, allowing for easy serialization and deserialization.
 * This is particularly useful for navigation and state management within the app.
 *
 * @property CalendarScreen Represents the calendar screen.
 * @property EditEventScreen Represents the screen for editing an event, with optional event ID and start date.
 * @property EventDetailsScreen Represents the screen displaying details of an event, with an optional event ID.
 * @property EditNoteScreen Represents the screen for editing a note, with an optional note ID.
 * @property NotesScreen Represents the notes screen.
 * @property SettingsScreen Represents the settings screen.
 * @property AllStatisticsScreen Represents the screen displaying all statistics.
 * @property StatisticsScreen Represents the statistics screen.
 * @property EditSubjectScreen Represents the screen for editing a subject, with an optional subject ID.
 * @property SubjectDetailsScreen Represents the screen displaying details of a subject, with an optional subject ID.
 * @property SubjectScreen Represents the subject screen.
 */

sealed interface Screen {

    @Serializable
    data object CalendarScreen : Screen

    @Serializable
    data class EditEventScreen(
        val eventId: Int? = -1,
        val startDate: String? = null, // String instead of LocalDate for serialization
    ) : Screen

    @Serializable
    data class EventDetailsScreen(
        val eventId: Int? = -1
    ) : Screen

    @Serializable
    data class EditNoteScreen(
        val noteId: Int? = -1
    ) : Screen

    @Serializable
    data object NotesScreen : Screen

    @Serializable
    data object SettingsScreen : Screen

    @Serializable
    data object AllStatisticsScreen : Screen

    @Serializable
    data object StatisticsScreen : Screen

    @Serializable
    data class EditSubjectScreen(
        val subjectId: Int? = -1
    ) : Screen

    @Serializable
    data class SubjectDetailsScreen(
        val subjectId: Int? = -1
    ) : Screen

    @Serializable
    data object SubjectScreen : Screen
}