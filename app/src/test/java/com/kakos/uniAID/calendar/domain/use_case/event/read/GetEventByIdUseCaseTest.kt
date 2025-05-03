package com.kakos.uniAID.calendar.domain.use_case.event.read

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.kakos.uniAID.calendar.data.repository.FakeEventRepository
import com.kakos.uniAID.calendar.domain.model.Event
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

class GetEventByIdUseCaseTest {
    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var fakeEventRepository: FakeEventRepository
    private lateinit var getEventByIdUseCase: GetEventByIdUseCase

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        fakeEventRepository = FakeEventRepository()
        getEventByIdUseCase = GetEventByIdUseCase(fakeEventRepository)
        fakeEventRepository.shouldHaveFilledList(true)
    }

    @Test
    fun `get event with valid id returns matching event, event properties match expected values`() =
        runTest {
            val expectedEvent = fakeEventRepository.getAllEvents().first()[0]
            val eventId = expectedEvent.id!!

            val result = getEventByIdUseCase(eventId)

            assertThat(result).isEqualTo(expectedEvent)
            assertThat(result?.id).isEqualTo(eventId)
        }

    @Test
    fun `get event with non-existing id returns null, no event is found`() = runTest {
        val nonExistingId = 999

        val result = getEventByIdUseCase(nonExistingId)

        assertThat(result).isNull()
    }

    @Test
    fun `get event with zero id returns null, invalid id produces null result`() = runTest {
        val invalidId = 0

        val result = getEventByIdUseCase(invalidId)

        assertThat(result).isNull()
    }

    @Test
    fun `get event with negative id returns null, negative id produces null result`() = runTest {
        val negativeId = -5

        val result = getEventByIdUseCase(negativeId)

        assertThat(result).isNull()
    }

    @Test
    fun `get event after adding new event returns the newly added event, new event is retrievable`() =
        runTest {
            val newEvent = Event(
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
                repeatEndDate = LocalDate.now().plusDays(5),
                repeatDays = emptyList(),
                allDay = false,
                subjectId = null,
                subjectName = null
            )

            fakeEventRepository.insertEvent(newEvent)

            val result = getEventByIdUseCase(newEvent.id!!)

            assertThat(result).isEqualTo(newEvent)
        }

    @Test
    fun `get event after deleting it returns null, deleted event is no longer retrievable`() =
        runTest {
            val eventToDelete = fakeEventRepository.getAllEvents().first()[0]
            val eventId = eventToDelete.id!!

            fakeEventRepository.deleteEvent(eventToDelete)

            val result = getEventByIdUseCase(eventId)

            assertThat(result).isNull()
        }

    @Test
    fun `get event after updating it returns updated event, updated properties are reflected`() =
        runTest {
            val originalEvent = fakeEventRepository.getAllEvents().first()[0]
            val eventId = originalEvent.id!!

            val updatedEvent = originalEvent.copy(
                title = "Updated Title",
                description = "Updated Description",
                color = 5
            )

            fakeEventRepository.updateEvent(updatedEvent)

            val result = getEventByIdUseCase(eventId)

            assertThat(result).isEqualTo(updatedEvent)
            assertThat(result?.title).isEqualTo("Updated Title")
            assertThat(result?.description).isEqualTo("Updated Description")
            assertThat(result?.color).isEqualTo(5)
        }
}