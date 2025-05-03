package com.kakos.uniAID.calendar.data.repository

import com.kakos.uniAID.calendar.data.EventDao
import com.kakos.uniAID.calendar.domain.model.Event
import com.kakos.uniAID.calendar.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Implementation of [EventRepository] interface.
 *
 * Handles data operations through the underlying [EventDao].
 *
 * @property dao The Data Access Object used for event database operations.
 */
class EventRepositoryImpl(
    private val dao: EventDao
) : EventRepository {

    override fun getAllEvents(): Flow<List<Event>> {
        return dao.getAllEvents()
    }

    override fun getEventsByDate(date: LocalDate): Flow<List<Event>> {
        return dao.getEventsByDate(date)
    }

    override suspend fun getEventById(eventId: Int): Event? {
        return dao.getEventById(eventId)
    }

    override suspend fun getEventsByRepeatId(repeatId: Int?): List<Event> {
        return dao.getEventsByRepeatId(repeatId)
    }

    override suspend fun insertEvent(event: Event): Long {
        return dao.insertEvent(event)
    }

    override suspend fun insertEvents(events: List<Event>) {
        dao.insertEvents(events)
    }

    override suspend fun updateEvent(event: Event) {
        return dao.updateEvent(event)
    }

    override suspend fun deleteEvent(event: Event) {
        dao.deleteEvent(event)
    }

    override suspend fun deleteEventsByRepeatId(repeatId: Int?) {
        dao.deleteEventsByRepeatId(repeatId)
    }

    override suspend fun deleteEventsByRepeatIdAndStartDate(repeatId: Int?, startDate: LocalDate) {
        dao.deleteEventsByRepeatIdAndStartDate(repeatId, startDate)
    }

    override suspend fun deleteEventById(eventId: Int) {
        dao.deleteEventById(eventId)
    }

    override fun getEventsInRange(start: LocalDate, end: LocalDate): Flow<List<Event>> {
        return dao.getEventsInRange(start, end)
    }
}