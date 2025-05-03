package com.kakos.uniAID.calendar.domain.repository

import com.kakos.uniAID.calendar.domain.model.Event
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Repository interface for event data management.
 *
 * Handles access to event data, abstracting the underlying data source from the application.
 * Provides methods for CRUD operations and specialized queries on events.
 *
 */
interface EventRepository {

    fun getAllEvents(): Flow<List<Event>>

    fun getEventsByDate(date: LocalDate): Flow<List<Event>>

    fun getEventsInRange(start: LocalDate, end: LocalDate): Flow<List<Event>>

    suspend fun getEventById(eventId: Int): Event?

    suspend fun getEventsByRepeatId(repeatId: Int?): List<Event>

    suspend fun insertEvent(event: Event): Long

    suspend fun insertEvents(events: List<Event>)

    suspend fun updateEvent(event: Event)

    suspend fun deleteEvent(event: Event)

    suspend fun deleteEventsByRepeatId(repeatId: Int?)

    suspend fun deleteEventsByRepeatIdAndStartDate(repeatId: Int?, startDate: LocalDate)

    suspend fun deleteEventById(eventId: Int)

}