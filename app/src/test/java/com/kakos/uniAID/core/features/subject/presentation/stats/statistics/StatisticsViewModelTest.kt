package com.kakos.uniAID.core.features.subject.presentation.stats.statistics

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.intPreferencesKey
import com.google.common.truth.Truth.assertThat
import com.kakos.uniAID.MainDispatcherRule
import com.kakos.uniAID.core.features.subject.data.repository.FakeSubjectRepository
import com.kakos.uniAID.core.features.subject.domain.fakeSubjectUseCases
import com.kakos.uniAID.core.domain.subject.domain.model.Subject
import com.kakos.uniAID.core.domain.subject.domain.use_case.SubjectUseCases
import com.kakos.uniAID.subjectAndStats.presentation.stats.statistics.util.StatisticsEvent
import com.kakos.uniAID.subjectAndStats.presentation.stats.statistics.StatisticsViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class StatisticsViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: StatisticsViewModel
    private lateinit var fakeSubjectRepository: FakeSubjectRepository
    private lateinit var subjectUseCases: SubjectUseCases
    private lateinit var dataStore: DataStore<androidx.datastore.preferences.core.Preferences>

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.d(any(), any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        fakeSubjectRepository = FakeSubjectRepository()
        subjectUseCases = fakeSubjectUseCases(fakeSubjectRepository)
        fakeSubjectRepository.shouldHaveFilledList(false)
        dataStore = mockk(relaxed = true)
        every { dataStore.data } returns flowOf(
            androidx.datastore.preferences.core.preferencesOf(intPreferencesKey("current_semester") to 1)
        )

        viewModel = StatisticsViewModel(subjectUseCases, dataStore)
    }

    @Test
    fun `initialization loads current semester from data store, sets up initial state`() = runTest {
        // Current semester is set to 1 in setup
        assertThat(viewModel.state.value.currentSemester).isEqualTo(1)
        assertThat(viewModel.state.value.subjects).isEmpty()
    }

    @Test
    fun `setting valid semester updates state and filters subjects correctly`() = runTest {
        // Add test subjects
        val subjects = listOf(
            Subject(id = 1, title = "Math", grade = 5, credit = 6, semester = 1),
            Subject(id = 2, title = "Physics", grade = 4, credit = 5, semester = 1),
            Subject(id = 3, title = "Chemistry", grade = 3, credit = 4, semester = 2)
        )

        for (subject in subjects) {
            fakeSubjectRepository.insert(subject)
        }

        // Reinitialize viewModel to load subjects
        viewModel = StatisticsViewModel(subjectUseCases, dataStore)
        advanceUntilIdle()

        // Set semester to 1
        viewModel.onEvent(StatisticsEvent.SetSemester(1))
        advanceUntilIdle()

        // Verify state
        assertThat(viewModel.state.value.currentSemester).isEqualTo(1)
        assertThat(viewModel.state.value.subjects.size).isEqualTo(2)
        assertThat(viewModel.state.value.subjects.map { it.title }).containsExactly(
            "Math",
            "Physics"
        )

        // Change semester
        viewModel.onEvent(StatisticsEvent.SetSemester(2))
        advanceUntilIdle()

        assertThat(viewModel.state.value.currentSemester).isEqualTo(2)
        assertThat(viewModel.state.value.subjects.size).isEqualTo(1)
        assertThat(viewModel.state.value.subjects.first().title).isEqualTo("Chemistry")
    }

    @Test
    fun `setting invalid semester does not update state`() = runTest {
        val initialSemester = viewModel.state.value.currentSemester

        viewModel.onEvent(StatisticsEvent.SetSemester(-1))
        advanceUntilIdle()

        assertThat(viewModel.state.value.currentSemester).isEqualTo(initialSemester)
    }

    @Test
    fun `setting grade for subject updates subject and recalculates stats`() = runTest {
        // Add test subjects
        val subject = Subject(id = 1, title = "Math", grade = 3, credit = 5, semester = 1)
        fakeSubjectRepository.insert(subject)

        // Reinitialize viewModel to load subjects
        viewModel = StatisticsViewModel(subjectUseCases, dataStore)
        advanceUntilIdle()

        // Get initial weighted average
        val initialAverage = viewModel.state.value.weightedAverage

        // Set new grade (5)
        viewModel.onEvent(StatisticsEvent.SetGrade(1, 5))
        advanceUntilIdle()

        // Verify subject grade was updated and stats recalculated
        val updatedSubject = fakeSubjectRepository.getSubjectById(1)
        assertThat(updatedSubject?.grade).isEqualTo(5)
        assertThat(viewModel.state.value.weightedAverage).isGreaterThan(initialAverage)
    }

    @Test
    fun `stats are calculated correctly for subjects with failing grades`() = runTest {
        val subjects = listOf(
            Subject(id = 1, title = "Math", grade = 5, credit = 6, semester = 1),
            Subject(id = 2, title = "Physics", grade = 1, credit = 5, semester = 1), // Failed
            Subject(id = 3, title = "Chemistry", grade = 4, credit = 4, semester = 1)
        )

        for (subject in subjects) {
            fakeSubjectRepository.insert(subject)
        }

        viewModel = StatisticsViewModel(subjectUseCases, dataStore)
        advanceUntilIdle()

        assertThat(viewModel.state.value.committedCredit).isEqualTo(15)
        assertThat(viewModel.state.value.completedCredit).isEqualTo(10) // Only Math and Chemistry
        assertThat(viewModel.state.value.weightedAverage).isEqualTo(4.6f) // (5*6 + 4*4) / 10
    }

    @Test
    fun `stats are calculated correctly with null grades and credits`() = runTest {
        val subjects = listOf(
            Subject(id = 1, title = "Math", grade = 5, credit = 6, semester = 1),
            Subject(
                id = 2,
                title = "Physics",
                grade = null,
                credit = 5,
                semester = 1
            ), // Not graded
            Subject(
                id = 3,
                title = "Chemistry",
                grade = 4,
                credit = null,
                semester = 1
            ) // No credit
        )

        for (subject in subjects) {
            fakeSubjectRepository.insert(subject)
        }

        viewModel = StatisticsViewModel(subjectUseCases, dataStore)
        advanceUntilIdle()

        assertThat(viewModel.state.value.committedCredit).isEqualTo(6)
        assertThat(viewModel.state.value.completedCredit).isEqualTo(6)
        assertThat(viewModel.state.value.weightedAverage).isWithin(0.01f).of(5.0f)
    }

    @Test
    fun `empty subject list results in zero stats`() = runTest {
        // Ensure repository is empty
        fakeSubjectRepository.shouldHaveFilledList(false)

        viewModel = StatisticsViewModel(subjectUseCases, dataStore)
        advanceUntilIdle()

        assertThat(viewModel.state.value.committedCredit).isEqualTo(0)
        assertThat(viewModel.state.value.completedCredit).isEqualTo(0)
        assertThat(viewModel.state.value.weightedAverage).isEqualTo(0f)
        assertThat(viewModel.state.value.ci).isEqualTo(0f)
        assertThat(viewModel.state.value.cci).isEqualTo(0f)
    }
}