package com.kakos.uniAID.core.features.subject.domain.use_case.read

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.kakos.uniAID.core.features.subject.data.repository.FakeSubjectRepository
import com.kakos.uniAID.core.domain.subject.domain.use_case.read.GetEventsOfSubjectUseCase
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetEventsOfSubjectUseCaseTest {
    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var fakeSubjectRepository: FakeSubjectRepository
    private lateinit var getEventsOfSubjectUseCase: GetEventsOfSubjectUseCase

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        fakeSubjectRepository = FakeSubjectRepository()
        getEventsOfSubjectUseCase = GetEventsOfSubjectUseCase(fakeSubjectRepository)
        fakeSubjectRepository.shouldHaveFilledList(true)
    }

    @Test
    fun `get events for existing subject with events returns correct events list, returns second event`() =
        runTest {
            fakeSubjectRepository.shouldHaveFilledEventList(true)
            val subjectId = 1 // Subject with events in the fake repository

            val events = getEventsOfSubjectUseCase(subjectId).first()

            assertThat(events).isNotEmpty()
            assertThat(events.size).isEqualTo(1)
            assertThat(events[0].title).isEqualTo("b")
            assertThat(events[0].subjectId).isEqualTo(subjectId)
        }

    @Test
    fun `get events for existing subject without events, returns empty list`() = runTest {
        fakeSubjectRepository.shouldHaveFilledEventList(true)
        val subjectId = 3 // Subject without events in the fake repository

        val events = getEventsOfSubjectUseCase(subjectId).first()

        assertThat(events).isEmpty()
    }

    @Test
    fun `get events for non-existent subject id, returns empty list`() = runTest {
        fakeSubjectRepository.shouldHaveFilledEventList(true)
        val nonExistentSubjectId = 999 // Non-existent subject ID

        val events = getEventsOfSubjectUseCase(nonExistentSubjectId).first()
        println("Events: $events")

        assertThat(events).isEmpty()
    }

    @Test
    fun `get events with zero subject id throws IllegalArgumentException, IllegalArgumentException`() =
        runTest {
            fakeSubjectRepository.shouldHaveFilledEventList(true)
            val invalidSubjectId = 0

            var exceptionThrown = false
            try {
                getEventsOfSubjectUseCase(invalidSubjectId).first()
            } catch (e: IllegalArgumentException) {
                exceptionThrown = true
                assertThat(e.message).contains("Invalid subject id")
            }

            assertThat(exceptionThrown).isTrue()
        }

    @Test
    fun `get events with negative subject id throws IllegalArgumentException, IllegalArgumentException`() =
        runTest {
            fakeSubjectRepository.shouldHaveFilledEventList(true)
            val invalidSubjectId = -1

            var exceptionThrown = false
            try {
                getEventsOfSubjectUseCase(invalidSubjectId).first()
            } catch (e: IllegalArgumentException) {
                exceptionThrown = true
                assertThat(e.message).contains("Invalid subject id")
            }

            assertThat(exceptionThrown).isTrue()
        }

    @Test
    fun `deleted subject has no related events, returns empty list`() = runTest {
        fakeSubjectRepository.shouldHaveFilledEventList(true)
        val subjectId = 1

        // First verify the subject has events
        val initialEvents = getEventsOfSubjectUseCase(subjectId).first()
        assertThat(initialEvents).isNotEmpty()

        // Delete the subject
        fakeSubjectRepository.deleteSubjectById(subjectId)

        // Try to get events - should return empty list due to error handling
        val eventsAfterDeletion = getEventsOfSubjectUseCase(subjectId).first()
        assertThat(eventsAfterDeletion).isEmpty()
    }
}