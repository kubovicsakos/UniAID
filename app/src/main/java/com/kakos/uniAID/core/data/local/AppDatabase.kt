package com.kakos.uniAID.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kakos.uniAID.calendar.data.EventDao
import com.kakos.uniAID.calendar.domain.model.Event
import com.kakos.uniAID.core.data.subject.data.SubjectDao
import com.kakos.uniAID.core.domain.subject.domain.model.Subject
import com.kakos.uniAID.notes.data.NoteDao
import com.kakos.uniAID.notes.domain.model.Note

/**
 * Room database for the application.
 *
 * Serves as the main access point for persistent data storage.
 * Defines database configuration including entities, version,
 * and schema export feature_settings.
 *
 * Provides access to DAOs for all application data entities.
 */
@TypeConverters(Converters::class)
@Database(
    // List of entities
    entities = [
        Note::class,
        Event::class,
        Subject::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    // Abstract functions for each Data Access Object (DAO)
    abstract val noteDao: NoteDao
    abstract fun eventDao(): EventDao
    abstract val subjectDao: SubjectDao

    companion object {
        const val DATABASE_NAME = "app_db" // Name of the database
    }
}