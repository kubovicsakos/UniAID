package com.kakos.uniAID.notes.data

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.kakos.uniAID.core.data.local.AppDatabase
import com.kakos.uniAID.core.di.AppModule
import com.kakos.uniAID.core.data.subject.data.SubjectDao
import com.kakos.uniAID.core.di.subject.di.SubjectModule
import com.kakos.uniAID.core.domain.subject.domain.model.Subject
import com.kakos.uniAID.calendar.di.CalendarModule
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
import java.time.LocalDateTime

@HiltAndroidTest
@SmallTest
@UninstallModules(
    AppModule::class,
    NoteModule::class,
    SubjectModule::class,
    CalendarModule::class
)
class NoteDaoTest {

    private val tag = "NoteDaoTest"

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var database: AppDatabase
    private lateinit var noteDao: NoteDao
    private lateinit var subjectDao: SubjectDao

    @Before
    fun setup() {
        hiltRule.inject()
        noteDao = database.noteDao
        subjectDao = database.subjectDao
    }

    @After
    fun teardown() {
        database.close()
    }

    // getNotes
    @Test
    fun getNotesFromEmptyDatabase_noteListIsEmpty() = runTest {
        val notes = noteDao.getNotes().first()
        assertThat(
            notes.isEmpty()
        ).isTrue()
    }

    @Test
    fun getNotesFromDatabase_noteListIsNotEmpty() = runTest {
        for (i in 1..3) {
            val newNote = Note(
                id = i,
                title = "Note $i",
                content = "Content $i",
                creationTime = LocalDateTime.now(),
                lastModified = LocalDateTime.now(),
                darkTheme = true
            )
            noteDao.insertNote(newNote)
        }

        val notes = noteDao.getNotes().first()
        Log.d(tag, "getAllNotesFromDatabase_noteListIsNotEmpty: $notes")
        assertThat(
            notes.isNotEmpty()
        ).isTrue()
    }


    // getNoteById
    @Test
    fun getNoteById_noteIsReturned() = runTest {
        val noteId = 1
        val newNote = Note(
            id = noteId,
            title = "Note 1",
            content = "Content 1",
            creationTime = LocalDateTime.now(),
            lastModified = LocalDateTime.now(),
            darkTheme = true
        )
        noteDao.insertNote(newNote)

        val note = noteDao.getNoteById(noteId)
        Log.d(tag, "getNoteById_noteIsReturned, note: $note")
        assertThat(
            note == newNote
        ).isTrue()
    }

    @Test
    fun getNoteById_noteIsNotReturned() = runTest {
        val noteId = 1
        val newNote = Note(
            id = noteId,
            title = "Note 1",
            content = "Content 1",
            creationTime = LocalDateTime.now(),
            lastModified = LocalDateTime.now(),
            darkTheme = true
        )
        noteDao.insertNote(newNote)

        val note = noteDao.getNoteById(noteId + 1)
        Log.d(tag, "getNoteById_noteIsNotReturned, note: $note")
        assertThat(
            note == null
        ).isTrue()
    }

    // insertNote
    @Test
    fun insertNote_noteIsInserted() = runTest {
        val newNote = Note(
            id = 1,
            title = "Note 1",
            content = "Content 1",
            creationTime = LocalDateTime.now(),
            lastModified = LocalDateTime.now(),
            darkTheme = true
        )
        noteDao.insertNote(newNote)

        val notes = noteDao.getNotes().first()
        Log.d(tag, "insertNote_noteIsInserted, notes: $notes")
        assertThat(
            notes.contains(newNote)
        ).isTrue()
    }

    // deleteNote
    @Test
    fun deleteNote_noteIsDeleted() = runTest {
        val newNote = Note(
            id = 1,
            title = "Note 1",
            content = "Content 1",
            creationTime = LocalDateTime.now(),
            lastModified = LocalDateTime.now(),
            darkTheme = true
        )
        // Insert the note
        noteDao.insertNote(newNote)
        var notes = noteDao.getNotes().first()
        Log.d(tag, "deleteNote_noteIsDeleted, notes: $notes")
        assertThat(
            notes.contains(newNote)
        ).isTrue()
        // Delete the note
        noteDao.deleteNote(newNote)
        notes = noteDao.getNotes().first()
        Log.d(tag, "deleteNote_noteIsDeleted, notes: $notes")
        assertThat(
            notes.contains(newNote)
        ).isFalse()
    }

    // updateNote
    @Test
    fun updateNote_noteIsUpdated() = runTest {
        val noteId = 1
        val newNote = Note(
            id = noteId,
            title = "Note 1",
            content = "Content 1",
            creationTime = LocalDateTime.now(),
            lastModified = LocalDateTime.now(),
            darkTheme = true
        )
        // Insert the note
        noteDao.insertNote(newNote)
        var note = noteDao.getNoteById(noteId)
        Log.d(tag, "updateNote_noteIsUpdated, note: $note")
        assertThat(
            note == newNote
        ).isTrue()
        // Update the note
        val updatedNote = newNote.copy(
            title = "Updated Note 1",
            content = "Updated Content 1"
        )
        noteDao.updateNote(updatedNote)
        note = noteDao.getNoteById(noteId)
        Log.d(tag, "updateNote_noteIsUpdated, notes: $note")
        assertThat(
            note == updatedNote && note != newNote
        ).isTrue()
    }

    // deleteNoteById
    @Test
    fun deleteNoteById_noteIsDeleted() = runTest {
        val noteId = 1
        val newNote = Note(
            id = noteId,
            title = "Note 1",
            content = "Content 1",
            creationTime = LocalDateTime.now(),
            lastModified = LocalDateTime.now(),
            darkTheme = true
        )
        // Insert the note
        noteDao.insertNote(newNote)
        var note = noteDao.getNoteById(noteId)
        Log.d(tag, "deleteNoteById_noteIsDeleted, notes: $note")
        assertThat(
            note == newNote
        ).isTrue()
        // Delete the note
        noteDao.deleteNoteById(noteId)
        note = noteDao.getNoteById(noteId)
        Log.d(tag, "deleteNoteById_noteIsDeleted, notes: $note")
        assertThat(
            note == null
        ).isTrue()
    }

    // getNotesWithSubjects
    @Test
    fun getNotesWithSubjects_notesAreReturned() = runTest {
        val subjectId = 1
        val subjectName = "Subject 1"
        subjectDao.insert(
            Subject(
                id = subjectId,
                title = subjectName,
                description = "Description 1",
                semester = 1
            )
        )
        val newNote = Note(
            id = 1,
            title = "Note 1",
            content = "Content 1",
            creationTime = LocalDateTime.now(),
            lastModified = LocalDateTime.now(),
            darkTheme = true,
            subjectId = subjectId,
            subjectName = subjectName
        )
        noteDao.insertNote(newNote)

        val notesWithSubjects = noteDao.getNotesWithSubjects().first()
        Log.d(tag, "getNotesWithSubjects_notesAreReturned, notes: $notesWithSubjects")
        assertThat(
            notesWithSubjects.any { it.note == newNote }
        ).isTrue()
    }

    // clearSubjectFromNotes
    @Test
    fun clearSubjectFromNotes_subjectIsCleared() = runTest {
        val subjectId = 1
        val subjectName = "Subject 1"
        subjectDao.insert(
            Subject(
                id = subjectId,
                title = subjectName,
                description = "Description 1",
                semester = 1
            )
        )
        val newNoteId = 1
        val newNote = Note(
            id = newNoteId,
            title = "Note 1",
            content = "Content 1",
            creationTime = LocalDateTime.now(),
            lastModified = LocalDateTime.now(),
            darkTheme = true,
            subjectId = subjectId,
            subjectName = subjectName
        )
        noteDao.insertNote(newNote)
        var notesWithSubjects = noteDao.getNotesWithSubjects().first()
        Log.d(tag, "clearSubjectFromNotes_subjectIsCleared, notes: $notesWithSubjects")

        // Clear the subject from the note
        noteDao.clearSubjectFromNotes(subjectId)

        notesWithSubjects = noteDao.getNotesWithSubjects().first()
        Log.d(tag, "clearSubjectFromNotes_subjectIsCleared, notes: $notesWithSubjects")
        assertThat(
            notesWithSubjects.any { it.note.id == newNoteId && it.note.subjectId == null && it.note.subjectName == null && it.subject == null },
        ).isTrue()
    }

    @Test
    fun clearSubjectFromNotes_onlyClearsCorrectNotes() = runTest {
        val subjectId1 = 1
        val subjectName1 = "Subject 1"
        val subjectId2 = 2
        val subjectName2 = "Subject 2"

        // Insert subjects
        subjectDao.insert(
            Subject(
                id = subjectId1,
                title = subjectName1,
                description = "Description 1",
                semester = 1
            )
        )
        subjectDao.insert(
            Subject(
                id = subjectId2,
                title = subjectName2,
                description = "Description 2",
                semester = 1
            )
        )

        // Insert notes with different subjects
        val newNote1 = Note(
            id = 1,
            title = "Note 1",
            content = "Content 1",
            creationTime = LocalDateTime.now(),
            lastModified = LocalDateTime.now(),
            darkTheme = true,
            subjectId = subjectId1,
            subjectName = subjectName1
        )
        noteDao.insertNote(newNote1)

        val newNote2 = Note(
            id = 2,
            title = "Note 2",
            content = "Content 2",
            creationTime = LocalDateTime.now(),
            lastModified = LocalDateTime.now(),
            darkTheme = true,
            subjectId = subjectId2,
            subjectName = subjectName2
        )
        noteDao.insertNote(newNote2)

        var notesWithSubjects = noteDao.getNotesWithSubjects().first()
        Log.d(tag, "clearSubjectFromNotes_onlyClearsCorrectNotes, notes: $notesWithSubjects")

        // Clear the first subject from the notes
        noteDao.clearSubjectFromNotes(subjectId1)

        notesWithSubjects = noteDao.getNotesWithSubjects().first()
        Log.d(tag, "clearSubjectFromNotes_onlyClearsCorrectNotes, notes: $notesWithSubjects")

        assertThat(
            notesWithSubjects.any { it.note.id == newNote1.id && it.note.subjectId == null && it.note.subjectName == null && it.subject == null }
        ).isTrue()

        assertThat(
            notesWithSubjects.any { it.note.id == newNote2.id && it.note.subjectId == subjectId2 && it.note.subjectName == subjectName2 && it.subject != null }
        ).isTrue()
    }

    // Stress Test
    @Test
    fun largeNumberOfNotes_canBeInserted() = runTest {
        val noteCount = 10000

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
                        "cursus.",
                creationTime = LocalDateTime.now(),
                lastModified = LocalDateTime.now(),
                darkTheme = i % 2 == 0,
                subjectId = null,
                subjectName = null
            )
            noteDao.insertNote(note)
        }

        // Verify
        val allNotes = noteDao.getNotes().first()
        Log.d(tag, "largeNumberOfNotes_canBeInserted, count: ${allNotes.size}")
        assertThat(allNotes.size).isEqualTo(noteCount)
    }

    @Test
    fun largeNumberOfNotesWithSubject_canBeFilteredEfficiently() = runTest {
        val totalNoteCount = 5000
        var notesWithTargetSubject = 0

        val targetSubjectId = 50
        val targetSubjectName = "Target Subject"
        val targetSubject = Subject(
            id = targetSubjectId,
            title = targetSubjectName
        )
        subjectDao.insert(targetSubject)
        val notTargetSubjectId = 25
        val notTargetSubjectName = "Not Target Subject"
        val notTargetSubject = Subject(
            id = notTargetSubjectId,
            title = notTargetSubjectName
        )
        subjectDao.insert(notTargetSubject)

        for (i in 1..totalNoteCount) {
            val useTargetSubject = i % 5 == 0

            if (useTargetSubject) {
                notesWithTargetSubject++
            }

            val note = Note(
                id = i,
                title = "Note $i",
                content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas " +
                        "vehicula volutpat porttitor. Fusce fringilla nisi id diam aliquet, ac " +
                        "suscipit turpis fringilla. Maecenas at tortor blandit, rutrum ante a, " +
                        "feugiat elit. Curabitur sollicitudin felis viverra nisi aliquam scelerisque." +
                        " Quisque finibus purus est, et venenatis odio vehicula at. Donec dignissim " +
                        "hendrerit felis at sollicitudin. Morbi pharetra placerat faucibus.",
                creationTime = LocalDateTime.now(),
                lastModified = LocalDateTime.now(),
                darkTheme = i % 2 == 0,
                subjectId = if (useTargetSubject) targetSubjectId else notTargetSubjectId,
                subjectName = if (useTargetSubject) targetSubjectName else notTargetSubjectName
            )
            noteDao.insertNote(note)
        }

        val allNotesWithSubjects = noteDao.getNotesWithSubjects().first()
        val filteredNotes = allNotesWithSubjects.filter { it.subject?.id == targetSubjectId }

        // Verify
        Log.d(
            tag,
            "largeNumberOfNotesWithSubject_canBeFilteredEfficiently, filtered count: ${filteredNotes.size}"
        )
        assertThat(filteredNotes.size).isEqualTo(notesWithTargetSubject)
    }

    @Test
    fun clearSubjectFromLargeNumberOfNotes_isEfficient() = runTest {
        val noteCount = 5000

        var notesWithTargetSubject = 0

        val targetSubjectId = 50
        val targetSubjectName = "Target Subject"
        val targetSubject = Subject(
            id = targetSubjectId,
            title = targetSubjectName
        )
        subjectDao.insert(targetSubject)
        val notTargetSubjectId = 25
        val notTargetSubjectName = "Not Target Subject"
        val notTargetSubject = Subject(
            id = notTargetSubjectId,
            title = notTargetSubjectName
        )
        subjectDao.insert(notTargetSubject)

        // Insert
        for (i in 1..noteCount) {
            val useTargetSubject = i % 4 == 0

            if (useTargetSubject) {
                notesWithTargetSubject++
            }

            val note = Note(
                id = i,
                title = "Note $i",
                content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas " +
                        "vehicula volutpat porttitor. Fusce fringilla nisi id diam aliquet, ac " +
                        "suscipit turpis fringilla. Maecenas at tortor blandit, rutrum ante a, " +
                        "feugiat elit. Curabitur sollicitudin felis viverra nisi aliquam scelerisque." +
                        " Quisque finibus purus est, et venenatis odio vehicula at.",
                creationTime = LocalDateTime.now(),
                lastModified = LocalDateTime.now(),
                darkTheme = i % 2 == 0,
                subjectId = if (useTargetSubject) targetSubjectId else notTargetSubjectId,
                subjectName = if (useTargetSubject) targetSubjectName else notTargetSubjectName
            )
            noteDao.insertNote(note)
        }

        // Verify
        val initialNotesWithTargetSubject =
            noteDao.getNotes().first().count { it.subjectId == targetSubjectId }
        Log.d(
            tag,
            "clearSubjectFromLargeNumberOfNotes_isEfficient, initial count: $initialNotesWithTargetSubject"
        )
        assertThat(initialNotesWithTargetSubject).isEqualTo(notesWithTargetSubject)

        noteDao.clearSubjectFromNotes(targetSubjectId)

        // Verify
        val finalNotesWithTargetSubject =
            noteDao.getNotes().first().count { it.subjectId == targetSubjectId }
        Log.d(
            tag,
            "clearSubjectFromLargeNumberOfNotes_isEfficient, final count: $finalNotesWithTargetSubject"
        )
        assertThat(finalNotesWithTargetSubject).isEqualTo(0)
    }

    @Test
    fun notesWithVaryingContentSize_canBeHandledEfficiently() = runTest {
        val noteCount = 1000

        for (i in 1..noteCount) {

            val contentRepetitions = when {
                i % 10 == 0 -> 20
                i % 5 == 0 -> 10
                i % 2 == 0 -> 5
                else -> 1
            }

            val contentBuilder = StringBuilder()
            repeat(contentRepetitions) {
                contentBuilder.append(
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas " +
                            "vehicula volutpat porttitor. Fusce fringilla nisi id diam aliquet, ac " +
                            "suscipit turpis fringilla. Maecenas at tortor blandit, rutrum ante a, " +
                            "feugiat elit. Curabitur sollicitudin felis viverra nisi aliquam scelerisque. "
                )
            }

            val note = Note(
                id = i,
                title = "Note $i with ${contentRepetitions}x content",
                content = contentBuilder.toString(),
                creationTime = LocalDateTime.now(),
                lastModified = LocalDateTime.now(),
                darkTheme = i % 2 == 0,
                subjectId = null,
                subjectName = null
            )
            noteDao.insertNote(note)
        }

        val allNotes = noteDao.getNotes().first()
        Log.d(tag, "notesWithVaryingContentSize_canBeHandledEfficiently, count: ${allNotes.size}")

        val smallContentNote = noteDao.getNoteById(1)
        val largeContentNote = noteDao.getNoteById(10)
        val veryLargeContentNote = noteDao.getNoteById(10)

        assertThat(allNotes.size).isEqualTo(noteCount)
        assertThat(smallContentNote).isNotNull()
        assertThat(largeContentNote).isNotNull()
        assertThat(veryLargeContentNote).isNotNull()
    }
}