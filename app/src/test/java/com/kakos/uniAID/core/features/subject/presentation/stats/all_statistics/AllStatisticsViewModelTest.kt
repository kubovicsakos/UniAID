package com.kakos.uniAID.core.features.subject.presentation.stats.all_statistics

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.kakos.uniAID.MainDispatcherRule
import com.kakos.uniAID.core.features.subject.data.repository.FakeSubjectRepository
import com.kakos.uniAID.core.features.subject.domain.fakeSubjectUseCases
import com.kakos.uniAID.core.domain.subject.domain.model.Subject
import com.kakos.uniAID.core.domain.subject.domain.use_case.SubjectUseCases
import com.kakos.uniAID.subjectAndStats.presentation.stats.all_statistics.AllStatisticsViewModel
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AllStatisticsViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: AllStatisticsViewModel
    private lateinit var fakeSubjectRepository: FakeSubjectRepository
    private lateinit var subjectUseCases: SubjectUseCases
    private lateinit var allStatisticsViewModel: AllStatisticsViewModel

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

    }


    @Test
    fun getAllSemestersData_shouldCalculateStatsCorrectlyWithValidData() = runTest {

        viewModel = AllStatisticsViewModel(subjectUseCases)

        val subjects = listOf(
            Subject(id = 1, title = "Math", grade = 5, credit = 6, semester = 1),
            Subject(id = 2, title = "Physics", grade = 4, credit = 5, semester = 1),
            Subject(id = 3, title = "Chemistry", grade = 3, credit = 4, semester = 2)
        )

        for (subject in subjects) {
            fakeSubjectRepository.insert(subject)
        }

        advanceUntilIdle()
        viewModel.getAllSemestersData()

        val stats = viewModel.state.value.semesterStats
        assertThat(stats.size).isEqualTo(2)

        val semStats1 = stats.find { it.semester == 1 }
        println("Semester stats: $semStats1")
        assertThat(semStats1).isNotNull()
        assertThat(semStats1!!.committedCredit).isEqualTo(11)
        assertThat(semStats1.completedCredit).isEqualTo(11)
        assertThat(semStats1.weightedAverage).isWithin(0.1f).of(4.55f)

        val semStats2 = stats.find { it.semester == 2 }
        println("Semester stats: $semStats2")
        assertThat(semStats2).isNotNull()
        assertThat(semStats2!!.committedCredit).isEqualTo(4)
        assertThat(semStats2.completedCredit).isEqualTo(4)
        assertThat(semStats2.weightedAverage).isWithin(0.1f).of(3.0f)
    }


    @Test
    fun whenSubjectsWithFailingGrades_shouldCalculateStatsCorrectly() = runTest {

        viewModel = AllStatisticsViewModel(subjectUseCases)
        val subjects = listOf(
            Subject(id = 1, title = "Math", grade = 5, credit = 6, semester = 1),
            Subject(
                id = 2,
                title = "Physics",
                grade = 1,
                credit = 5,
                semester = 1
            ), // Failed subject
            Subject(id = 3, title = "Chemistry", grade = 4, credit = 4, semester = 1)
        )

        for (subject in subjects) {
            fakeSubjectRepository.insert(subject)
        }

        advanceUntilIdle()
        viewModel.getAllSemestersData()

        val semStats = viewModel.state.value.semesterStats[0]
        println("Semester stats: $semStats")
        assertThat(semStats.committedCredit).isEqualTo(15)
        assertThat(semStats.completedCredit).isEqualTo(10) // Only Math and Chemistry completed
        assertThat(semStats.weightedAverage).isWithin(0.1f)
            .of(4.6f) // (6*5 + 4*4) / 10 = 46 / 10 = 4.6
    }

    @Test
    fun whenSubjectsWithNullGrade_shouldExcludeFromCalculations() = runTest {

        viewModel = AllStatisticsViewModel(subjectUseCases)
        val subjects = listOf(
            Subject(id = 1, title = "Math", grade = 5, credit = 6, semester = 1),
            Subject(
                id = 2,
                title = "Physics",
                grade = null,
                credit = 5,
                semester = 1
            ), // Not graded
            Subject(id = 3, title = "Chemistry", grade = 4, credit = 4, semester = 1)
        )
        for (subject in subjects) {
            fakeSubjectRepository.insert(subject)
        }

        advanceUntilIdle()
        viewModel.getAllSemestersData()

        val semStats = viewModel.state.value.semesterStats[0]
        assertThat(semStats.committedCredit).isEqualTo(10)
        assertThat(semStats.completedCredit).isEqualTo(10)
    }

    @Test
    fun `when subject credit is null, should handle it with leaving it out`() = runTest {

        viewModel = AllStatisticsViewModel(subjectUseCases)

        val subjects = listOf(
            Subject(id = 4, title = "Math", grade = 5, credit = null, semester = 1), // No credit
            Subject(id = 5, title = "Physics", grade = 4, credit = 5, semester = 1),
            Subject(
                id = 6,
                title = "Chemistry",
                grade = 3,
                credit = null,
                semester = 2
            ), // No credit
            Subject(id = 7, title = "Biology", grade = 2, credit = 4, semester = 2)
        )
        for (subject in subjects) {
            fakeSubjectRepository.insert(subject)
        }

        val allSubjects = subjectUseCases.getAllSubjects().first()
        println("All subjects:")
        allSubjects.forEach {
            println(it)
        }

        advanceUntilIdle()
        viewModel.getAllSemestersData()

        println("Semester stats:")
        viewModel.state.value.semesterStats.forEach {
            println(it)
        }
        val semStats1 = viewModel.state.value.semesterStats[0]
        val semStats2 = viewModel.state.value.semesterStats[1]
        assertThat(semStats1.committedCredit).isEqualTo(5)
        assertThat(semStats1.completedCredit).isEqualTo(5)
        assertThat(semStats2.completedCredit).isEqualTo(4)
        assertThat(semStats2.committedCredit).isEqualTo(4)
    }

    @Test
    fun whenMultipleSemesterDataAvailable_shouldSortBySemesterNumber() = runTest {

        viewModel = AllStatisticsViewModel(subjectUseCases)
        val subjects = listOf(
            Subject(id = 1, title = "Math", grade = 5, credit = 6, semester = 3),
            Subject(id = 2, title = "Physics", grade = 4, credit = 5, semester = 1),
            Subject(id = 3, title = "Chemistry", grade = 3, credit = 4, semester = 2)
        )
        for (subject in subjects) {
            fakeSubjectRepository.insert(subject)
        }

        advanceUntilIdle()
        viewModel.getAllSemestersData()

        val stats = viewModel.state.value.semesterStats
        assertThat(stats.size).isEqualTo(3)
        assertThat(stats[0].semester).isEqualTo(1)
        assertThat(stats[1].semester).isEqualTo(2)
        assertThat(stats[2].semester).isEqualTo(3)
    }
}