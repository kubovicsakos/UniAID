package com.kakos.uniAID.core.di

import android.content.Context
import androidx.room.Room
import com.kakos.uniAID.calendar.data.repository.EventRepositoryImpl
import com.kakos.uniAID.calendar.domain.repository.EventRepository
import com.kakos.uniAID.core.data.local.AppDatabase
import com.kakos.uniAID.core.data.subject.data.repository.SubjectRepositoryImpl
import com.kakos.uniAID.core.domain.subject.domain.repository.SubjectRepository
import com.kakos.uniAID.notes.data.repository.NoteRepositoryImpl
import com.kakos.uniAID.notes.domain.repository.NoteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


/**
 * Dagger Hilt module for providing dependencies.
 *
 * Installed in SingletonComponent, provides database and repository dependencies
 * for the application.
 *
 * Responsibilities:
 * - Provides AppDatabase instance
 * - Provides repository implementations as singletons
 * - Ensures consistent data access across the application
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase { // Database provider
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideNotesRepository(db: AppDatabase): NoteRepository { // NotesRepository provider
        return NoteRepositoryImpl(db.noteDao)
    }

    @Provides
    @Singleton
    fun provideEventsRepository(db: AppDatabase): EventRepository { // EventsRepository provider
        return EventRepositoryImpl(db.eventDao())
    }

    @Provides
    @Singleton
    fun provideSubjectRepository(db: AppDatabase): SubjectRepository { // SubjectRepository provider
        return SubjectRepositoryImpl(db.subjectDao, db.eventDao(), db.noteDao)
    }

}