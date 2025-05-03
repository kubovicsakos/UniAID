package com.kakos.uniAID.calendar.domain.use_case

import com.kakos.uniAID.calendar.domain.use_case.event.create.AddEventUseCase
import com.kakos.uniAID.calendar.domain.use_case.event.delete.DeleteAllRepeatUseCase
import com.kakos.uniAID.calendar.domain.use_case.event.delete.DeleteEventUseCase
import com.kakos.uniAID.calendar.domain.use_case.event.delete.DeleteRepeatFromDateUseCase
import com.kakos.uniAID.calendar.domain.use_case.event.read.GetAllEventsUseCase
import com.kakos.uniAID.calendar.domain.use_case.event.read.GetEventByIdUseCase
import com.kakos.uniAID.calendar.domain.use_case.event.read.GetEventsByDateUseCase
import com.kakos.uniAID.calendar.domain.use_case.event.read.GetEventsInRangeUseCase
import com.kakos.uniAID.calendar.domain.use_case.event.update.UpdateAllRepeatingEventsUseCase
import com.kakos.uniAID.calendar.domain.use_case.event.update.UpdateEventUseCase
import com.kakos.uniAID.calendar.domain.use_case.event.update.UpdateFutureRepeatingEventsUseCase
import com.kakos.uniAID.calendar.domain.use_case.event.validate.ValidateDateTimeUseCase
import com.kakos.uniAID.calendar.domain.use_case.event.validate.ValidateRepeatUseCase
import com.kakos.uniAID.calendar.domain.use_case.event.validate.ValidateTitleUseCase

/**
 * Container for calendar event-related use cases.
 *
 * Encapsulates all operations for managing calendar events, grouping related
 * functionality into a single cohesive unit for dependency injection.
 *
 * @property addEventUseCase For creating new calendar events.
 * @property getEventByIdUseCase For retrieving events by unique identifier.
 * @property getEventsByDateUseCase For retrieving events on a specific date.
 * @property updateEventUseCase For modifying existing events.
 * @property updateFutureRepeatingEventsUseCase For updating future recurring event instances.
 * @property updateAllRepeatingEventsUseCase For updating entire recurring event series.
 * @property deleteEventUseCase For removing individual events.
 * @property deleteAllRepeatUseCase For removing entire recurring event series.
 * @property deleteRepeatFromDateUseCase For removing future recurring event instances.
 * @property getAllEventsUseCase For retrieving all calendar events.
 * @property validateDateTimeUseCase For ensuring chronological date/time consistency.
 * @property validateRepeatUseCase For validating recurring event parameters.
 * @property validateTitleUseCase For validating event title content.
 * @property getEventsInRangeUseCase For retrieving events within a date range.
 */
data class EventUseCases(
    val addEventUseCase: AddEventUseCase,
    val getEventByIdUseCase: GetEventByIdUseCase,
    val getEventsByDateUseCase: GetEventsByDateUseCase,
    val updateEventUseCase: UpdateEventUseCase,
    val updateFutureRepeatingEventsUseCase: UpdateFutureRepeatingEventsUseCase,
    val updateAllRepeatingEventsUseCase: UpdateAllRepeatingEventsUseCase,
    val deleteEventUseCase: DeleteEventUseCase,
    val deleteAllRepeatUseCase: DeleteAllRepeatUseCase,
    val deleteRepeatFromDateUseCase: DeleteRepeatFromDateUseCase,
    val getAllEventsUseCase: GetAllEventsUseCase,
    val validateDateTimeUseCase: ValidateDateTimeUseCase,
    val validateRepeatUseCase: ValidateRepeatUseCase,
    val validateTitleUseCase: ValidateTitleUseCase,
    val getEventsInRangeUseCase: GetEventsInRangeUseCase
)
