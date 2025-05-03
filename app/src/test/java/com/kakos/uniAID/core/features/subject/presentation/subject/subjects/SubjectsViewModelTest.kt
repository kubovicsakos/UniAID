package com.kakos.uniAID.core.features.subject.presentation.subject.subjects

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import com.google.common.truth.Truth.assertThat
import com.kakos.uniAID.MainDispatcherRule
import com.kakos.uniAID.core.features.subject.data.repository.FakeSubjectRepository
import com.kakos.uniAID.core.features.subject.domain.fakeSubjectUseCases
import com.kakos.uniAID.core.domain.subject.domain.model.Subject
import com.kakos.uniAID.core.domain.subject.domain.use_case.SubjectUseCases
import com.kakos.uniAID.subjectAndStats.presentation.subject.subjects.SubjectsViewModel
import com.kakos.uniAID.subjectAndStats.presentation.subject.subjects.util.SubjectsEvent
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SubjectsViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: SubjectsViewModel
    private lateinit var fakeSubjectRepository: FakeSubjectRepository
    private lateinit var subjectUseCases: SubjectUseCases
    private lateinit var dataStore: DataStore<Preferences>

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

        viewModel = SubjectsViewModel(subjectUseCases, dataStore)
    }

    @Test
    fun `initial state has correct default values`() = runTest {
        advanceUntilIdle()

        assertThat(viewModel.state.value.subjects).isEmpty()
        assertThat(viewModel.state.value.isLoading).isFalse()
        assertThat(viewModel.state.value.currentSemester).isEqualTo(1)
    }

    @Test
    fun `loading subjects populates state correctly`() = runTest {
        fakeSubjectRepository.shouldHaveFilledList(true)
        viewModel = SubjectsViewModel(subjectUseCases, dataStore)
        advanceUntilIdle()

        assertThat(viewModel.state.value.subjects).isNotEmpty()
    }

    @Test
    fun `setting semester filters subjects correctly`() = runTest {
        // Add test subjects
        val subjects = listOf(
            Subject(id = 1, title = "Math", semester = 1, credit = 6),
            Subject(id = 2, title = "Physics", semester = 1, credit = 5),
            Subject(id = 3, title = "Chemistry", semester = 2, credit = 4)
        )

        for (subject in subjects) {
            fakeSubjectRepository.insert(subject)
        }

        viewModel = SubjectsViewModel(subjectUseCases, dataStore)
        advanceUntilIdle()

        // Set semester to 1
        viewModel.onEvent(SubjectsEvent.SetSemester(1))
        advanceUntilIdle()

        assertThat(viewModel.state.value.subjects.size).isEqualTo(2)
        assertThat(viewModel.state.value.subjects.map { it.title }).containsExactly(
            "Math",
            "Physics"
        )

        // Change semester
        viewModel.onEvent(SubjectsEvent.SetSemester(2))
        advanceUntilIdle()

        assertThat(viewModel.state.value.subjects.size).isEqualTo(1)
        assertThat(viewModel.state.value.subjects.first().title).isEqualTo("Chemistry")
    }

    @Test
    fun `setting invalid semester does not update state and emits error`() = runTest {
        val initialSemester = viewModel.state.value.currentSemester

        viewModel.onEvent(SubjectsEvent.SetSemester(-1))
        advanceUntilIdle()

        assertThat(viewModel.state.value.currentSemester).isEqualTo(initialSemester)

        val events = mutableListOf<SubjectsViewModel.UiEvent>()
        val job = viewModel.eventFlow.onEach { events.add(it) }.launchIn(this)

        viewModel.onEvent(SubjectsEvent.SetSemester(-1))
        advanceUntilIdle()

        assertThat(events).contains(SubjectsViewModel.UiEvent.ShowSnackbar("Invalid semester value"))
        job.cancel()
    }

    @Test
    fun `datastore correctly loads and sets current semester`() = runTest {
        val testSemester = 3
        val testDataStore = mockk<DataStore<Preferences>>(relaxed = true)
        every { testDataStore.data } returns flowOf(
            androidx.datastore.preferences.core.preferencesOf(intPreferencesKey("current_semester") to testSemester)
        )

        viewModel = SubjectsViewModel(subjectUseCases, testDataStore)
        advanceUntilIdle()

        assertThat(viewModel.state.value.currentSemester).isEqualTo(testSemester)
    }

//    @Test
//    fun `exception during subject loading emits error message`() = runTest {
//        // Create a mock that throws an exception immediately when getAllSubjects is called
//        val exceptionSubjectUseCases = mockk<SubjectUseCases>()
//        every { exceptionSubjectUseCases.getAllSubjects() } throws Exception("Test exception")
//
//        viewModel = SubjectsViewModel(exceptionSubjectUseCases, dataStore)
//
//        val events = mutableListOf<SubjectsViewModel.UiEvent>()
//        val job = viewModel.eventFlow.onEach { events.add(it) }.launchIn(this)
//        advanceUntilIdle()
//
//        println(events)
//        assertThat(events).contains(SubjectsViewModel.UiEvent.ShowSnackbar("Exception while fetching subjects"))
//        job.cancel()
//    }

    @Test
    fun `empty repository returns empty subjects list`() = runTest {
        fakeSubjectRepository.shouldHaveFilledList(false)
        viewModel = SubjectsViewModel(subjectUseCases, dataStore)
        advanceUntilIdle()

        assertThat(viewModel.state.value.subjects).isEmpty()
    }
}