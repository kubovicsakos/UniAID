package com.kakos.uniAID.calendar.data.repository

import com.kakos.uniAID.calendar.domain.model.Event
import com.kakos.uniAID.calendar.domain.model.Repeat
import com.kakos.uniAID.calendar.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDate
import java.time.LocalTime

class FakeAndroidEventRepository : EventRepository {
    private var eventItems = MutableStateFlow<List<Event>>(emptyList())

    fun shouldHaveFilledList(shouldHaveFilledList: Boolean) {
        eventItems = if (shouldHaveFilledList) {
            MutableStateFlow(
                listOf(
                    Event(
                        id = 1,
                        title = "a",
                        description = "Description 1",
                        color = 0,
                        location = null,
                        startDate = LocalDate.now(),
                        endDate = LocalDate.now(),
                        startTime = LocalTime.now(),
                        endTime = LocalTime.now().plusHours(1),
                        repeatId = null,
                        repeat = Repeat.NONE,
                        repeatDifference = 1,
                        repeatEndDate = LocalDate.now().plusWeeks(1),
                        repeatDays = emptyList(),
                        allDay = false,
                        subjectId = null,
                        subjectName = null
                    ),
                    Event(
                        id = 2,
                        title = "b",
                        description = "Description 2",
                        color = 1,
                        location = "location1",
                        startDate = LocalDate.now(),
                        endDate = LocalDate.now(),
                        startTime = LocalTime.now(),
                        endTime = LocalTime.now().plusHours(1),
                        repeatId = null,
                        repeat = Repeat.NONE,
                        repeatDifference = 1,
                        repeatEndDate = LocalDate.now().plusWeeks(1),
                        repeatDays = emptyList(),
                        allDay = false,
                        subjectId = null,
                        subjectName = null
                    ),
                    Event(
                        id = 3,
                        title = "c",
                        description = "Description 3",
                        color = 2,
                        location = "location2",
                        startDate = LocalDate.now(),
                        endDate = LocalDate.now(),
                        startTime = LocalTime.now(),
                        endTime = LocalTime.now().plusHours(1),
                        repeatId = null,
                        repeat = Repeat.NONE,
                        repeatDifference = 1,
                        repeatEndDate = LocalDate.now().plusWeeks(1),
                        repeatDays = emptyList(),
                        allDay = false,
                        subjectId = null,
                        subjectName = null
                    )
                )
            )
        } else {
            MutableStateFlow(emptyList())
        }
    }

    override fun getAllEvents(): Flow<List<Event>> {
        return eventItems
    }

    override fun getEventsByDate(date: LocalDate): Flow<List<Event>> {
        val events = eventItems.value.filter { date in it.startDate..it.endDate }
        return MutableStateFlow(events)
    }

    override fun getEventsInRange(start: LocalDate, end: LocalDate): Flow<List<Event>> {
        val events = eventItems.value.filter {
            (it.startDate in start..end) ||
                    (it.endDate in start..end) ||
                    (it.startDate <= start && it.endDate >= end)
        }
        return MutableStateFlow(events)
    }

    override suspend fun getEventById(eventId: Int): Event? {
        val event = eventItems.value.find { it.id == eventId }
        return event
    }

    override suspend fun getEventsByRepeatId(repeatId: Int?): List<Event> {
        val events = eventItems.value.filter { it.repeatId == repeatId }
        return events
    }

    override suspend fun insertEvent(event: Event): Long {
        if (event.id == null) {
            val eventItemsLast = eventItems.value.last()
            val newId = eventItemsLast.id!!.plus(1)
            eventItems.value += event.copy(id = newId)
            return newId.toLong()
        } else {
            eventItems.value += event
            return event.id!!.toLong()
        }
    }

    override suspend fun insertEvents(events: List<Event>) {
        for (event in events) {
            if (event.id == null) {
                val eventItemsLast = eventItems.value.last()
                val newId = eventItemsLast.id!!.plus(1)
                eventItems.value += event.copy(id = newId)
            } else {
                eventItems.value += event
            }
        }
    }

    override suspend fun updateEvent(event: Event) {
        eventItems.value = eventItems.value.map {
            if (it.id == event.id) {
                event
            } else {
                it
            }
        }
    }

    override suspend fun deleteEvent(event: Event) {
        eventItems.value -= event
    }

    override suspend fun deleteEventsByRepeatId(repeatId: Int?) {
        eventItems.value = eventItems.value.filter { it.repeatId != repeatId }
    }

    override suspend fun deleteEventsByRepeatIdAndStartDate(repeatId: Int?, startDate: LocalDate) {
        eventItems.value =
            eventItems.value.filter { it.repeatId != repeatId && it.startDate < startDate }
    }

    override suspend fun deleteEventById(eventId: Int) {
        eventItems.value = eventItems.value.filter { it.id != eventId }
    }
}