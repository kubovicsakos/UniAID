package com.kakos.uniAID.calendar.domain.use_case.event.update

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
import java.time.LocalDate
import java.time.LocalTime

class UpdateEventUseCaseTest {
    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var fakeEventRepository: FakeEventRepository
    private lateinit var updateEventUseCase: UpdateEventUseCase

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        fakeEventRepository = FakeEventRepository()
        updateEventUseCase = UpdateEventUseCase(fakeEventRepository)
        fakeEventRepository.shouldHaveFilledList(true)
    }

    @Test
    fun `update existing event changes properties in repository, updated event has new properties`() =
        runTest {
            val existingEvent = fakeEventRepository.getAllEvents().first()[0]
            val updatedEvent = existingEvent.copy(
                title = "Updated Title",
                description = "Updated Description",
                color = 5,
                location = "Updated Location"
            )

            updateEventUseCase(updatedEvent)

            val retrievedEvent = fakeEventRepository.getEventById(existingEvent.id!!)
            assertThat(retrievedEvent?.id).isEqualTo(updatedEvent.id)
            assertThat(retrievedEvent?.title).isEqualTo("Updated Title")
            assertThat(retrievedEvent?.description).isEqualTo("Updated Description")
            assertThat(retrievedEvent?.color).isEqualTo(5)
            assertThat(retrievedEvent?.location).isEqualTo("Updated Location")
        }

    @Test
    fun `update event with blank title throws InvalidEventException, exception message contains title`() =
        runTest {
            val existingEvent = fakeEventRepository.getAllEvents().first()[0]
            val invalidEvent = existingEvent.copy(title = "")

            var exceptionThrown = false
            try {
                updateEventUseCase(invalidEvent)
            } catch (e: InvalidEventException) {
                exceptionThrown = true
                assertThat(e.message).contains("title")
            }

            assertThat(exceptionThrown).isTrue()
        }

    @Test
    fun `update event with start date after end date throws InvalidEventException, exception message contains date`() =
        runTest {
            val existingEvent = fakeEventRepository.getAllEvents().first()[0]
            val invalidEvent = existingEvent.copy(
                startDate = LocalDate.now().plusDays(2),
                endDate = LocalDate.now()
            )

            var exceptionThrown = false
            try {
                updateEventUseCase(invalidEvent)
            } catch (e: InvalidEventException) {
                exceptionThrown = true
                assertThat(e.message).contains("date")
            }

            assertThat(exceptionThrown).isTrue()
        }

    @Test
    fun `update weekly repeating event without repeat days throws InvalidEventException, exception message contains weekly`() =
        runTest {
            val existingEvent = fakeEventRepository.getAllEvents().first()[0]
            val invalidEvent = existingEvent.copy(
                repeat = Repeat.WEEKLY,
                repeatDays = emptyList()
            )

            var exceptionThrown = false
            try {
                updateEventUseCase(invalidEvent)
            } catch (e: InvalidEventException) {
                exceptionThrown = true
                assertThat(e.message).contains("weekly")
            }

            assertThat(exceptionThrown).isTrue()
        }

    @Test
    fun `update event with different id changes correct event, only specified event is updated`() =
        runTest {
            val firstEvent = fakeEventRepository.getAllEvents().first()[0]
            val secondEvent = fakeEventRepository.getAllEvents().first()[1]

            val updatedEvent = firstEvent.copy(title = "First Event Updated")

            updateEventUseCase(updatedEvent)

            val retrievedFirstEvent = fakeEventRepository.getEventById(firstEvent.id!!)
            val retrievedSecondEvent = fakeEventRepository.getEventById(secondEvent.id!!)

            assertThat(retrievedFirstEvent?.title).isEqualTo("First Event Updated")
            assertThat(retrievedSecondEvent?.title).isEqualTo(secondEvent.title)
        }

    @Test
    fun `update event with null id throws InvalidEventException, exception message contains id`() =
        runTest {
            val existingEvent = fakeEventRepository.getAllEvents().first()[0]
            val invalidEvent = existingEvent.copy(id = null)

            var exceptionThrown = false
            try {
                updateEventUseCase(invalidEvent)
            } catch (e: InvalidEventException) {
                exceptionThrown = true
                assertThat(e.message).contains("ID")
            }

            assertThat(exceptionThrown).isTrue()
        }

    @Test
    fun `update non-existing event do not change anything, repository unchanged`() = runTest {
        val initialEvents = fakeEventRepository.getAllEvents().first()

        val nonExistingEvent = Event(
            id = 999,
            title = "Non-existing Event",
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

        updateEventUseCase(nonExistingEvent)

        val updatedEvents = fakeEventRepository.getAllEvents().first()
        assertThat(updatedEvents).isEqualTo(initialEvents)
    }

    @Test
    fun `update event with all values changed updates all properties correctly, all fields are updated`() =
        runTest {
            val existingEvent = fakeEventRepository.getAllEvents().first()[0]
            val updatedEvent = Event(
                id = existingEvent.id,
                title = "Completely Updated Title",
                description = "Completely Updated Description",
                color = 7,
                location = "Completely Updated Location",
                startDate = LocalDate.now().plusDays(5),
                endDate = LocalDate.now().plusDays(6),
                startTime = LocalTime.of(14, 30),
                endTime = LocalTime.of(16, 45),
                repeatId = existingEvent.repeatId,
                repeat = existingEvent.repeat,
                repeatDifference = existingEvent.repeatDifference,
                repeatEndDate = existingEvent.repeatEndDate,
                repeatDays = existingEvent.repeatDays,
                allDay = !existingEvent.allDay,
                subjectId = 100,
                subjectName = "Updated Subject"
            )

            updateEventUseCase(updatedEvent)

            val retrievedEvent = fakeEventRepository.getEventById(existingEvent.id!!)
            assertThat(retrievedEvent).isEqualTo(updatedEvent)
        }
}