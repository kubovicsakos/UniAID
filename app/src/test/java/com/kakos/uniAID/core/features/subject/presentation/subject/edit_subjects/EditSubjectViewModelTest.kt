package com.kakos.uniAID.core.features.subject.presentation.subject.edit_subjects

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
import com.kakos.uniAID.subjectAndStats.presentation.subject.edit_subjects.util.EditSubjectEvent
import com.kakos.uniAID.subjectAndStats.presentation.subject.edit_subjects.EditSubjectViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EditSubjectViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: EditSubjectViewModel
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

        viewModel = EditSubjectViewModel(subjectUseCases, dataStore)
    }

    @Test
    fun `initial state has correct default values`() = runTest {
        advanceUntilIdle()
        assertThat(viewModel.state.value.title.text).isEmpty()
        assertThat(viewModel.state.value.description).isEmpty()
        assertThat(viewModel.state.value.semester).isNull()
        assertThat(viewModel.state.value.credit).isEqualTo(0)
        assertThat(viewModel.state.value.finalGrade).isNull()
        assertThat(viewModel.state.value.grade).isNull()
        assertThat(viewModel.state.value.isFormValid).isFalse()
    }

    @Test
    fun `loading existing subject populates state correctly`() = runTest {
        // Add test subject
        val subject = Subject(
            id = 1,
            title = "Math",
            description = "Calculus",
            semester = 1,
            credit = 5,
            finalGrade = 4,
            grade = 5
        )
        fakeSubjectRepository.insert(subject)

        // Get subject by ID
        viewModel.onEvent(EditSubjectEvent.GetSubjectById(1))
        advanceUntilIdle()

        viewModel.state.value.apply {
            assertThat(title.text).isEqualTo("Math")
            assertThat(description).isEqualTo("Calculus")
            assertThat(semester).isEqualTo(1)
            assertThat(credit).isEqualTo(5)
            assertThat(finalGrade).isEqualTo(4)
            assertThat(grade).isEqualTo(5)
            assertThat(isFormValid).isTrue()
        }
    }

    @Test
    fun `loading -1 subject id creates new subject with current semester`() = runTest {
        viewModel.onEvent(EditSubjectEvent.GetSubjectById(-1))
        advanceUntilIdle()

        assertThat(viewModel.state.value.semester).isEqualTo(1)
        assertThat(viewModel.state.value.title.text).isEmpty()
    }

    @Test
    fun `entering title updates state`() = runTest {
        val testTitle = "Physics"
        viewModel.onEvent(EditSubjectEvent.EnteredTitle(testTitle))
        advanceUntilIdle()

        assertThat(viewModel.state.value.title.text).isEqualTo(testTitle)
    }

    @Test
    fun `entering description updates state`() = runTest {
        val testDescription = "Quantum Physics"
        viewModel.onEvent(EditSubjectEvent.EnteredDescription(testDescription))
        advanceUntilIdle()

        assertThat(viewModel.state.value.description).isEqualTo(testDescription)
    }

    @Test
    fun `entering semester updates state`() = runTest {
        val testSemester = 3
        viewModel.onEvent(EditSubjectEvent.EnteredSemester(testSemester))
        advanceUntilIdle()

        assertThat(viewModel.state.value.semester).isEqualTo(testSemester)
    }

    @Test
    fun `entering credit updates state`() = runTest {
        val testCredit = 6
        viewModel.onEvent(EditSubjectEvent.EnteredCredit(testCredit))
        advanceUntilIdle()

        assertThat(viewModel.state.value.credit).isEqualTo(testCredit)
    }

    @Test
    fun `entering final grade updates state`() = runTest {
        val testFinalGrade = 5
        viewModel.onEvent(EditSubjectEvent.EnteredFinalGrade(testFinalGrade))
        advanceUntilIdle()

        assertThat(viewModel.state.value.finalGrade).isEqualTo(testFinalGrade)
    }

    @Test
    fun `form is valid with required fields filled`() = runTest {
        viewModel.onEvent(EditSubjectEvent.EnteredTitle("Math"))
        viewModel.onEvent(EditSubjectEvent.EnteredSemester(1))
        viewModel.onEvent(EditSubjectEvent.EnteredCredit(5))
        viewModel.onEvent(EditSubjectEvent.ValidateEvent)
        advanceUntilIdle()

        assertThat(viewModel.state.value.isFormValid).isTrue()
    }

    @Test
    fun `form is invalid with empty title`() = runTest {
        viewModel.onEvent(EditSubjectEvent.EnteredTitle(""))
        viewModel.onEvent(EditSubjectEvent.EnteredSemester(1))
        viewModel.onEvent(EditSubjectEvent.EnteredCredit(5))
        viewModel.onEvent(EditSubjectEvent.ValidateEvent)
        advanceUntilIdle()

        assertThat(viewModel.state.value.isFormValid).isFalse()
        assertThat(viewModel.state.value.title.error).isNotNull()
    }

    @Test
    fun `form is invalid without semester`() = runTest {
        viewModel.onEvent(EditSubjectEvent.EnteredTitle("Math"))
        viewModel.onEvent(EditSubjectEvent.EnteredCredit(5))
        viewModel.onEvent(EditSubjectEvent.ValidateEvent)
        advanceUntilIdle()

        assertThat(viewModel.state.value.isFormValid).isFalse()
    }

    @Test
    fun `saving new subject adds it to repository`() = runTest {
        fakeSubjectRepository.shouldHaveFilledList(true)
        viewModel = EditSubjectViewModel(subjectUseCases, dataStore)
        advanceUntilIdle()
        viewModel.onEvent(EditSubjectEvent.GetSubjectById(-1))
        advanceUntilIdle()
        viewModel.onEvent(EditSubjectEvent.EnteredTitle("Physics"))
        viewModel.onEvent(EditSubjectEvent.EnteredDescription("Classical Physics"))
        viewModel.onEvent(EditSubjectEvent.EnteredSemester(1))
        viewModel.onEvent(EditSubjectEvent.EnteredCredit(5))
        viewModel.onEvent(EditSubjectEvent.EnteredFinalGrade(4))
        advanceUntilIdle()
        viewModel.onEvent(EditSubjectEvent.SaveSubject)
        advanceUntilIdle()

        val subjects = fakeSubjectRepository.getAllSubjects().first()
        assertThat(subjects).isNotEmpty()

        val subject = fakeSubjectRepository.getSubjectById(4)
        assertThat(subject).isNotNull()
        assertThat(subject?.title).isEqualTo("Physics")
        assertThat(subject?.semester).isEqualTo(1)
        assertThat(subject?.credit).isEqualTo(5)
    }

    @Test
    fun `updating existing subject changes repository data`() = runTest {
        // Add initial subject
        fakeSubjectRepository.insert(Subject(id = 1, title = "Math", semester = 1, credit = 5))

        // Load subject
        viewModel.onEvent(EditSubjectEvent.GetSubjectById(1))
        advanceUntilIdle()

        // Update subject
        viewModel.onEvent(EditSubjectEvent.EnteredTitle("Advanced Math"))
        viewModel.onEvent(EditSubjectEvent.SaveSubject)
        advanceUntilIdle()

        val updatedSubject = fakeSubjectRepository.getSubjectById(1)
        assertThat(updatedSubject?.title).isEqualTo("Advanced Math")
    }

    @Test
    fun `invalid subject is not saved`() = runTest {
        viewModel.onEvent(EditSubjectEvent.EnteredTitle(""))
        viewModel.onEvent(EditSubjectEvent.SaveSubject)
        advanceUntilIdle()

        assertThat(fakeSubjectRepository.getAllSubjects().first()).isEmpty()
    }
}