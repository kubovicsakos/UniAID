package com.kakos.uniAID.calendar.presentation.edit_event.util

import com.kakos.uniAID.calendar.domain.model.Repeat
import com.kakos.uniAID.core.domain.subject.domain.model.Subject
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime


/**
 * Represents UI state for event editing screen.
 *
 * Encapsulates all form data, validation states, and available options
 * for creating or editing calendar events, including time feature_settings,
 * recurrence patterns, visual styling, and associated academic subjects.
 *
 * @property title State of the title input field with text and validation.
 * @property allDay Whether the event spans the entire day.
 * @property startTime Time when the event begins.
 * @property endTime Time when the event ends.
 * @property startDate Date when the event begins.
 * @property endDate Date when the event ends.
 * @property repeat Recurrence pattern for the event.
 * @property selectedDays Days of week selected for weekly repetition.
 * @property repeatDifference Interval between repeated occurrences.
 * @property repeatEndDate Date when event recurrence stops.
 * @property color Index of the selected event color.
 * @property location State of the location input field.
 * @property description State of the description input field.
 * @property dateTimeError Validation message for date/time issues.
 * @property repeatError Validation message for recurrence issues.
 * @property isFormValid Whether all inputs pass validation.
 * @property subjectId Identifier of associated academic subject.
 * @property subjectName Display name of associated subject.
 * @property subjects Available subjects for association.
 * @property filteredSubjects Filtered subset of subjects based on criteria.
 */
data class EditEventState(
    val title: EventTextFieldState = EventTextFieldState(),
    val allDay: Boolean = false,
    val startTime: LocalTime = LocalTime.of(LocalTime.now().hour, 0),
    val endTime: LocalTime = LocalTime.of(LocalTime.now().plusHours(1).hour, 0),
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate = LocalDate.now(),
    val repeat: Repeat = Repeat.NONE,
    val selectedDays: List<DayOfWeek> = emptyList(),
    val repeatDifference: Long = 1,
    val repeatEndDate: LocalDate = endDate.plusWeeks(1),
    val color: Int = 0,
    val location: EventTextFieldState = EventTextFieldState(),
    val description: EventTextFieldState = EventTextFieldState(),
    val dateTimeError: String? = null,
    val repeatError: String? = null,
    val isFormValid: Boolean = false,
    val subjectId: Int? = null,
    val subjectName: String? = null,
    val subjects: List<Subject> = emptyList(),
    val filteredSubjects: List<Subject> = emptyList(),
)