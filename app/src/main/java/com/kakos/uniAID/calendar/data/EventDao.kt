package com.kakos.uniAID.calendar.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.kakos.uniAID.calendar.domain.model.Event
import com.kakos.uniAID.calendar.domain.model.EventWithSubject
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Data Access Object for Event entity.
 *
 * Provides CRUD operations for the events table.
 */
@Dao
interface EventDao {
    @Insert
    suspend fun insertEvent(event: Event): Long

    @Insert
    suspend fun insertEvents(events: List<Event>)

    @Update
    suspend fun updateEvent(event: Event)

    @Delete
    suspend fun deleteEvent(event: Event)

    @Query("DELETE FROM events WHERE id = :eventId")
    suspend fun deleteEventById(eventId: Int)

    @Query("DELETE FROM events WHERE repeatId = :repeatId AND startDate >= :startDate")
    suspend fun deleteEventsByRepeatIdAndStartDate(
        repeatId: Int?,
        startDate: LocalDate
    ) // Delete all events that are part of a repeating event starting from a specific date

    @Query("DELETE FROM events WHERE repeatId = :repeatId")
    suspend fun deleteEventsByRepeatId(repeatId: Int?) // Delete all events that are part of a repeating event

    @Query("SELECT * FROM events WHERE repeatId = :repeatId")
    suspend fun getEventsByRepeatId(repeatId: Int?): List<Event>

    @Query("SELECT * FROM events WHERE id = :eventId")
    suspend fun getEventById(eventId: Int): Event?

    @Query(
        """
        SELECT * 
        FROM events 
        WHERE (:date BETWEEN startDate AND endDate)
        """
    )
    fun getEventsByDate(date: LocalDate): Flow<List<Event>>

    @Query(
        "SELECT * FROM events WHERE " +
                "(startDate BETWEEN :start AND :end) OR " +
                "(endDate BETWEEN :start AND :end) OR " +
                "(startDate <= :start AND endDate >= :end)"
    )
    fun getEventsInRange(start: LocalDate, end: LocalDate): Flow<List<Event>>

    @Query("SELECT * FROM events")
    fun getAllEvents(): Flow<List<Event>>

    @Transaction
    @Query("SELECT * FROM events")
    fun getEventsWithSubjects(): Flow<List<EventWithSubject>>

    @Query("UPDATE events SET subjectId = null, subjectName = null WHERE subjectId = :subjectId")
    suspend fun clearSubjectFromEvents(subjectId: Int)

}