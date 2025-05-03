package com.kakos.uniAID.calendar.domain.use_case

import com.kakos.uniAID.calendar.data.repository.FakeEventRepository
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

fun fakeEventUseCases(
    repository: FakeEventRepository
): EventUseCases {
    return EventUseCases(
        addEventUseCase = AddEventUseCase(repository),
        getEventByIdUseCase = GetEventByIdUseCase(repository),
        getEventsByDateUseCase = GetEventsByDateUseCase(repository),
        updateEventUseCase = UpdateEventUseCase(repository),
        updateFutureRepeatingEventsUseCase = UpdateFutureRepeatingEventsUseCase(repository),
        updateAllRepeatingEventsUseCase = UpdateAllRepeatingEventsUseCase(repository),
        deleteEventUseCase = DeleteEventUseCase(repository),
        deleteAllRepeatUseCase = DeleteAllRepeatUseCase(repository),
        deleteRepeatFromDateUseCase = DeleteRepeatFromDateUseCase(repository),
        getAllEventsUseCase = GetAllEventsUseCase(repository),
        validateDateTimeUseCase = ValidateDateTimeUseCase(),
        validateRepeatUseCase = ValidateRepeatUseCase(),
        validateTitleUseCase = ValidateTitleUseCase(),
        getEventsInRangeUseCase = GetEventsInRangeUseCase(repository)
    )
}