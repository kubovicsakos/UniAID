package com.kakos.uniAID.calendar.domain.use_case.event.create

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.kakos.uniAID.calendar.data.repository.FakeEventRepository
import com.kakos.uniAID.calendar.domain.model.Event
import com.kakos.uniAID.calendar.domain.model.InvalidEventException
import com.kakos.uniAID.calendar.domain.model.Repeat
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class AddEventUseCaseTest {
    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var fakeEventRepository: FakeEventRepository
    private lateinit var addEventUseCase: AddEventUseCase

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        fakeEventRepository = FakeEventRepository()
        addEventUseCase = AddEventUseCase(fakeEventRepository)
        fakeEventRepository.shouldHaveFilledList(true)
    }

    @Test
    fun `valid non-repeating event is added to repository successfully, event is in repository`() =
        runTest {
            val validEvent = Event(
                id = 4,
                title = "Test Event",
                description = "Test Description",
                color = 3,
                location = "Test Location",
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(11, 0),
                repeatId = null,
                repeat = Repeat.NONE,
                repeatDifference = 1,
                repeatEndDate = LocalDate.now().plusWeeks(1),
                repeatDays = emptyList(),
                allDay = false,
                subjectId = null,
                subjectName = null
            )

            addEventUseCase(validEvent)

            val allEvents = fakeEventRepository.getAllEvents().first()
            assertThat(allEvents).contains(validEvent)
            assertThat(allEvents.size).isEqualTo(4)
        }

    @Test
    fun `valid daily repeating event generates multiple events, more than one event added`() =
        runTest {
            val validRepeatingEvent = Event(
                id = null,
                title = "Daily Repeating Event",
                description = "Test Description",
                color = 3,
                location = "Test Location",
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(11, 0),
                repeatId = null,
                repeat = Repeat.DAILY,
                repeatDifference = 1,
                repeatEndDate = LocalDate.now().plusDays(3),
                repeatDays = emptyList(),
                allDay = false,
                subjectId = null,
                subjectName = null
            )

            val initialEvents = fakeEventRepository.getAllEvents().first().size
            println("Initial events: $initialEvents")

            addEventUseCase(validRepeatingEvent)

            val allEvents = fakeEventRepository.getAllEvents().first()
            // Expect at least the original events plus some generated ones
            assertThat(allEvents.size).isGreaterThan(initialEvents)
            println("All events:")
            for (event in allEvents) {
                println(event)
            }

            // The original event with given ID shouldn't exist as it's replaced by generated events
            assertThat(allEvents.none { it.id == validRepeatingEvent.id }).isTrue()

            // Check if there are events with the same title but different dates
            val repeatingEvents = allEvents.filter { it.title == validRepeatingEvent.title }
            assertThat(repeatingEvents.size).isGreaterThan(1)
        }

    @Test
    fun `valid weekly repeating event with specific days generates events for those days only`() =
        runTest {
            val today = LocalDate.now()
            val validRepeatingEvent = Event(
                id = null,
                title = "Weekly Repeating Event",
                description = "Test Description",
                color = 3,
                location = "Test Location",
                startDate = today,
                endDate = today,
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(11, 0),
                repeatId = null,
                repeat = Repeat.WEEKLY,
                repeatDifference = 1,
                repeatEndDate = today.plusWeeks(2),
                repeatDays = listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY),
                allDay = false,
                subjectId = null,
                subjectName = null
            )

            addEventUseCase(validRepeatingEvent)

            val allEvents = fakeEventRepository.getAllEvents().first()
            println("All events:")
            for (event in allEvents) {
                println(event)
            }
            val repeatingEvents = allEvents.filter { it.title == validRepeatingEvent.title }

            // Check that only events for the specified days were created
            val daysOfWeek = repeatingEvents.map { it.startDate.dayOfWeek }.distinct()
            assertThat(daysOfWeek).containsExactly(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY)
        }

    @Test
    fun `monthly repeating event generates correctly spaced events`() = runTest {
        val today = LocalDate.now()
        val validRepeatingEvent = Event(
            id = null,
            title = "Monthly Repeating Event",
            description = "Test Description",
            color = 3,
            location = "Test Location",
            startDate = today,
            endDate = today,
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(11, 0),
            repeatId = null,
            repeat = Repeat.MONTHLY,
            repeatDifference = 1,
            repeatEndDate = today.plusMonths(3),
            repeatDays = emptyList(),
            allDay = false,
            subjectId = null,
            subjectName = null
        )

        addEventUseCase(validRepeatingEvent)

        val allEvents = fakeEventRepository.getAllEvents().first()
        println("All events:")
        for (event in allEvents) {
            println(event)
        }
        val repeatingEvents = allEvents.filter { it.title == validRepeatingEvent.title }

        // Should have at least 4 events (original + 3 months)
        assertThat(repeatingEvents.size).isAtLeast(3)

        // Events should have same day of month
        val daysOfMonth = repeatingEvents.map { it.startDate.dayOfMonth }.distinct()
        assertThat(daysOfMonth.size).isEqualTo(1)
        assertThat(daysOfMonth[0]).isEqualTo(today.dayOfMonth)
    }

    @Test
    fun `repeating event with different repeatDifference respects that value`() = runTest {
        val today = LocalDate.now()
        val validRepeatingEvent = Event(
            id = null,
            title = "Bi-Weekly Event",
            description = "Test Description",
            color = 3,
            location = "Test Location",
            startDate = today,
            endDate = today,
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(11, 0),
            repeatId = null,
            repeat = Repeat.WEEKLY,
            repeatDifference = 2, // Every 2 weeks
            repeatEndDate = today.plusWeeks(6),
            repeatDays = listOf(DayOfWeek.MONDAY),
            allDay = false,
            subjectId = null,
            subjectName = null
        )

        addEventUseCase(validRepeatingEvent)

        val allEvents = fakeEventRepository.getAllEvents().first()
        println("All events:")
        for (event in allEvents) {
            println(event)
        }
        val repeatingEvents = allEvents.filter { it.title == validRepeatingEvent.title }

        // Extract the dates to verify they're two weeks apart
        val dates = repeatingEvents.map { it.startDate }.sorted()

        for (i in 0 until dates.size - 1) {
            // Each date should be 14 days apart (2 weeks)
            assertThat(dates[i].plusDays(14)).isEqualTo(dates[i + 1])
        }
    }

    @Test
    fun `yearly repeating event generates events on same day each year`() = runTest {
        val today = LocalDate.now()
        val validRepeatingEvent = Event(
            id = null,
            title = "Yearly Event",
            description = "Test Description",
            color = 3,
            location = "Test Location",
            startDate = today,
            endDate = today,
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(11, 0),
            repeatId = null,
            repeat = Repeat.YEARLY,
            repeatDifference = 1,
            repeatEndDate = today.plusYears(3),
            repeatDays = emptyList(),
            allDay = false,
            subjectId = null,
            subjectName = null
        )

        addEventUseCase(validRepeatingEvent)

        val allEvents = fakeEventRepository.getAllEvents().first()
        println("All events:")
        for (event in allEvents) {
            println(event)
        }
        val repeatingEvents = allEvents.filter { it.title == validRepeatingEvent.title }

        // Should have at least 3 events (today + 3 years)
        assertThat(repeatingEvents.size).isAtLeast(3)

        // All events should have same day and month
        val dayAndMonth =
            repeatingEvents.map { Pair(it.startDate.dayOfMonth, it.startDate.monthValue) }
                .distinct()
        assertThat(dayAndMonth.size).isEqualTo(1)
        assertThat(dayAndMonth[0].first).isEqualTo(today.dayOfMonth)
        assertThat(dayAndMonth[0].second).isEqualTo(today.monthValue)
    }

    @Test
    fun `event with null id gets assigned a new id, event added with new id`() = runTest {
        val eventWithNullId = Event(
            id = null,
            title = "Test Event",
            description = "Test Description",
            color = 3,
            location = "Test Location",
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(11, 0),
            repeatId = null,
            repeat = Repeat.NONE,
            repeatDifference = 1,
            repeatEndDate = LocalDate.now().plusWeeks(1),
            repeatDays = emptyList(),
            allDay = false,
            subjectId = null,
            subjectName = null
        )

        addEventUseCase(eventWithNullId)

        val allEvents = fakeEventRepository.getAllEvents().first()
        // The event should have been assigned an ID
        assertThat(allEvents.any { it.title == "Test Event" && it.id != null }).isTrue()
    }

    @Test
    fun `event with blank title throws InvalidEventException, exception message contains title`() =
        runTest {
            val invalidEvent = Event(
                id = null,
                title = "   ",  // Blank but not empty
                description = "Test Description",
                color = 3,
                location = "Test Location",
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(11, 0),
                repeatId = null,
                repeat = Repeat.NONE,
                repeatDifference = 1,
                repeatEndDate = LocalDate.now().plusWeeks(1),
                repeatDays = emptyList(),
                allDay = false,
                subjectId = null,
                subjectName = null
            )

            var exceptionThrown = false
            try {
                addEventUseCase(invalidEvent)
            } catch (e: InvalidEventException) {
                exceptionThrown = true
                assertThat(e.message).contains("title")
            }

            assertThat(exceptionThrown).isTrue()
        }

    @Test
    fun `event with empty title throws InvalidEventException, exception message contains title`() =
        runTest {
            val invalidEvent = Event(
                id = null,
                title = "",
                description = "Test Description",
                color = 3,
                location = "Test Location",
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(11, 0),
                repeatId = null,
                repeat = Repeat.NONE,
                repeatDifference = 1,
                repeatEndDate = LocalDate.now().plusWeeks(1),
                repeatDays = emptyList(),
                allDay = false,
                subjectId = null,
                subjectName = null
            )

            var exceptionThrown = false
            try {
                addEventUseCase(invalidEvent)
            } catch (e: InvalidEventException) {
                exceptionThrown = true
                assertThat(e.message).contains("title")
            }

            assertThat(exceptionThrown).isTrue()
        }

    @Test
    fun `weekly repeating event without repeat days throws InvalidEventException, exception message contains weekly`() =
        runTest {
            val invalidEvent = Event(
                id = null,
                title = "Test Event",
                description = "Test Description",
                color = 3,
                location = "Test Location",
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(11, 0),
                repeatId = null,
                repeat = Repeat.WEEKLY,
                repeatDifference = 1,
                repeatEndDate = LocalDate.now().plusWeeks(1),
                repeatDays = emptyList(),
                allDay = false,
                subjectId = null,
                subjectName = null
            )

            var exceptionThrown = false
            try {
                addEventUseCase(invalidEvent)
            } catch (e: InvalidEventException) {
                exceptionThrown = true
                assertThat(e.message).contains("weekly")
            }

            assertThat(exceptionThrown).isTrue()
        }

    @Test
    fun `event with start date after end date throws InvalidEventException, exception message contains date`() =
        runTest {
            val invalidEvent = Event(
                id = null,
                title = "Test Event",
                description = "Test Description",
                color = 3,
                location = "Test Location",
                startDate = LocalDate.now().plusDays(2),
                endDate = LocalDate.now(),
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(11, 0),
                repeatId = null,
                repeat = Repeat.NONE,
                repeatDifference = 1,
                repeatEndDate = LocalDate.now().plusWeeks(1),
                repeatDays = emptyList(),
                allDay = false,
                subjectId = null,
                subjectName = null
            )

            var exceptionThrown = false
            try {
                addEventUseCase(invalidEvent)
            } catch (e: InvalidEventException) {
                exceptionThrown = true
                assertThat(e.message).contains("date")
            }

            assertThat(exceptionThrown).isTrue()
        }
}