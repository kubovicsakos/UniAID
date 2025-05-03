package com.kakos.uniAID.calendar.di

import com.kakos.uniAID.calendar.domain.repository.EventRepository
import com.kakos.uniAID.calendar.domain.use_case.EventUseCases
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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestCalendarModule {

    @Provides
    @Singleton
    fun provideEventUseCases(
        repository: EventRepository
    ): EventUseCases {
        return EventUseCases(
            getEventsByDateUseCase = GetEventsByDateUseCase(repository),
            getAllEventsUseCase = GetAllEventsUseCase(repository),
            getEventByIdUseCase = GetEventByIdUseCase(repository),
            deleteEventUseCase = DeleteEventUseCase(repository),
            updateEventUseCase = UpdateEventUseCase(repository),
            updateFutureRepeatingEventsUseCase = UpdateFutureRepeatingEventsUseCase(repository),
            updateAllRepeatingEventsUseCase = UpdateAllRepeatingEventsUseCase(repository),
            addEventUseCase = AddEventUseCase(repository),
            deleteAllRepeatUseCase = DeleteAllRepeatUseCase(repository),
            deleteRepeatFromDateUseCase = DeleteRepeatFromDateUseCase(repository),
            validateDateTimeUseCase = ValidateDateTimeUseCase(),
            validateTitleUseCase = ValidateTitleUseCase(),
            validateRepeatUseCase = ValidateRepeatUseCase(),
            getEventsInRangeUseCase = GetEventsInRangeUseCase(repository)
        )
    }

}