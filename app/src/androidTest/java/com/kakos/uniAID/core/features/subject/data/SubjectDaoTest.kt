package com.kakos.uniAID.core.features.subject.data

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.kakos.uniAID.core.data.local.AppDatabase
import com.kakos.uniAID.core.di.AppModule
import com.kakos.uniAID.core.di.subject.di.SubjectModule
import com.kakos.uniAID.core.domain.subject.domain.model.Subject
import com.kakos.uniAID.core.data.subject.data.SubjectDao
import com.kakos.uniAID.calendar.data.EventDao
import com.kakos.uniAID.calendar.di.CalendarModule
import com.kakos.uniAID.calendar.domain.model.Event
import com.kakos.uniAID.notes.data.NoteDao
import com.kakos.uniAID.notes.di.NoteModule
import com.kakos.uniAID.notes.domain.model.Note
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import jakarta.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@HiltAndroidTest
@SmallTest
@UninstallModules(
    AppModule::class,
    NoteModule::class,
    SubjectModule::class,
    CalendarModule::class
)
class SubjectDaoTest {

    private val tag = "SubjectDaoTest"

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var database: AppDatabase
    private lateinit var eventDao: EventDao
    private lateinit var noteDao: NoteDao
    private lateinit var subjectDao: SubjectDao

    @Before
    fun setup() {
        hiltRule.inject()
        eventDao = database.eventDao()
        subjectDao = database.subjectDao
        noteDao = database.noteDao
    }

    @After
    fun teardown() {
        database.close()
    }

    // insert
    @Test
    fun insertSubject_subjectIsInserted() = runTest {
        val subject = Subject(
            id = 1,
            title = "Test Subject"
        )
        subjectDao.insert(subject)

        val subjects = subjectDao.getAllSubjects().first()
        Log.d(tag, "insertSubject_subjectIsInserted, subjects: $subjects")
        assertThat(subjects).contains(subject)
    }

    // update
    @Test
    fun updateSubject_subjectIsUpdated() = runTest {
        val subjectId = 1
        val subject = Subject(
            id = subjectId,
            title = "Test Subject"
        )

        // Insert the subject
        subjectDao.insert(subject)
        var retrievedSubject = subjectDao.getSubjectById(subjectId)
        Log.d(tag, "updateSubject_subjectIsUpdated, subject: $retrievedSubject")
        assertThat(retrievedSubject == subject).isTrue()

        // Update the subject
        val updatedSubject = subject.copy(
            title = "Updated Subject"
        )
        subjectDao.update(updatedSubject)
        retrievedSubject = subjectDao.getSubjectById(subjectId)
        Log.d(tag, "updateSubject_subjectIsUpdated, subject: $retrievedSubject")
        assertThat(retrievedSubject == updatedSubject && retrievedSubject != subject).isTrue()
    }

    // delete
    @Test
    fun deleteSubject_subjectIsDeleted() = runTest {
        val subject = Subject(
            id = 1,
            title = "Test Subject"
        )
        val subject2 = Subject(
            id = 2,
            title = "Another Subject"
        )

        // Insert the subjects
        subjectDao.insert(subject)
        subjectDao.insert(subject2)

        var subjects = subjectDao.getAllSubjects().first()
        Log.d(tag, "deleteSubject_subjectIsDeleted, subjects: $subjects")
        assertThat(subjects.contains(subject)).isTrue()

        // Delete the subject
        subjectDao.delete(subject)
        subjects = subjectDao.getAllSubjects().first()
        Log.d(tag, "deleteSubject_subjectIsDeleted, subjects: $subjects")
        assertThat(subjects).doesNotContain(subject)
    }

    // deleteSubjectById
    @Test
    fun deleteSubjectById_subjectIsDeleted() = runTest {
        val subjectId = 1
        val subject = Subject(
            id = subjectId,
            title = "Test Subject"
        )
        // Insert the subject
        subjectDao.insert(subject)
        var retrievedSubject = subjectDao.getSubjectById(subjectId)
        Log.d(tag, "deleteSubjectById_subjectIsDeleted, subject: $retrievedSubject")
        assertThat(retrievedSubject == subject).isTrue()

        // Delete the subject by id
        subjectDao.deleteSubjectById(subjectId)
        retrievedSubject = subjectDao.getSubjectById(subjectId)
        Log.d(tag, "deleteSubjectById_subjectIsDeleted, subject: $retrievedSubject")
        assertThat(retrievedSubject).isNull()
    }

    // getSubjectById
    @Test
    fun getSubjectById_subjectIsReturned() = runTest {
        val subjectId = 1
        val subject1 = Subject(
            id = subjectId,
            title = "Test Subject"
        )
        subjectDao.insert(subject1)

        val subject2 = Subject(
            id = 2,
            title = "Another Subject"
        )
        subjectDao.insert(subject2)


        val retrievedSubject = subjectDao.getSubjectById(subjectId)
        Log.d(tag, "getSubjectById_subjectIsReturned, subject: $retrievedSubject")
        assertThat(retrievedSubject).isEqualTo(subject1)
    }

    @Test
    fun getSubjectById_subjectIsNotReturned() = runTest {
        val subjectId = 1
        val subject1 = Subject(
            id = subjectId,
            title = "Test Subject"
        )
        subjectDao.insert(subject1)

        val subject2 = Subject(
            id = 3,
            title = "Another Subject"
        )
        subjectDao.insert(subject2)


        val retrievedSubject = subjectDao.getSubjectById(subjectId + 1)
        Log.d(tag, "getSubjectById_subjectIsNotReturned, subject: $retrievedSubject")
        assertThat(retrievedSubject).isNull()
    }

    // getAllSubjects
    @Test
    fun getAllSubjects_returnsAllSubjects() = runTest {
        val subject1 = Subject(
            id = 1,
            title = "Subject 1"
        )

        val subject2 = Subject(
            id = 2,
            title = "Subject 2"
        )

        val subjectList = listOf(subject1, subject2)

        for (subject in subjectList) {
            subjectDao.insert(subject)
        }

        val subjects = subjectDao.getAllSubjects().first()
        Log.d(tag, "getAllSubjects_returnsAllSubjects, subjects: $subjects")

        assertThat(subjects.size).isEqualTo(2)
        assertThat(subjects).containsExactlyElementsIn(subjectList)
    }

    @Test
    fun getAllSubjectsFromEmptyDatabase_subjectListIsEmpty() = runTest {
        val subjects = subjectDao.getAllSubjects().first()
        assertThat(subjects).isEmpty()
    }

    // getSubjectsBySemester
    @Test
    fun getSubjectsBySemester_returnsSubjectsWithMatchingSemester() = runTest {
        val semester1 = 1
        val semester2 = 2

        val subject1 = Subject(
            id = 1,
            title = "Subject 1",
            semester = semester1
        )

        val subject2 = Subject(
            id = 2,
            title = "Subject 2",
            semester = semester1
        )

        val subject3 = Subject(
            id = 3,
            title = "Subject 3",
            semester = semester2
        )

        subjectDao.insert(subject1)
        subjectDao.insert(subject2)
        subjectDao.insert(subject3)

        val semester1Subjects = subjectDao.getSubjectsBySemester(semester1).first()
        Log.d(
            tag,
            "getSubjectsBySemester_returnsSubjectsWithMatchingSemester, subjects: $semester1Subjects"
        )

        assertThat(semester1Subjects).contains(subject1)
        assertThat(semester1Subjects).contains(subject2)
        assertThat(semester1Subjects).doesNotContain(subject3)
        assertThat(semester1Subjects.size).isEqualTo(2)
        assertThat(semester1Subjects).containsExactly(subject1, subject2)
    }

    // getSubjectWithNotes
    @Test
    fun getSubjectWithNotes_returnsSubjectWithAssociatedNotes() = runTest {
        val subjectId = 1
        val subject = Subject(
            id = subjectId,
            title = "Test Subject"
        )
        subjectDao.insert(subject)

        val note1 = Note(
            id = 1,
            title = "Note 1",
            content = "Content 1",
            creationTime = LocalDateTime.now(),
            lastModified = LocalDateTime.now(),
            darkTheme = true,
            subjectId = subjectId
        )

        val note2 = Note(
            id = 2,
            title = "Note 2",
            content = "Content 2",
            creationTime = LocalDateTime.now(),
            lastModified = LocalDateTime.now(),
            darkTheme = false,
            subjectId = subjectId
        )

        noteDao.insertNote(note1)
        noteDao.insertNote(note2)

        val subjectWithNotes = subjectDao.getSubjectWithNotes(subjectId)
        Log.d(
            tag,
            "getSubjectWithNotes_returnsSubjectWithAssociatedNotes, subject: ${subjectWithNotes.subject}, notes: ${subjectWithNotes.notes}"
        )

        assertThat(subjectWithNotes.subject).isEqualTo(subject)
        assertThat(subjectWithNotes.notes).containsExactly(note1, note2)
    }

    // getSubjectWithEvents
    @Test
    fun getSubjectWithEvents_returnsSubjectWithAssociatedEvents() = runTest {
        val subjectId = 1
        val subject = Subject(
            id = subjectId,
            title = "Test Subject"
        )
        subjectDao.insert(subject)

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
            subjectId = subjectId,
            subjectName = "Test Subject",
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
            subjectId = subjectId,
            subjectName = "Test Subject",
            repeatEndDate = LocalDate.now().plusDays(7)
        )

        eventDao.insertEvent(event1)
        eventDao.insertEvent(event2)

        val subjectWithEvents = subjectDao.getSubjectWithEvents(subjectId)
        Log.d(
            tag,
            "getSubjectWithEvents_returnsSubjectWithAssociatedEvents, subject: ${subjectWithEvents.subject}, events: ${subjectWithEvents.events}"
        )

        assertThat(subjectWithEvents.subject).isEqualTo(subject)
        assertThat(subjectWithEvents.events).containsExactly(event1, event2)
    }

    @Test
    fun deleteSubjectWithRelatedData_relationshipsAreHandledCorrectly() = runTest {
        val subjectId = 1
        val subject = Subject(
            id = subjectId,
            title = "Test Subject"
        )
        subjectDao.insert(subject)

        // Create an event associated with the subject
        val event = Event(
            id = 1,
            title = "Event with Subject",
            description = "Test",
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            allDay = false,
            repeatId = null,
            subjectId = subjectId,
            subjectName = "Test Subject",
            repeatEndDate = LocalDate.now().plusDays(7)
        )
        eventDao.insertEvent(event)

        // Create a note associated with the subject
        val note = Note(
            id = 1,
            title = "Note with Subject",
            content = "Content",
            creationTime = LocalDateTime.now(),
            lastModified = LocalDateTime.now(),
            darkTheme = true,
            subjectId = subjectId,
            subjectName = "Test Subject"
        )
        noteDao.insertNote(note)

        var retrievedEvent = eventDao.getEventById(1)
        var retrievedNote = noteDao.getNoteById(1)
        assertThat(retrievedEvent?.subjectId).isEqualTo(subjectId)
        assertThat(retrievedNote?.subjectId).isEqualTo(subjectId)

        eventDao.clearSubjectFromEvents(subjectId)
        noteDao.clearSubjectFromNotes(subjectId)
        subjectDao.deleteSubjectById(subjectId)

        val retrievedSubject = subjectDao.getSubjectById(subjectId)
        assertThat(retrievedSubject).isNull()

        retrievedEvent = eventDao.getEventById(1)
        assertThat(retrievedEvent?.subjectId).isNull()
        assertThat(retrievedEvent?.subjectName).isNull()

        retrievedNote = noteDao.getNoteById(1)
        assertThat(retrievedNote?.subjectId).isNull()
        assertThat(retrievedNote?.subjectName).isNull()
    }

    // Stress test
    @Test
    fun largeNumberOfSubjects_canBeInsertedAndGetBack() = runTest {
        val subjectCount = 10000

        // Insert
        for (i in 1..subjectCount) {
            val subject = Subject(
                id = i,
                title = "Subject $i",
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
                semester = (i % 10) + 1
            )
            subjectDao.insert(subject)
        }

        // Verify
        val allSubjects = subjectDao.getAllSubjects().first()
        Log.d(tag, "largeNumberOfSubjects_canBeInsertedAndRetrieved, count: ${allSubjects.size}")

        assertThat(allSubjects.size).isEqualTo(subjectCount)
    }

    @Test
    fun largeNumberOfSubjects_canBeGetBackWithSemester() = runTest {
        val subjectCount = 10000
        val targetSemester = 3
        var expectedCount = 0

        // Insert subjects wit semesters
        for (i in 1..subjectCount) {
            val semester = (i % 5) + 1
            if (semester == targetSemester) expectedCount++

            val subject = Subject(
                id = i,
                title = "Subject $i",
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
                semester = semester
            )
            subjectDao.insert(subject)
        }

        // Get with semester
        val filteredSubjects = subjectDao.getSubjectsBySemester(targetSemester).first()
        Log.d(
            tag,
            "largeNumberOfSubjectsWithSemester_canBeFiltered, count: ${filteredSubjects.size}"
        )

        // Verify
        assertThat(filteredSubjects.size).isEqualTo(expectedCount)
    }

    @Test
    fun largeNumberOfRelatedItems_canBeGetBackWithSubject() = runTest {
        val subjectId = 1
        val subject = Subject(
            id = subjectId,
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
            title = "Lot of relations"
        )
        subjectDao.insert(subject)

        // Add big number of notes
        val noteCount = 5000
        for (i in 1..noteCount) {
            val note = Note(
                id = i,
                title = "Note $i",
                content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas " +
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
                creationTime = LocalDateTime.now(),
                lastModified = LocalDateTime.now(),
                darkTheme = i % 2 == 0,
                subjectId = subjectId,
                subjectName = subject.title
            )
            noteDao.insertNote(note)
        }

        // Add big number of events
        val eventCount = 5000
        for (i in 1..eventCount) {
            val event = Event(
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
                repeatId = null,
                subjectId = subjectId,
                subjectName = subject.title,
                repeatEndDate = LocalDate.now().plusDays(30)
            )
            eventDao.insertEvent(event)
        }

        // Verify
        val subjectWithNotes = subjectDao.getSubjectWithNotes(subjectId)
        for (note in subjectWithNotes.notes) {
            Log.d(tag, "Note: ${note.id}, ${note.title}, Subject: ${note.subjectName}")
        }
        assertThat(subjectWithNotes.notes.size).isEqualTo(noteCount)

        val subjectWithEvents = subjectDao.getSubjectWithEvents(subjectId)
        for (event in subjectWithEvents.events) {
            Log.d(tag, "Event:  ${event.id}, ${event.title}, Subject: ${event.subjectName}")
        }
        assertThat(subjectWithEvents.events.size).isEqualTo(eventCount)
    }

    @Test
    fun fastCRUDOperations() = runTest {
        val operationCount = 10000
        val maxSubjectId = 100

        for (i in 1..operationCount) {
            val subjectId = (i % maxSubjectId) + 1

            when (i % 4) {
                0 -> {
                    // Insert
                    val subject = Subject(
                        id = subjectId,
                        title = "Subject $subjectId v${i / maxSubjectId}",
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
                    )
                    subjectDao.insert(subject)
                }

                1 -> {
                    // Update
                    val existing = subjectDao.getSubjectById(subjectId)
                    if (existing != null) {
                        subjectDao.update(
                            existing.copy(
                                title = "Updated ${existing.title}",
                                description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas " +
                                        "vehicula volutpat porttitor. Fusce fringilla nisi id diam aliquet, ac " +
                                        "suscipit turpis fringilla. Maecenas at tortor blandit, rutrum ante a, " +
                                        "feugiat elit. Curabitur sollicitudin felis viverra nisi aliquam scelerisque." +
                                        " Quisque finibus purus est, et venenatis odio vehicula at. Donec dignissim " +
                                        "hendrerit felis at sollicitudin. Morbi pharetra placerat faucibus. Curabitur" +
                                        " semper feugiat nisi eget cursus. Donec aliquam, diam sit amet feugiat " +
                                        "iaculis, diam lectus feugiat magna, ac pretium neque sapien sed urna. " +
                                        "Sed pretium mattis leo quis egestas. Suspendisse pretium eu ipsum sed " +
                                        "cursus. Curabitur in diam dui."
                            )
                        )
                    }
                }

                2 -> {
                    // Read
                    subjectDao.getSubjectById(subjectId)
                }

                3 -> {
                    // Delete
                    val existing = subjectDao.getSubjectById(subjectId)
                    if (existing != null && i > operationCount / 2) {
                        subjectDao.delete(existing)
                    }
                }
            }
        }


        // Verify
        val remainingSubjects = subjectDao.getAllSubjects().first()
        for (subject in remainingSubjects) {
            Log.d(tag, " subject: $subject")
        }
        Log.d(
            tag,
            "rapidSequentialCrudOperations_areHandledCorrectly, final count: ${remainingSubjects.size}"
        )

        assertThat(remainingSubjects.size).isAtMost(maxSubjectId)
    }
}