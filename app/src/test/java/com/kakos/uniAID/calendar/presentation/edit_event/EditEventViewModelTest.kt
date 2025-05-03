package com.kakos.uniAID.calendar.presentation.edit_event

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.common.truth.Truth.assertThat
import com.kakos.uniAID.MainDispatcherRule
import com.kakos.uniAID.core.features.subject.data.repository.FakeSubjectRepository
import com.kakos.uniAID.core.features.subject.domain.fakeSubjectUseCases
import com.kakos.uniAID.core.domain.subject.domain.use_case.SubjectUseCases
import com.kakos.uniAID.calendar.data.repository.FakeEventRepository
import com.kakos.uniAID.calendar.domain.model.Event
import com.kakos.uniAID.calendar.domain.model.Repeat
import com.kakos.uniAID.calendar.domain.use_case.EventUseCases
import com.kakos.uniAID.calendar.domain.use_case.fakeEventUseCases
import com.kakos.uniAID.calendar.domain.util.SaveOption
import com.kakos.uniAID.calendar.presentation.edit_event.util.EditCalendarEventEvent
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

@OptIn(ExperimentalCoroutinesApi::class)
class EditEventViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: EditEventViewModel
    private lateinit var fakeEventRepository: FakeEventRepository
    private lateinit var fakeSubjectRepository: FakeSubjectRepository
    private lateinit var eventUseCases: EventUseCases
    private lateinit var subjectUseCases: SubjectUseCases
    private lateinit var dataStore: DataStore<Preferences>

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.d(any(), any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        fakeEventRepository = FakeEventRepository()
        eventUseCases = fakeEventUseCases(fakeEventRepository)
        fakeSubjectRepository = FakeSubjectRepository()
        subjectUseCases = fakeSubjectUseCases(fakeSubjectRepository)
        fakeEventRepository.shouldHaveFilledList(false)
        dataStore = mockk(relaxed = true)
        every { dataStore.data } returns flowOf(
            androidx.datastore.preferences.core.preferencesOf(
                stringPreferencesKey("week_start_day") to DayOfWeek.MONDAY.name
            )
        )

        viewModel = EditEventViewModel(eventUseCases, subjectUseCases, dataStore)
    }

    // Initial state tests
    @Test
    fun `initial state has correct default values`() = runTest {
        advanceUntilIdle()

        assertThat(viewModel.state.value.title.text).isEmpty()
        assertThat(viewModel.state.value.description.text).isEmpty()
        assertThat(viewModel.state.value.location.text).isEmpty()
        assertThat(viewModel.state.value.startDate).isEqualTo(LocalDate.now())
        assertThat(viewModel.state.value.endDate).isEqualTo(LocalDate.now())
        assertThat(viewModel.state.value.isFormValid).isFalse()
    }

    @Test
    fun `loading existing event populates state correctly`() = runTest {
        val testEvent = Event(
            id = 1,
            title = "Meeting",
            description = "Team sync",
            location = "Conference Room",
            color = 0,
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
        fakeEventRepository.insertEvent(testEvent)

        viewModel.onEvent(EditCalendarEventEvent.GetEventById(1))
        advanceUntilIdle()

        viewModel.state.value.apply {
            assertThat(title.text).isEqualTo("Meeting")
            assertThat(description.text).isEqualTo("Team sync")
            assertThat(location.text).isEqualTo("Conference Room")
            assertThat(startDate).isEqualTo(LocalDate.now())
            assertThat(endDate).isEqualTo(LocalDate.now())
            assertThat(startTime).isEqualTo(LocalTime.of(10, 0))
            assertThat(endTime).isEqualTo(LocalTime.of(11, 0))
            assertThat(allDay).isFalse()
            assertThat(isFormValid).isTrue()
        }
    }

    @Test
    fun `loading -1 event id creates new event with current date`() = runTest {
        viewModel.onEvent(EditCalendarEventEvent.GetEventById(-1))
        advanceUntilIdle()

        assertThat(viewModel.state.value.startDate).isEqualTo(LocalDate.now())
        assertThat(viewModel.state.value.title.text).isEmpty()
    }

    @Test
    fun `InitializeStartDate and InitializeEndDate date sets dates correctly`() = runTest {
        viewModel.onEvent(EditCalendarEventEvent.GetEventById(-1))
        advanceUntilIdle()

        viewModel.onEvent(EditCalendarEventEvent.InitializeStartDate(LocalDate.now().plusDays(1)))
        viewModel.onEvent(EditCalendarEventEvent.InitializeEndDate(LocalDate.now().plusDays(1)))

        assertThat(viewModel.state.value.startDate).isEqualTo(LocalDate.now().plusDays(1))
        assertThat(viewModel.state.value.endDate).isEqualTo(LocalDate.now().plusDays(1))
        assertThat(viewModel.state.value.title.text).isEmpty()
    }

    @Test
    fun `test loadDefaultEventColor on new event`() = runTest {
        val testDataStore = mockk<DataStore<Preferences>>(relaxed = true)
        every { testDataStore.data } returns flowOf(
            androidx.datastore.preferences.core.preferencesOf(
                intPreferencesKey("event_color") to 3
            )
        )

        viewModel = EditEventViewModel(eventUseCases, subjectUseCases, testDataStore)
        viewModel.onEvent(EditCalendarEventEvent.GetEventById(-1))
        advanceUntilIdle()
        assertThat(viewModel.state.value.color).isEqualTo(3)
    }

    // onEvent tests

    @Test
    fun `entering title, updates state`() = runTest {
        val testTitle = "Exam"
        viewModel.onEvent(EditCalendarEventEvent.EnteredTitle(testTitle))
        advanceUntilIdle()

        assertThat(viewModel.state.value.title.text).isEqualTo(testTitle)
    }

    @Test
    fun `entering description, updates state`() = runTest {
        val testDescription = "Final exam for Computer Science"
        viewModel.onEvent(EditCalendarEventEvent.EnteredDescription(testDescription))
        advanceUntilIdle()

        assertThat(viewModel.state.value.description.text).isEqualTo(testDescription)
    }

    @Test
    fun `entering location, updates state`() = runTest {
        val testLocation = "Room 101"
        viewModel.onEvent(EditCalendarEventEvent.EnteredLocation(testLocation))
        advanceUntilIdle()

        assertThat(viewModel.state.value.location.text).isEqualTo(testLocation)
    }

    @Test
    fun `entering dates, updates state`() = runTest {
        val testStartDate = LocalDate.now().plusDays(1)
        val testEndDate = LocalDate.now().plusDays(2)

        viewModel.onEvent(EditCalendarEventEvent.EnteredStartDate(testStartDate))
        viewModel.onEvent(EditCalendarEventEvent.EnteredEndDate(testEndDate))
        advanceUntilIdle()

        assertThat(viewModel.state.value.startDate).isEqualTo(testStartDate)
        assertThat(viewModel.state.value.endDate).isEqualTo(testEndDate)
    }

    @Test
    fun `entering times, updates state`() = runTest {
        val testStartTime = LocalTime.of(9, 30)
        val testEndTime = LocalTime.of(11, 45)

        viewModel.onEvent(EditCalendarEventEvent.EnteredStartTime(testStartTime))
        viewModel.onEvent(EditCalendarEventEvent.EnteredEndTime(testEndTime))
        advanceUntilIdle()

        assertThat(viewModel.state.value.startTime).isEqualTo(testStartTime)
        assertThat(viewModel.state.value.endTime).isEqualTo(testEndTime)
    }

    @Test
    fun `toggling all day, updates state`() = runTest {
        viewModel.onEvent(EditCalendarEventEvent.EnteredAllDay(true))
        advanceUntilIdle()

        assertThat(viewModel.state.value.allDay).isTrue()

        viewModel.onEvent(EditCalendarEventEvent.EnteredAllDay(false))
        advanceUntilIdle()

        assertThat(viewModel.state.value.allDay).isFalse()
    }

    @Test
    fun `entering repeat, updates state`() = runTest {
        val testRepeat = Repeat.WEEKLY
        viewModel.onEvent(EditCalendarEventEvent.EnteredRepeat(testRepeat))
        advanceUntilIdle()

        assertThat(viewModel.state.value.repeat).isEqualTo(testRepeat)
    }

    @Test
    fun `entering repeatEndDate, updates state`() = runTest {
        val testRepeatEndDate = LocalDate.now().plusDays(5)
        viewModel.onEvent(EditCalendarEventEvent.EnteredRepeatEndDate(testRepeatEndDate))
        advanceUntilIdle()

        assertThat(viewModel.state.value.repeatEndDate).isEqualTo(testRepeatEndDate)
    }

    @Test
    fun `entering repeat difference, updates state`() = runTest {
        val testRepeatDifference = 2L
        viewModel.onEvent(EditCalendarEventEvent.EnteredRepeatDifference(testRepeatDifference))
        advanceUntilIdle()

        assertThat(viewModel.state.value.repeatDifference).isEqualTo(testRepeatDifference)
    }

    @Test
    fun `entering repeat days, updates state`() = runTest {
        val testRepeatDays = listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY)
        viewModel.onEvent(EditCalendarEventEvent.EnteredSelectedDays(testRepeatDays))
        advanceUntilIdle()

        assertThat(viewModel.state.value.selectedDays).isEqualTo(testRepeatDays)
    }

    @Test
    fun `entering color, updates state`() = runTest {
        val testColor = 2
        viewModel.onEvent(EditCalendarEventEvent.EnteredColor(testColor))
        advanceUntilIdle()

        assertThat(viewModel.state.value.color).isEqualTo(testColor)
    }

    @Test
    fun `fetching subjects updates the states`() = runTest {
        val testSubjectId = 1
        val testSubjectName = "a"
        fakeSubjectRepository.shouldHaveFilledList(true)
        fakeSubjectRepository.getAllSubjects().first().forEach {
            println("Subject: $it")
        }
        advanceUntilIdle()
        viewModel = EditEventViewModel(
            eventUseCases = eventUseCases,
            subjectUseCases = subjectUseCases,
            dataStore = dataStore
        )
        advanceUntilIdle()

        viewModel.onEvent(EditCalendarEventEvent.FetchSubjects)
        advanceUntilIdle()

        assertThat(viewModel.state.value.subjects.size).isEqualTo(3) // 3 subjects
        assertThat(viewModel.state.value.subjects[0].id).isEqualTo(testSubjectId) // first subject id
        assertThat(viewModel.state.value.subjects[0].title).isEqualTo(testSubjectName) // first subject name
        assertThat(viewModel.state.value.filteredSubjects.size).isEqualTo(2) // 2 subjects with semester 1
    }

    @Test
    fun `entering subject updates state`() = runTest {
        val testSubjectId = 1
        val testSubjectName = "a"
        fakeSubjectRepository.shouldHaveFilledList(true)
        advanceUntilIdle()

        viewModel = EditEventViewModel(
            eventUseCases = eventUseCases,
            subjectUseCases = subjectUseCases,
            dataStore = dataStore
        )
        advanceUntilIdle()

        viewModel.onEvent(EditCalendarEventEvent.EnteredSubject(testSubjectId))
        advanceUntilIdle()

        assertThat(viewModel.state.value.subjectId).isEqualTo(testSubjectId)
        assertThat(viewModel.state.value.subjectName).isEqualTo(testSubjectName)
    }

    @Test
    fun `GetDefaultEventColor updates state`() = runTest {
        val testColor = 2
        val testDataStore = mockk<DataStore<Preferences>>(relaxed = true)
        every { testDataStore.data } returns flowOf(
            androidx.datastore.preferences.core.preferencesOf(
                intPreferencesKey("event_color") to testColor
            )
        )

        viewModel = EditEventViewModel(eventUseCases, subjectUseCases, testDataStore)
        advanceUntilIdle()

        viewModel.onEvent(EditCalendarEventEvent.GetDefaultEventColor)
        advanceUntilIdle()

        assertThat(viewModel.state.value.color).isEqualTo(testColor)
    }

    @Test
    fun `test setCurrentSemesterEndDates`() = runTest {

        val testDataStore = mockk<DataStore<Preferences>>(relaxed = true)
        val tomorrowMillis =
            LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
                .toEpochMilli()
        val semesterEndDatesString = "3:$tomorrowMillis"

        every { testDataStore.data } returns flowOf(
            androidx.datastore.preferences.core.preferencesOf(
                intPreferencesKey("current_semester") to 3,
                stringPreferencesKey("semester_end_dates") to semesterEndDatesString
            )
        )

        viewModel = EditEventViewModel(eventUseCases, subjectUseCases, testDataStore)
        advanceUntilIdle()
        viewModel.onEvent(EditCalendarEventEvent.GetEventById(-1))

        viewModel.onEvent(EditCalendarEventEvent.SetCurrentSemesterEndDate)
        advanceUntilIdle()

        assertThat(viewModel.state.value.repeatEndDate).isEqualTo(LocalDate.now().plusDays(1))
    }

    // Validation tests
    @Test
    fun `form is valid with, required fields filled`() = runTest {
        viewModel.onEvent(EditCalendarEventEvent.EnteredTitle("Exam"))
        viewModel.onEvent(EditCalendarEventEvent.EnteredStartDate(LocalDate.now()))
        viewModel.onEvent(EditCalendarEventEvent.EnteredEndDate(LocalDate.now()))
        viewModel.onEvent(EditCalendarEventEvent.ValidateEvent)
        advanceUntilIdle()

        assertThat(viewModel.state.value.isFormValid).isTrue()
    }

    @Test
    fun `form is valid with valid repeat end date`() = runTest {
        viewModel.onEvent(EditCalendarEventEvent.EnteredTitle("Meeting"))
        viewModel.onEvent(EditCalendarEventEvent.EnteredStartDate(LocalDate.now()))
        viewModel.onEvent(EditCalendarEventEvent.EnteredEndDate(LocalDate.now()))
        viewModel.onEvent(EditCalendarEventEvent.EnteredRepeat(Repeat.DAILY))
        viewModel.onEvent(EditCalendarEventEvent.EnteredRepeatEndDate(LocalDate.now().plusDays(2)))
        viewModel.onEvent(EditCalendarEventEvent.ValidateEvent)
        advanceUntilIdle()

        assertThat(viewModel.state.value.isFormValid).isTrue()
        assertThat(viewModel.state.value.repeatEndDate).isEqualTo(LocalDate.now().plusDays(2))
    }

    @Test
    fun `form is invalid, with wrong start date`() = runTest {
        viewModel.onEvent(EditCalendarEventEvent.EnteredTitle("Meeting"))
        viewModel.onEvent(EditCalendarEventEvent.EnteredStartDate(LocalDate.now().plusDays(2)))
        viewModel.onEvent(EditCalendarEventEvent.EnteredEndDate(LocalDate.now()))
        viewModel.onEvent(EditCalendarEventEvent.ValidateEvent)
        advanceUntilIdle()

        assertThat(viewModel.state.value.isFormValid).isFalse()
        assertThat(viewModel.state.value.startDate).isEqualTo(LocalDate.now().plusDays(2))
    }

    @Test
    fun `form is invalid with wrong end date`() = runTest {
        viewModel.onEvent(EditCalendarEventEvent.EnteredTitle("Meeting"))
        viewModel.onEvent(EditCalendarEventEvent.EnteredStartDate(LocalDate.now()))
        viewModel.onEvent(EditCalendarEventEvent.EnteredEndDate(LocalDate.now().minusDays(2)))
        viewModel.onEvent(EditCalendarEventEvent.ValidateEvent)
        advanceUntilIdle()

        assertThat(viewModel.state.value.isFormValid).isFalse()
        assertThat(viewModel.state.value.endDate).isEqualTo(LocalDate.now().minusDays(2))
    }

    @Test
    fun `form is invalid with wrong title`() = runTest {
        viewModel.onEvent(EditCalendarEventEvent.EnteredTitle(" "))
        viewModel.onEvent(EditCalendarEventEvent.EnteredStartDate(LocalDate.now()))
        viewModel.onEvent(EditCalendarEventEvent.EnteredEndDate(LocalDate.now()))
        viewModel.onEvent(EditCalendarEventEvent.ValidateEvent)
        advanceUntilIdle()

        assertThat(viewModel.state.value.isFormValid).isFalse()
        assertThat(viewModel.state.value.title.error).isNotNull()
    }

    @Test
    fun `form is invalid with wrong repeat end date`() = runTest {
        viewModel.onEvent(EditCalendarEventEvent.EnteredTitle("Meeting"))
        viewModel.onEvent(EditCalendarEventEvent.EnteredStartDate(LocalDate.now()))
        viewModel.onEvent(EditCalendarEventEvent.EnteredEndDate(LocalDate.now()))
        viewModel.onEvent(EditCalendarEventEvent.EnteredRepeat(Repeat.DAILY))
        viewModel.onEvent(EditCalendarEventEvent.EnteredRepeatEndDate(LocalDate.now().minusDays(2)))
        viewModel.onEvent(EditCalendarEventEvent.ValidateEvent)
        advanceUntilIdle()

        assertThat(viewModel.state.value.isFormValid).isFalse()
        assertThat(viewModel.state.value.repeatEndDate).isEqualTo(LocalDate.now().minusDays(2))
    }

    @Test
    fun `form is invalid with end time before start time on same day`() = runTest {
        viewModel.onEvent(EditCalendarEventEvent.EnteredTitle("Meeting"))
        viewModel.onEvent(EditCalendarEventEvent.EnteredStartDate(LocalDate.now()))
        viewModel.onEvent(EditCalendarEventEvent.EnteredEndDate(LocalDate.now()))
        viewModel.onEvent(EditCalendarEventEvent.EnteredStartTime(LocalTime.of(14, 0)))
        viewModel.onEvent(EditCalendarEventEvent.EnteredEndTime(LocalTime.of(13, 0)))
        viewModel.onEvent(EditCalendarEventEvent.ValidateEvent)
        advanceUntilIdle()

        assertThat(viewModel.state.value.isFormValid).isFalse()
    }

    // Saving/Updating tests

    @Test
    fun `saving new event adds it to repository`() = runTest {
        fakeEventRepository.shouldHaveFilledList(true)
        viewModel.onEvent(EditCalendarEventEvent.GetEventById(-1))
        advanceUntilIdle()

        viewModel.onEvent(EditCalendarEventEvent.EnteredTitle("Lecture"))
        viewModel.onEvent(EditCalendarEventEvent.EnteredDescription("Computer Science"))
        viewModel.onEvent(EditCalendarEventEvent.EnteredLocation("Hall A"))
        viewModel.onEvent(EditCalendarEventEvent.EnteredStartDate(LocalDate.now()))
        viewModel.onEvent(EditCalendarEventEvent.EnteredEndDate(LocalDate.now()))
        viewModel.onEvent(EditCalendarEventEvent.EnteredStartTime(LocalTime.of(10, 0)))
        viewModel.onEvent(EditCalendarEventEvent.EnteredEndTime(LocalTime.of(12, 0)))
        viewModel.onEvent(EditCalendarEventEvent.EnteredAllDay(false))
        viewModel.onEvent(EditCalendarEventEvent.EnteredRepeat(Repeat.NONE))
        viewModel.onEvent(EditCalendarEventEvent.EnteredRepeatEndDate(LocalDate.now().plusWeeks(1)))
        viewModel.onEvent(EditCalendarEventEvent.EnteredColor(3))
        viewModel.onEvent(EditCalendarEventEvent.ValidateEvent)
        advanceUntilIdle()

        println("State after validation: ${viewModel.state.value}")
        viewModel.onEvent(EditCalendarEventEvent.SaveEvent(saveOption = SaveOption.THIS))
        advanceTimeBy(1000)
        advanceUntilIdle()


        val events = fakeEventRepository.getAllEvents().first()
        assertThat(events).isNotEmpty()
        assertThat(events.size).isEqualTo(4)
        println("Events: ")
        events.forEach { event ->
            println("Event: $event")
        }

        val event = events.last()
        assertThat(event.title).isEqualTo("Lecture")
        assertThat(event.description).isEqualTo("Computer Science")
        assertThat(event.location).isEqualTo("Hall A")
    }

    @Test
    fun `updating existing event changes repository data`() = runTest {
        val initialEvent = Event(
            id = 1,
            title = "Meeting",
            description = "",
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
        )
        fakeEventRepository.insertEvent(initialEvent)

        viewModel.onEvent(EditCalendarEventEvent.GetEventById(1))
        advanceUntilIdle()

        viewModel.onEvent(EditCalendarEventEvent.EnteredTitle("Updated Meeting"))
        viewModel.onEvent(EditCalendarEventEvent.SaveEvent(saveOption = SaveOption.THIS))
        advanceUntilIdle()

        val updatedEvent = fakeEventRepository.getEventById(1)
        assertThat(updatedEvent?.title).isEqualTo("Updated Meeting")
    }

    @Test
    fun `saving new repeating event adds them to repository`() = runTest {
        fakeEventRepository.shouldHaveFilledList(true)
        viewModel.onEvent(EditCalendarEventEvent.GetEventById(-1))
        advanceUntilIdle()

        viewModel.onEvent(EditCalendarEventEvent.EnteredTitle("Lecture"))
        viewModel.onEvent(EditCalendarEventEvent.EnteredDescription("Computer Science"))
        viewModel.onEvent(EditCalendarEventEvent.EnteredLocation("Hall A"))
        viewModel.onEvent(EditCalendarEventEvent.EnteredStartDate(LocalDate.now()))
        viewModel.onEvent(EditCalendarEventEvent.EnteredEndDate(LocalDate.now()))
        viewModel.onEvent(EditCalendarEventEvent.EnteredStartTime(LocalTime.of(10, 0)))
        viewModel.onEvent(EditCalendarEventEvent.EnteredEndTime(LocalTime.of(12, 0)))
        viewModel.onEvent(EditCalendarEventEvent.EnteredAllDay(false))
        viewModel.onEvent(EditCalendarEventEvent.EnteredRepeat(Repeat.DAILY))
        viewModel.onEvent(EditCalendarEventEvent.EnteredRepeatEndDate(LocalDate.now().plusWeeks(1)))
        viewModel.onEvent(EditCalendarEventEvent.EnteredColor(3))
        viewModel.onEvent(EditCalendarEventEvent.ValidateEvent)
        advanceUntilIdle()

        println("State after validation: ${viewModel.state.value}")
        viewModel.onEvent(EditCalendarEventEvent.SaveEvent(saveOption = SaveOption.THIS))
        advanceTimeBy(1000)
        advanceUntilIdle()

        val events = fakeEventRepository.getAllEvents().first()
        events.forEach {
            println("Event: $it")
        }
        assertThat(events).isNotEmpty()
        assertThat(events.size).isEqualTo(11)
    }

    @Test
    fun `updating existing repeating event changes repository data`() = runTest {
        fakeEventRepository.shouldHaveFilledList(true)
        viewModel.onEvent(EditCalendarEventEvent.GetEventById(-1))
        advanceUntilIdle()

        viewModel.onEvent(EditCalendarEventEvent.EnteredTitle("Lecture"))
        viewModel.onEvent(EditCalendarEventEvent.EnteredDescription("Computer Science"))
        viewModel.onEvent(EditCalendarEventEvent.EnteredLocation("Hall A"))
        viewModel.onEvent(EditCalendarEventEvent.EnteredStartDate(LocalDate.now()))
        viewModel.onEvent(EditCalendarEventEvent.EnteredEndDate(LocalDate.now()))
        viewModel.onEvent(EditCalendarEventEvent.EnteredStartTime(LocalTime.of(10, 0)))
        viewModel.onEvent(EditCalendarEventEvent.EnteredEndTime(LocalTime.of(12, 0)))
        viewModel.onEvent(EditCalendarEventEvent.EnteredAllDay(false))
        viewModel.onEvent(EditCalendarEventEvent.EnteredRepeat(Repeat.DAILY))
        viewModel.onEvent(EditCalendarEventEvent.EnteredRepeatEndDate(LocalDate.now().plusWeeks(1)))
        viewModel.onEvent(EditCalendarEventEvent.EnteredColor(3))
        viewModel.onEvent(EditCalendarEventEvent.ValidateEvent)
        advanceUntilIdle()

        println("State after validation: ${viewModel.state.value}")
        viewModel.onEvent(EditCalendarEventEvent.SaveEvent(saveOption = SaveOption.THIS))
        advanceTimeBy(1000)
        advanceUntilIdle()

        val events = fakeEventRepository.getAllEvents().first()
        events.forEach {
            println("Event: $it")
        }
        assertThat(events).isNotEmpty()
        assertThat(events.size).isEqualTo(11)

        viewModel.onEvent(EditCalendarEventEvent.GetEventById(5))
        advanceUntilIdle()

        viewModel.onEvent(EditCalendarEventEvent.EnteredTitle("Updated Meeting"))
        viewModel.onEvent(EditCalendarEventEvent.EnteredDescription("Updated Description"))
        viewModel.onEvent(EditCalendarEventEvent.EnteredLocation("Updated Location"))
        viewModel.onEvent(EditCalendarEventEvent.EnteredRepeat(Repeat.WEEKLY))
        viewModel.onEvent(EditCalendarEventEvent.EnteredRepeatEndDate(LocalDate.now().plusWeeks(2)))
        viewModel.onEvent(EditCalendarEventEvent.EnteredSelectedDays(listOf(DayOfWeek.MONDAY)))
        viewModel.onEvent(EditCalendarEventEvent.ValidateEvent)
        viewModel.onEvent(EditCalendarEventEvent.SaveEvent(saveOption = SaveOption.ALL))
        advanceUntilIdle()

        val updatedEvents = fakeEventRepository.getAllEvents().first()
        updatedEvents.forEach {
            println("Updated Event: $it")
        }
        assertThat(updatedEvents.size).isEqualTo(5)
    }

    @Test
    fun `invalid title event is not saved`() = runTest {
        fakeEventRepository.shouldHaveFilledList(true)
        viewModel.onEvent(EditCalendarEventEvent.EnteredTitle(""))
        viewModel.onEvent(EditCalendarEventEvent.SaveEvent(saveOption = SaveOption.THIS))
        advanceUntilIdle()

        fakeEventRepository.getAllEvents().first().forEach {
            println("Event: $it")
        }
        assertThat(fakeEventRepository.getAllEvents().first()).isNotEmpty()
        assertThat(fakeEventRepository.getAllEvents().first().size).isEqualTo(3)
    }

    @Test
    fun `invalid date event is not saved`() = runTest {
        fakeEventRepository.shouldHaveFilledList(true)
        viewModel.onEvent(EditCalendarEventEvent.EnteredStartDate(LocalDate.now().plusDays(2)))
        viewModel.onEvent(EditCalendarEventEvent.EnteredEndDate(LocalDate.now()))
        viewModel.onEvent(EditCalendarEventEvent.SaveEvent(saveOption = SaveOption.THIS))
        advanceUntilIdle()

        fakeEventRepository.getAllEvents().first().forEach {
            println("Event: $it")
        }
        assertThat(fakeEventRepository.getAllEvents().first()).isNotEmpty()
        assertThat(fakeEventRepository.getAllEvents().first().size).isEqualTo(3)
    }

    @Test
    fun `invalid time event is not saved`() = runTest {
        fakeEventRepository.shouldHaveFilledList(true)
        viewModel.onEvent(EditCalendarEventEvent.EnteredStartTime(LocalTime.of(14, 0)))
        viewModel.onEvent(EditCalendarEventEvent.EnteredEndTime(LocalTime.of(13, 0)))
        viewModel.onEvent(EditCalendarEventEvent.SaveEvent(saveOption = SaveOption.THIS))
        advanceUntilIdle()

        fakeEventRepository.getAllEvents().first().forEach {
            println("Event: $it")
        }
        assertThat(fakeEventRepository.getAllEvents().first()).isNotEmpty()
        assertThat(fakeEventRepository.getAllEvents().first().size).isEqualTo(3)
    }

    @Test
    fun `invalid repeat end date event is not saved`() = runTest {
        fakeEventRepository.shouldHaveFilledList(true)
        viewModel.onEvent(EditCalendarEventEvent.EnteredRepeatEndDate(LocalDate.now().minusDays(2)))
        viewModel.onEvent(EditCalendarEventEvent.SaveEvent(saveOption = SaveOption.THIS))
        advanceUntilIdle()

        fakeEventRepository.getAllEvents().first().forEach {
            println("Event: $it")
        }
        assertThat(fakeEventRepository.getAllEvents().first()).isNotEmpty()
        assertThat(fakeEventRepository.getAllEvents().first().size).isEqualTo(3)
    }
}