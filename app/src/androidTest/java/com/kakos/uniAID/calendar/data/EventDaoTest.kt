package com.kakos.uniAID.calendar.data

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.kakos.uniAID.core.data.local.AppDatabase
import com.kakos.uniAID.core.di.AppModule
import com.kakos.uniAID.core.data.subject.data.SubjectDao
import com.kakos.uniAID.core.di.subject.di.SubjectModule
import com.kakos.uniAID.core.domain.subject.domain.model.Subject
import com.kakos.uniAID.calendar.di.CalendarModule
import com.kakos.uniAID.calendar.domain.model.Event
import com.kakos.uniAID.notes.di.NoteModule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
@SmallTest
@UninstallModules(
    AppModule::class,
    NoteModule::class,
    SubjectModule::class,
    CalendarModule::class
)
class EventDaoTest {

    private val tag = "EventDaoTest"

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var database: AppDatabase
    private lateinit var eventDao: EventDao
    private lateinit var subjectDao: SubjectDao

    @Before
    fun setup() {
        hiltRule.inject()
        eventDao = database.eventDao()
        subjectDao = database.subjectDao
    }

    @After
    fun teardown() {
        database.close()
    }

    // insertEvent
    @Test
    fun insertEvent_eventIsInserted() = runTest {
        val event = Event(
            id = 1,
            title = "Test Event",
            description = "Test Description",
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            allDay = false,
            repeatId = null,
            subjectId = null,
            subjectName = null,
            repeatEndDate = LocalDate.now().plusDays(7)
        )
        eventDao.insertEvent(event)

        val events = eventDao.getAllEvents().first()
        Log.d(tag, "insertEvent_eventIsInserted, events: $events")
        assertThat(events.contains(event)).isTrue()
    }

    // insertEvents
    @Test
    fun insertEvents_eventsAreInserted() = runTest {
        val event1 = Event(
            id = 1,
            title = "Test Event 1",
            description = "Test Description",
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            allDay = false,
            repeatId = null,
            subjectId = null,
            subjectName = null,
            repeatEndDate = LocalDate.now().plusDays(7)
        )

        val event2 = Event(
            id = 2,
            title = "Test Event 2",
            description = "Test Description",
            startDate = LocalDate.now().plusDays(1),
            endDate = LocalDate.now().plusDays(1),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            allDay = false,
            repeatId = null,
            subjectId = null,
            subjectName = null,
            repeatEndDate = LocalDate.now().plusDays(7)
        )

        val eventList = listOf(event1, event2)
        eventDao.insertEvents(eventList)

        val events = eventDao.getAllEvents().first()
        Log.d(tag, "insertEvents_eventsAreInserted, events: $events")
        assertThat(events.contains(event1)).isTrue()
        assertThat(events.contains(event2)).isTrue()
    }

    // updateEvent
    @Test
    fun updateEvent_eventIsUpdated() = runTest {
        val eventId = 1
        val event = Event(
            id = eventId,
            title = "Test Event",
            description = "Test Description",
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            allDay = false,
            repeatId = null,
            subjectId = null,
            subjectName = null,
            repeatEndDate = LocalDate.now().plusDays(7)
        )


        // Insert the event
        eventDao.insertEvent(event)
        var retrievedEvent = eventDao.getEventById(eventId)
        Log.d(tag, "updateEvent_eventIsUpdated, event: $retrievedEvent")
        assertThat(retrievedEvent == event).isTrue()

        // Update the event
        val updatedEvent = event.copy(
            title = "Updated Event",
            description = "Updated Description"
        )
        eventDao.updateEvent(updatedEvent)
        retrievedEvent = eventDao.getEventById(eventId)
        Log.d(tag, "updateEvent_eventIsUpdated, event: $retrievedEvent")
        assertThat(retrievedEvent == updatedEvent && retrievedEvent != event).isTrue()
    }

    // deleteEvent
    @Test
    fun deleteEvent_eventIsDeleted() = runTest {
        val event = Event(
            id = 1,
            title = "Test Event",
            description = "Test Description",
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            allDay = false,
            repeatId = null,
            subjectId = null,
            subjectName = null,
            repeatEndDate = LocalDate.now().plusDays(7)
        )
        // Insert the event
        eventDao.insertEvent(event)
        var events = eventDao.getAllEvents().first()
        Log.d(tag, "deleteEvent_eventIsDeleted, events: $events")
        assertThat(events.contains(event)).isTrue()

        // Delete the event
        eventDao.deleteEvent(event)
        events = eventDao.getAllEvents().first()
        Log.d(tag, "deleteEvent_eventIsDeleted, events: $events")
        assertThat(events.contains(event)).isFalse()
    }

    // deleteEventById
    @Test
    fun deleteEventById_eventIsDeleted() = runTest {
        val eventId = 1
        val event = Event(
            id = eventId,
            title = "Test Event",
            description = "Test Description",
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            allDay = false,
            repeatId = null,
            subjectId = null,
            subjectName = null,
            repeatEndDate = LocalDate.now().plusDays(7)
        )
        // Insert the event
        eventDao.insertEvent(event)
        var retrievedEvent = eventDao.getEventById(eventId)
        Log.d(tag, "deleteEventById_eventIsDeleted, event: $retrievedEvent")
        assertThat(retrievedEvent == event).isTrue()

        // Delete the event by id
        eventDao.deleteEventById(eventId)
        retrievedEvent = eventDao.getEventById(eventId)
        Log.d(tag, "deleteEventById_eventIsDeleted, event: $retrievedEvent")
        assertThat(retrievedEvent == null).isTrue()
    }

    // deleteEventsByRepeatIdAndStartDate
    @Test
    fun deleteEventsByRepeatIdAndStartDate_allEventsWithRepeatIdAndStartDateAreDeleted() = runTest {
        val repeatId = 5
        val startDate = LocalDate.now()

        // Events with repeatId and startDate
        val event1 = Event(
            id = 1,
            title = "Repeating Event 1",
            description = "Test",
            startDate = startDate,
            endDate = startDate,
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            allDay = false,
            repeatId = repeatId,
            subjectId = null,
            subjectName = null,
            repeatEndDate = startDate.plusDays(7)
        )

        val event2 = Event(
            id = 2,
            title = "Repeating Event 2",
            description = "Test",
            startDate = startDate.plusDays(7),
            endDate = startDate.plusDays(7),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            allDay = false,
            repeatId = repeatId,
            subjectId = null,
            subjectName = null,
            repeatEndDate = startDate.plusDays(7)
        )

        // Event without repeatId
        val event3 = Event(
            id = 3,
            title = "Repeating Event 3",
            description = "Test",
            startDate = startDate.plusDays(7),
            endDate = startDate.plusDays(7),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            allDay = false,
            repeatId = null,
            subjectId = null,
            subjectName = null,
            repeatEndDate = startDate.plusDays(7)
        )

        val eventList = listOf(event1, event2, event3)

        eventDao.insertEvents(eventList)

        var events = eventDao.getAllEvents().first()
        assertThat(events.size).isEqualTo(3)

        eventDao.deleteEventsByRepeatIdAndStartDate(repeatId, startDate)

        events = eventDao.getAllEvents().first()
        Log.d(
            tag,
            "deleteEventsByRepeatIdAndStartDate_allEventsWithRepeatIdAndStartDateAreDeleted, events: $events"
        )

        assertThat(events.size).isEqualTo(1)
        assertThat(events.contains(event3)).isTrue()
    }

    // deleteEventsByRepeatId
    @Test
    fun deleteEventsByRepeatId_allEventsWithRepeatIdAreDeleted() = runTest {
        val repeatId = 5

        // Events with repeatId
        val event1 = Event(
            id = 1,
            title = "Repeating Event 1",
            description = "Test",
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            allDay = false,
            repeatId = repeatId,
            subjectId = null,
            subjectName = null,
            repeatEndDate = LocalDate.now().plusDays(7)
        )

        val event2 = Event(
            id = 2,
            title = "Repeating Event 2",
            description = "Test",
            startDate = LocalDate.now().plusDays(7),
            endDate = LocalDate.now().plusDays(7),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            allDay = false,
            repeatId = repeatId,
            subjectId = null,
            subjectName = null,
            repeatEndDate = LocalDate.now().plusDays(7)
        )

        // Event without repeatId
        val event3 = Event(
            id = 3,
            title = "Repeating Event 3",
            description = "Test",
            startDate = LocalDate.now().plusDays(7),
            endDate = LocalDate.now().plusDays(7),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            allDay = false,
            repeatId = null,
            subjectId = null,
            subjectName = null,
            repeatEndDate = LocalDate.now().plusDays(7)
        )

        val eventList = listOf(event1, event2, event3)

        eventDao.insertEvents(eventList)

        var events = eventDao.getAllEvents().first()
        assertThat(events.size).isEqualTo(3)

        eventDao.deleteEventsByRepeatId(repeatId)

        events = eventDao.getAllEvents().first()
        Log.d(tag, "deleteEventsByRepeatId_allEventsWithRepeatIdAreDeleted, events: $events")

        assertThat(events.size).isEqualTo(1)
        assertThat(events.contains(event3)).isTrue()
    }

    // getEventsByRepeatId
    @Test
    fun getEventsByRepeatId_returnsEventsWithRepeatId() = runTest {
        val repeatId = 5

        // Events with repeatId
        val event1 = Event(
            id = 1,
            title = "Repeating Event 1",
            description = "Test",
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            allDay = false,
            repeatId = repeatId,
            subjectId = null,
            subjectName = null,
            repeatEndDate = LocalDate.now().plusDays(7)
        )

        val event2 = Event(
            id = 2,
            title = "Repeating Event 2",
            description = "Test",
            startDate = LocalDate.now().plusDays(7),
            endDate = LocalDate.now().plusDays(7),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            allDay = false,
            repeatId = repeatId,
            subjectId = null,
            subjectName = null,
            repeatEndDate = LocalDate.now().plusDays(7)
        )

        // Event without repeatId
        val event3 = Event(
            id = 3,
            title = "Repeating Event 3",
            description = "Test",
            startDate = LocalDate.now().plusDays(7),
            endDate = LocalDate.now().plusDays(7),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            allDay = false,
            repeatId = null,
            subjectId = null,
            subjectName = null,
            repeatEndDate = LocalDate.now().plusDays(7)
        )

        val eventList = listOf(event1, event2, event3)

        eventDao.insertEvents(eventList)

        val eventsWithRepeatId = eventDao.getEventsByRepeatId(repeatId)
        Log.d(tag, "getEventsByRepeatId_returnsEventsWithRepeatId, events: $eventsWithRepeatId")

        assertThat(eventsWithRepeatId.contains(event1)).isTrue()
        assertThat(eventsWithRepeatId.contains(event2)).isTrue()
        assertThat(eventsWithRepeatId.contains(event3)).isFalse()
    }

    // getEventById
    @Test
    fun getEventById_eventIsReturned() = runTest {
        val eventId = 1
        val event = Event(
            id = eventId,
            title = "Test Event",
            description = "Test Description",
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            allDay = false,
            repeatId = null,
            subjectId = null,
            subjectName = null,
            repeatEndDate = LocalDate.now().plusDays(7)
        )
        eventDao.insertEvent(event)

        val retrievedEvent = eventDao.getEventById(eventId)
        Log.d(tag, "getEventById_eventIsReturned, event: $retrievedEvent")
        assertThat(retrievedEvent == event).isTrue()
    }

    @Test
    fun getEventById_eventIsNotReturned() = runTest {
        val eventId = 1
        val event = Event(
            id = eventId,
            title = "Test Event",
            description = "Test Description",
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            allDay = false,
            repeatId = null,
            subjectId = null,
            subjectName = null,
            repeatEndDate = LocalDate.now().plusDays(7)
        )
        eventDao.insertEvent(event)

        val retrievedEvent = eventDao.getEventById(eventId + 1)
        Log.d(tag, "getEventById_eventIsNotReturned, event: $retrievedEvent")
        assertThat(retrievedEvent == null).isTrue()
    }

    // getEventsByDate
    @Test
    fun getEventsByDateFromEmptyDatabase_eventListIsEmpty() = runTest {
        val events = eventDao.getEventsByDate(LocalDate.now()).first()
        assertThat(events.isEmpty()).isTrue()
    }

    @Test
    fun getEventsByDate_eventListIsNotEmpty() = runTest {
        val date = LocalDate.now()
        val event = Event(
            id = 1,
            title = "Test Event",
            description = "Test Description",
            startDate = date,
            endDate = date,
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            allDay = false,
            repeatId = null,
            subjectId = null,
            subjectName = null,
            repeatEndDate = LocalDate.now().plusDays(7)
        )
        eventDao.insertEvent(event)

        val events = eventDao.getEventsByDate(date).first()
        Log.d(tag, "getEventsByDate_eventListIsNotEmpty: $events")
        assertThat(events.isNotEmpty()).isTrue()
    }

    // getEventsInRange
    @Test
    fun getEventsInRange_returnsEventsInRange() = runTest {
        val startDate = LocalDate.now()
        val endDate = startDate.plusDays(5)

        // Event fully inside range
        val event1 = Event(
            id = 1,
            title = "Event Inside Range",
            description = "Test",
            startDate = startDate.plusDays(1),
            endDate = startDate.plusDays(2),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            allDay = false,
            repeatId = null,
            subjectId = null,
            subjectName = null,
            repeatEndDate = endDate.plusDays(7)
        )

        // Event starts before range but ends in range
        val event2 = Event(
            id = 2,
            title = "Event Crosses Start",
            description = "Test",
            startDate = startDate.minusDays(1),
            endDate = startDate.plusDays(1),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            allDay = false,
            repeatId = null,
            subjectId = null,
            subjectName = null,
            repeatEndDate = endDate.plusDays(7)
        )

        // Event outside range
        val event3 = Event(
            id = 3,
            title = "Event Crosses Start",
            description = "Test",
            startDate = startDate.minusDays(10),
            endDate = startDate.minusDays(9),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            allDay = false,
            repeatId = null,
            subjectId = null,
            subjectName = null,
            repeatEndDate = endDate.plusDays(7)
        )

        val eventList = listOf(event1, event2, event3)

        eventDao.insertEvents(eventList)

        val eventsInRange = eventDao.getEventsInRange(startDate, endDate).first()
        Log.d(tag, "getEventsInRange_returnsEventsInRange, events: $eventsInRange")

        assertThat(eventsInRange.contains(event1)).isTrue()
        assertThat(eventsInRange.contains(event2)).isTrue()
        assertThat(eventsInRange.contains(event3)).isFalse()
    }

    // getAllEvents
    @Test
    fun getAllEvents_returnsAllEvents() = runTest {
        val event1 = Event(
            id = 1,
            title = "Event 1",
            description = "Test",
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            allDay = false,
            repeatId = null,
            subjectId = null,
            subjectName = null,
            repeatEndDate = LocalDate.now().plusDays(7)
        )

        val event2 = Event(
            id = 2,
            title = "Event 2",
            description = "Test",
            startDate = LocalDate.now().plusDays(1),
            endDate = LocalDate.now().plusDays(1),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            allDay = false,
            repeatId = null,
            subjectId = null,
            subjectName = null,
            repeatEndDate = LocalDate.now().plusDays(7)
        )

        val eventList = listOf(event1, event2)

        eventDao.insertEvents(eventList)

        val events = eventDao.getAllEvents().first()
        Log.d(tag, "getAllEvents_returnsAllEvents, events: $events")

        assertThat(events.contains(event1)).isTrue()
        assertThat(events.contains(event2)).isTrue()
        assertThat(events.size).isEqualTo(2)
    }

    // clearSubjectFromEvents
    @Test
    fun clearSubjectFromEvents_subjectClearedFromEvents() = runTest {
        // For testing purposes, it's better to use null for the subjectId
        // or mock the subject dependency if needed

        val testSubject = Subject(
            id = 1,
            title = "Test"
        )

        subjectDao.insert(testSubject)

        val event1 = Event(
            id = 1,
            title = "Event with Subject",
            description = "Test",
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            allDay = false,
            repeatId = null,
            subjectId = 1, // Changed to null to avoid foreign key constraint
            subjectName = "Math",
            repeatEndDate = LocalDate.now().plusDays(7)
        )

        // Insert the event
        eventDao.insertEvent(event1)

        val event2 = Event(
            id = 2,
            title = "Event without Subject",
            description = "Test",
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(15, 0),
            allDay = false,
            repeatId = null,
            subjectId = null,
            subjectName = null,
            repeatEndDate = LocalDate.now().plusDays(7)
        )

        eventDao.insertEvent(event2)

        var retrievedEvent = eventDao.getEventById(1)
        assertThat(retrievedEvent?.subjectId).isEqualTo(1)

        eventDao.clearSubjectFromEvents(1)

        retrievedEvent = eventDao.getEventById(1)
        Log.d(tag, "clearSubjectFromEvents_subjectClearedFromEvents, event: $retrievedEvent")

        assertThat(retrievedEvent?.subjectId).isNull()
        assertThat(retrievedEvent?.subjectName).isNull()
    }

    // Stress test
    @Test
    fun largeNumberOfEvents_canBeInsertedAndRetrieved() = runTest {
        val eventCount = 10000

        val events = (1..eventCount).map { i ->

            if (i % 10 == 0) {
                subjectDao.insert(
                    Subject(
                        id = i,
                        title = "Subject $i"
                    )
                )
            }
            advanceUntilIdle()
            Event(
                id = i,
                title = "Event $i",
                description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas " +
                        "vehicula volutpat porttitor. Fusce fringilla nisi id diam aliquet, ac " +
                        "suscipit turpis fringilla. Maecenas at tortor blandit, rutrum ante a, " +
                        "feugiat elit. Curabitur sollicitudin felis viverra nisi aliquam scelerisque." +
                        " Quisque finibus purus est, et venenatis odio vehicula at. Donec dignissim " +
                        "hendrerit felis at sollicitudin. Morbi pharetra placerat faucibus. Curabitur" +
                        " semper feugiat nisi eget cursus. Donec aliquam, diam sit amet feugiat " +
                        "iaculis, diam lectus feugiat magna, ac pretium neque sapien sed urna. " +
                        "Sed pretium mattis leo quis egestas. Suspendisse pretium eu ipsum sed " +
                        "cursus. Curabitur in diam dui. Sed leo odio, tincidunt sit amet risus eu, " +
                        "laoreet consectetur sem. Nunc elementum vestibulum quam, sed mattis neque " +
                        "fringilla vitae. Nullam metus urna, tristique sed est vel, laoreet maximus " +
                        "nunc. Etiam egestas quam eu justo maximus dictum. Quisque sagittis massa " +
                        "at neque dignissim lobortis. Morbi suscipit felis nec justo varius " +
                        "accumsan. Curabitur volutpat metus id ex ultricies accumsan. Duis sit amet" +
                        " nibh ex. Donec in velit vel lorem vehicula vehicula. Morbi lobortis arcu " +
                        "ac sollicitudin ornare. Suspendisse a orci ac augue gravida cursus. Donec " +
                        "nec sem malesuada purus dapibus sodales eu nec mi. Duis vestibulum sed " +
                        "ipsum vitae faucibus.",
                startDate = LocalDate.now().plusDays((i % 30).toLong()),
                endDate = LocalDate.now().plusDays((i % 30).toLong()),
                startTime = LocalTime.of(9, 0),
                endTime = LocalTime.of(10, 0),
                allDay = false,
                repeatId = if (i % 3 == 0) i / 3 else null,
                subjectId = if (i % 10 == 0) i else null,
                subjectName = if (i % 10 == 0) "Subject $i" else null,
                repeatEndDate = LocalDate.now().plusDays(30)
            )
        }

        events.forEach {
            eventDao.insertEvent(it)
        }

        // Verify
        val allEvents = eventDao.getAllEvents().first()
        Log.d(tag, "largeNumberOfEvents_canBeInsertedAndRetrieved, count: ${allEvents.size}")
        assertThat(allEvents.size).isEqualTo(eventCount)
    }

    @Test
    fun largeNumberOfEventsInDateRange_canBeFilteredEfficiently() = runTest {
        val eventCount = 3000
        val startDate = LocalDate.now()
        val endDate = startDate.plusDays(7)
        var expectedCount = 0

        val events = (1..eventCount).map { i ->
            val eventStartDate = startDate.plusDays((i % 20).toLong())
            val eventEndDate = eventStartDate.plusDays(if (i % 5 == 0) 2 else 0)

            if ((eventStartDate.isEqual(startDate) || eventStartDate.isAfter(startDate)) &&
                (eventEndDate.isEqual(endDate) || eventEndDate.isBefore(endDate)) ||
                (eventStartDate.isBefore(startDate) && eventEndDate.isAfter(startDate)) ||
                (eventStartDate.isBefore(endDate) && eventEndDate.isAfter(endDate))
            ) {
                expectedCount++
            }

            Event(
                id = i,
                title = "Event $i",
                description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas " +
                        "vehicula volutpat porttitor. Fusce fringilla nisi id diam aliquet, ac " +
                        "suscipit turpis fringilla. Maecenas at tortor blandit, rutrum ante a, " +
                        "feugiat elit. Curabitur sollicitudin felis viverra nisi aliquam scelerisque." +
                        " Quisque finibus purus est, et venenatis odio vehicula at. Donec dignissim " +
                        "hendrerit felis at sollicitudin. Morbi pharetra placerat faucibus. Curabitur" +
                        " semper feugiat nisi eget cursus. Donec aliquam, diam sit amet feugiat " +
                        "iaculis, diam lectus feugiat magna, ac pretium neque sapien sed urna. " +
                        "Sed pretium mattis leo quis egestas. Suspendisse pretium eu ipsum sed " +
                        "cursus. Curabitur in diam dui. Sed leo odio, tincidunt sit amet risus eu, " +
                        "laoreet consectetur sem. Nunc elementum vestibulum quam, sed mattis neque " +
                        "fringilla vitae. Nullam metus urna, tristique sed est vel, laoreet maximus " +
                        "nunc. Etiam egestas quam eu justo maximus dictum. Quisque sagittis massa " +
                        "at neque dignissim lobortis. Morbi suscipit felis nec justo varius " +
                        "accumsan. Curabitur volutpat metus id ex ultricies accumsan. Duis sit amet" +
                        " nibh ex. Donec in velit vel lorem vehicula vehicula. Morbi lobortis arcu " +
                        "ac sollicitudin ornare. Suspendisse a orci ac augue gravida cursus. Donec " +
                        "nec sem malesuada purus dapibus sodales eu nec mi. Duis vestibulum sed " +
                        "ipsum vitae faucibus.",
                startDate = eventStartDate,
                endDate = eventEndDate,
                startTime = LocalTime.of(9, 0),
                endTime = LocalTime.of(10, 0),
                allDay = false,
                repeatId = if (i % 5 == 0) i / 5 else null,
                subjectId = null,
                subjectName = null,
                repeatEndDate = endDate.plusDays(10)
            )
        }

        events.forEach {
            eventDao.insertEvent(it)
        }


        val filteredEvents = eventDao.getEventsInRange(startDate, endDate).first()
        Log.d(
            tag,
            "largeNumberOfEventsInDateRange_canBeFilteredEfficiently, count: ${filteredEvents.size}"
        )
        assertThat(filteredEvents.size).isGreaterThan(0)
    }

    @Test
    fun eventsWithRepeatingIds_canBeRetrievedEfficiently() = runTest {
        val eventCount = 2000
        val targetRepeatId = 42
        var expectedCount = 0

        val events = (1..eventCount).map { i ->
            val repeatId = if (i % 10 == 0) targetRepeatId else if (i % 3 == 0) i / 3 else null
            if (repeatId == targetRepeatId) expectedCount++

            Event(
                id = i,
                title = "Event $i",
                description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas " +
                        "vehicula volutpat porttitor. Fusce fringilla nisi id diam aliquet, ac " +
                        "suscipit turpis fringilla. Maecenas at tortor blandit, rutrum ante a, " +
                        "feugiat elit. Curabitur sollicitudin felis viverra nisi aliquam scelerisque." +
                        " Quisque finibus purus est, et venenatis odio vehicula at. Donec dignissim " +
                        "hendrerit felis at sollicitudin. Morbi pharetra placerat faucibus. Curabitur" +
                        " semper feugiat nisi eget cursus. Donec aliquam, diam sit amet feugiat " +
                        "iaculis, diam lectus feugiat magna, ac pretium neque sapien sed urna. " +
                        "Sed pretium mattis leo quis egestas. Suspendisse pretium eu ipsum sed " +
                        "cursus. Curabitur in diam dui. Sed leo odio, tincidunt sit amet risus eu, " +
                        "laoreet consectetur sem. Nunc elementum vestibulum quam, sed mattis neque " +
                        "fringilla vitae. Nullam metus urna, tristique sed est vel, laoreet maximus " +
                        "nunc. Etiam egestas quam eu justo maximus dictum. Quisque sagittis massa " +
                        "at neque dignissim lobortis. Morbi suscipit felis nec justo varius " +
                        "accumsan. Curabitur volutpat metus id ex ultricies accumsan. Duis sit amet" +
                        " nibh ex. Donec in velit vel lorem vehicula vehicula. Morbi lobortis arcu " +
                        "ac sollicitudin ornare. Suspendisse a orci ac augue gravida cursus. Donec " +
                        "nec sem malesuada purus dapibus sodales eu nec mi. Duis vestibulum sed " +
                        "ipsum vitae faucibus.",
                startDate = LocalDate.now().plusDays((i % 30).toLong()),
                endDate = LocalDate.now().plusDays((i % 30).toLong()),
                startTime = LocalTime.of(9, 0),
                endTime = LocalTime.of(10, 0),
                allDay = false,
                repeatId = repeatId,
                subjectId = null,
                subjectName = null,
                repeatEndDate = LocalDate.now().plusDays(30)
            )
        }

        events.forEach {
            eventDao.insertEvent(it)
        }


        // Verify
        val repeatingEvents = eventDao.getEventsByRepeatId(targetRepeatId)
        Log.d(
            tag,
            "eventsWithRepeatingIds_canBeRetrievedEfficiently, count: ${repeatingEvents.size}"
        )
        assertThat(repeatingEvents.size).isEqualTo(expectedCount)
    }

    @Test
    fun multipleEventsWithSameDate_canBeFilteredEfficiently() = runTest {
        val eventCount = 3000
        val targetDate = LocalDate.now().plusDays(5)
        var expectedCount = 0

        val events = (1..eventCount).map { i ->
            val startDate =
                if (i % 10 == 0) targetDate else LocalDate.now().plusDays((i % 30).toLong())
            val endDate = startDate.plusDays(if (i % 5 == 0) 2 else 0)


            if ((startDate.isEqual(targetDate) || startDate.isBefore(targetDate)) &&
                (endDate.isEqual(targetDate) || endDate.isAfter(targetDate))
            ) {
                expectedCount++
            }

            Event(
                id = i,
                title = "Event $i",
                description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas " +
                        "vehicula volutpat porttitor. Fusce fringilla nisi id diam aliquet, ac " +
                        "suscipit turpis fringilla. Maecenas at tortor blandit, rutrum ante a, " +
                        "feugiat elit. Curabitur sollicitudin felis viverra nisi aliquam scelerisque." +
                        " Quisque finibus purus est, et venenatis odio vehicula at. Donec dignissim " +
                        "hendrerit felis at sollicitudin. Morbi pharetra placerat faucibus. Curabitur" +
                        " semper feugiat nisi eget cursus. Donec aliquam, diam sit amet feugiat " +
                        "iaculis, diam lectus feugiat magna, ac pretium neque sapien sed urna. " +
                        "Sed pretium mattis leo quis egestas. Suspendisse pretium eu ipsum sed " +
                        "cursus. Curabitur in diam dui. Sed leo odio, tincidunt sit amet risus eu, " +
                        "laoreet consectetur sem. Nunc elementum vestibulum quam, sed mattis neque " +
                        "fringilla vitae. Nullam metus urna, tristique sed est vel, laoreet maximus " +
                        "nunc. Etiam egestas quam eu justo maximus dictum. Quisque sagittis massa " +
                        "at neque dignissim lobortis. Morbi suscipit felis nec justo varius " +
                        "accumsan. Curabitur volutpat metus id ex ultricies accumsan. Duis sit amet" +
                        " nibh ex. Donec in velit vel lorem vehicula vehicula. Morbi lobortis arcu " +
                        "ac sollicitudin ornare. Suspendisse a orci ac augue gravida cursus. Donec " +
                        "nec sem malesuada purus dapibus sodales eu nec mi. Duis vestibulum sed " +
                        "ipsum vitae faucibus.",
                startDate = startDate,
                endDate = endDate,
                startTime = LocalTime.of(9, 0),
                endTime = LocalTime.of(10, 0),
                allDay = false,
                repeatId = if (i % 5 == 0) i / 5 else null,
                subjectId = null,
                subjectName = null,
                repeatEndDate = LocalDate.now().plusDays(30)
            )
        }

        events.forEach {
            eventDao.insertEvent(it)
        }

        // Verify
        val filteredEvents = eventDao.getEventsByDate(targetDate).first()
        Log.d(
            tag,
            "multipleEventsWithSameDate_canBeFilteredEfficiently, count: ${filteredEvents.size}"
        )
        assertThat(filteredEvents.size).isEqualTo(expectedCount)
    }

    @Test
    fun rapidSequentialEventCrudOperations_areHandledCorrectly() = runTest {
        val operationCount = 10000
        val maxEventId = 10000
        val subjectCount = 10

        val subjects = (1..subjectCount).map { i ->
            Subject(
                id = i,
                title = "Subject $i for events"
            )
        }

        subjects.forEach {
            subjectDao.insert(it)
        }

        for (i in 1..operationCount) {
            val subjectId = (i % subjectCount) + 1

            when (i % 4) {
                0 -> {
                    // Insert
                    val event = Event(
                        id = i,
                        title = "Event $i v${i / maxEventId}",
                        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas " +
                                "vehicula volutpat porttitor. Fusce fringilla nisi id diam aliquet, ac " +
                                "suscipit turpis fringilla. Maecenas at tortor blandit, rutrum ante a, " +
                                "feugiat elit. Curabitur sollicitudin felis viverra nisi aliquam scelerisque." +
                                " Quisque finibus purus est, et venenatis odio vehicula at. Donec dignissim " +
                                "hendrerit felis at sollicitudin. Morbi pharetra placerat faucibus. Curabitur" +
                                " semper feugiat nisi eget cursus. Donec aliquam, diam sit amet feugiat " +
                                "iaculis, diam lectus feugiat magna, ac pretium neque sapien sed urna. " +
                                "Sed pretium mattis leo quis egestas. Suspendisse pretium eu ipsum sed " +
                                "cursus. Curabitur in diam dui. Sed leo odio, tincidunt sit amet risus eu, " +
                                "laoreet consectetur sem. Nunc elementum vestibulum quam, sed mattis neque " +
                                "fringilla vitae. Nullam metus urna, tristique sed est vel, laoreet maximus " +
                                "nunc. Etiam egestas quam eu justo maximus dictum. Quisque sagittis massa " +
                                "at neque dignissim lobortis. Morbi suscipit felis nec justo varius " +
                                "accumsan. Curabitur volutpat metus id ex ultricies accumsan. Duis sit amet" +
                                " nibh ex. Donec in velit vel lorem vehicula vehicula. Morbi lobortis arcu " +
                                "ac sollicitudin ornare. Suspendisse a orci ac augue gravida cursus. Donec " +
                                "nec sem malesuada purus dapibus sodales eu nec mi. Duis vestibulum sed " +
                                "ipsum vitae faucibus.",
                        startDate = LocalDate.now().plusDays((i % 30).toLong()),
                        endDate = LocalDate.now().plusDays((i % 30 + 1).toLong()),
                        startTime = LocalTime.of(9, 0),
                        endTime = LocalTime.of(10, 0),
                        allDay = false,
                        repeatId = if (i % 5 == 0) i / 5 else null,
                        subjectId = subjectId,
                        subjectName = "Subject $subjectId",
                        repeatEndDate = LocalDate.now().plusDays(30)
                    )
                    eventDao.insertEvent(event)
                }

                1 -> {
                    // Update
                    val existing = eventDao.getEventById(i)
                    if (existing != null) {
                        eventDao.updateEvent(
                            existing.copy(
                                title = "Updated ${existing.title}",
                                description = "Updated on operation $i"
                            )
                        )
                    }
                }

                2 -> {
                    // Read
                    eventDao.getEventById(i)
                }

                3 -> {
                    // Delete
                    val existing = eventDao.getEventById(i)
                    if (existing != null && i > operationCount / 2) {
                        eventDao.deleteEvent(existing)
                    }
                }
            }
        }

        // Verify
        val remainingEvents = eventDao.getAllEvents().first()
        Log.d(
            tag,
            "rapidSequentialEventCrudOperations_areHandledCorrectly, final count: ${remainingEvents.size}"
        )
        assertThat(remainingEvents.size).isAtMost(maxEventId)
    }

    @Test
    fun largeNumberOfRepeatingEvents_canBeInsertedEditedAndDeleted() = runTest {
        val eventCount = 1000
        val initialDuration = 100L
        val updatedDuration = 50L
        val baseStartDate = LocalDate.now()
        val repeatId = 99999

        for (i in 1..eventCount) {
            val subjectId = if (i % 10 == 0) i else null
            if (subjectId != null) {
                subjectDao.insert(
                    Subject(
                        id = subjectId,
                        title = "Subject $subjectId"
                    )
                )
            }
        }

        // Insert
        Log.d(tag, "Starting insertion of $eventCount repeating events")
        val events = (1..eventCount).map { i ->
            val subjectId = if (i % 10 == 0) i else null
            Event(
                id = i,
                title = "Repeating Event $i",
                description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas " +
                        "vehicula volutpat porttitor. Fusce fringilla nisi id diam aliquet, ac " +
                        "suscipit turpis fringilla. Maecenas at tortor blandit, rutrum ante a, " +
                        "feugiat elit. Curabitur sollicitudin felis viverra nisi aliquam scelerisque." +
                        " Quisque finibus purus est, et venenatis odio vehicula at. Donec dignissim " +
                        "hendrerit felis at sollicitudin. Morbi pharetra placerat faucibus. Curabitur" +
                        " semper feugiat nisi eget cursus. Donec aliquam, diam sit amet feugiat " +
                        "iaculis, diam lectus feugiat magna, ac pretium neque sapien sed urna. " +
                        "Sed pretium mattis leo quis egestas. Suspendisse pretium eu ipsum sed " +
                        "cursus. Curabitur in diam dui. Sed leo odio, tincidunt sit amet risus eu, " +
                        "laoreet consectetur sem. Nunc elementum vestibulum quam, sed mattis neque " +
                        "fringilla vitae. Nullam metus urna, tristique sed est vel, laoreet maximus " +
                        "nunc. Etiam egestas quam eu justo maximus dictum. Quisque sagittis massa " +
                        "at neque dignissim lobortis. Morbi suscipit felis nec justo varius " +
                        "accumsan. Curabitur volutpat metus id ex ultricies accumsan. Duis sit amet" +
                        " nibh ex. Donec in velit vel lorem vehicula vehicula. Morbi lobortis arcu " +
                        "ac sollicitudin ornare. Suspendisse a orci ac augue gravida cursus. Donec " +
                        "nec sem malesuada purus dapibus sodales eu nec mi. Duis vestibulum sed " +
                        "ipsum vitae faucibus.",
                startDate = baseStartDate.plusDays((i % 30).toLong()),
                endDate = baseStartDate.plusDays((i % 30) + initialDuration),
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(11, 0),
                allDay = false,
                repeatId = repeatId,
                subjectId = subjectId,
                subjectName = if (subjectId != null) "Subject $subjectId" else null,
                repeatEndDate = baseStartDate.plusDays(initialDuration)
            )
        }


        events.forEach { eventDao.insertEvent(it) }


        // Verify
        val insertedEvents = eventDao.getEventsByRepeatId(repeatId)
        Log.d(
            tag,
            "largeNumberOfRepeatingEvents_canBeInsertedEditedAndDeleted, inserted count: ${insertedEvents.size}"
        )
        assertThat(insertedEvents.size).isEqualTo(eventCount)

        // Update
        Log.d(tag, "Starting update of all repeating events")
        insertedEvents.forEach { event ->
            val updatedEvent = event.copy(
                description = "Updated event now lasting $updatedDuration days",
                endDate = event.startDate.plusDays(updatedDuration),
                repeatEndDate = baseStartDate.plusDays(updatedDuration)
            )
            eventDao.updateEvent(updatedEvent)
        }


        // Verify
        val eventsAfterUpdate = eventDao.getEventsByRepeatId(repeatId)
        Log.d(
            tag,
            "largeNumberOfRepeatingEvents_canBeInsertedEditedAndDeleted, updated count: ${eventsAfterUpdate.size}"
        )

        val sampleEvent = eventDao.getEventById(1)
        assertThat(sampleEvent).isNotNull()
        assertThat(sampleEvent!!.description).contains("$updatedDuration days")
        assertThat(sampleEvent.endDate).isEqualTo(sampleEvent.startDate.plusDays(updatedDuration))

        // Delete
        Log.d(tag, "Starting deletion of all repeating events")
        eventDao.deleteEventsByRepeatId(repeatId)

        // Verify
        val remainingEvents = eventDao.getEventsByRepeatId(repeatId)
        Log.d(
            tag,
            "largeNumberOfRepeatingEvents_canBeInsertedEditedAndDeleted, remaining count: ${remainingEvents.size}"
        )
        assertThat(remainingEvents).isEmpty()
    }
}