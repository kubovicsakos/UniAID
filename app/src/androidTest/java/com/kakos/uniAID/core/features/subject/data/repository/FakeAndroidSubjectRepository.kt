package com.kakos.uniAID.core.features.subject.data.repository

import com.kakos.uniAID.core.domain.subject.domain.model.Subject
import com.kakos.uniAID.core.domain.subject.domain.model.SubjectWithEvents
import com.kakos.uniAID.core.domain.subject.domain.model.SubjectWithNotes
import com.kakos.uniAID.core.domain.subject.domain.repository.SubjectRepository
import com.kakos.uniAID.calendar.domain.model.Event
import com.kakos.uniAID.calendar.domain.model.Repeat
import com.kakos.uniAID.notes.domain.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class FakeAndroidSubjectRepository : SubjectRepository {
    private var subjectItems = MutableStateFlow<List<Subject>>(emptyList())

    fun shouldHaveFilledList(shouldHaveFilledList: Boolean) {
        subjectItems = if (shouldHaveFilledList) {
            MutableStateFlow(
                listOf(
                    Subject(
                        id = 1,
                        title = "a",
                        description = "Description 1",
                        semester = 1,
                        credit = 4,
                        finalGrade = 5,
                        grade = 4
                    ),
                    Subject(
                        id = 2,
                        title = "b",
                        description = "Description 2",
                        semester = 1,
                        credit = 2,
                        finalGrade = 2,
                        grade = 5
                    ),
                    Subject(
                        id = 1,
                        title = "c",
                        description = "Description 3",
                        semester = 2,
                        credit = 4,
                        finalGrade = 3,
                        grade = 3
                    )
                )
            )
        } else {
            MutableStateFlow(emptyList())
        }
    }

    private var noteItems: List<Note> = emptyList()

    fun shouldHaveFilledNoteList(shouldHaveFilledNoteList: Boolean) {
        noteItems = if (shouldHaveFilledNoteList) {
            listOf(
                Note(
                    id = 1,
                    title = "a",
                    content = "Content 1",
                    creationTime = LocalDateTime.now(),
                    lastModified = LocalDateTime.now(),
                    darkTheme = true,
                    subjectId = null,
                ),
                Note(
                    id = 2,
                    title = "b",
                    content = "Content 2",
                    creationTime = LocalDateTime.now().minusDays(1),
                    lastModified = LocalDateTime.now().minusDays(1),
                    darkTheme = true,
                    subjectId = 1,
                    subjectName = "a"
                ),
                Note(
                    id = 3,
                    title = "c",
                    content = "Content 3",
                    creationTime = LocalDateTime.now().minusDays(2),
                    lastModified = LocalDateTime.now().minusDays(2),
                    darkTheme = true,
                    subjectId = 2,
                    subjectName = "b"
                )
            )
        } else {
            emptyList()
        }
    }

    private var eventItems: List<Event> = emptyList()

    fun shouldHaveFilledEventList(shouldHaveFilledEventList: Boolean) {
        eventItems = if (shouldHaveFilledEventList) {
            listOf(
                Event(
                    id = 1,
                    title = "a",
                    description = "Description 1",
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
                ),
                Event(
                    id = 2,
                    title = "b",
                    description = "Description 2",
                    color = 1,
                    location = "location1",
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
                    subjectId = 1,
                    subjectName = "a"
                ),
                Event(
                    id = 3,
                    title = "c",
                    description = "Description 3",
                    color = 2,
                    location = "location2",
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
                    subjectId = 2,
                    subjectName = "b"
                )
            )
        } else {
            emptyList()
        }
    }

    override suspend fun insert(subject: Subject): Long {
        if (subject.id == null) {
            val subjectItemsLast = subjectItems.value.last()
            val newId = subjectItemsLast.id!!.plus(1)
            subjectItems.value += subject.copy(id = newId)
            return newId.toLong()
        } else {
            subjectItems.value += subject
            return subject.id!!.toLong()
        }
    }

    override suspend fun update(subject: Subject) {
        subjectItems.value = subjectItems.value.map {
            if (it.id == subject.id) {
                subject
            } else {
                it
            }
        }
    }

    override suspend fun delete(subject: Subject) {
        noteItems = noteItems.filter { it.subjectId != subject.id }
        eventItems = eventItems.filter { it.subjectId != subject.id }
        subjectItems.value = subjectItems.value.filter { it != subject }
    }

    override suspend fun deleteSubjectById(subjectId: Int) {
        noteItems = noteItems.filter { it.subjectId != subjectId }
        eventItems = eventItems.filter { it.subjectId != subjectId }
        subjectItems.value = subjectItems.value.filter { it.id != subjectId }
    }

    override suspend fun getSubjectById(subjectId: Int): Subject? {
        return subjectItems.value.find { it.id == subjectId }
    }

    override fun getAllSubjects(): Flow<List<Subject>> {
        return subjectItems
    }

    override fun getSubjectsBySemester(semester: Int): Flow<List<Subject>> {
        val subjects = subjectItems.value.filter { it.semester == semester }
        return MutableStateFlow(subjects)
    }


    override suspend fun getSubjectWithNotes(subjectId: Int): SubjectWithNotes {

        val notes = noteItems.filter { it.subjectId == subjectId }

        return subjectItems.value.find { it.id == subjectId }?.let { subject ->
            SubjectWithNotes(subject, notes)
        } ?: throw IllegalArgumentException("Subject not found")
    }


    override suspend fun getSubjectWithEvents(subjectId: Int): SubjectWithEvents {

        val events = eventItems.filter { it.subjectId == subjectId }

        return subjectItems.value.find { it.id == subjectId }?.let { subject ->
            SubjectWithEvents(subject, events)
        } ?: throw IllegalArgumentException("Subject not found")
    }
}