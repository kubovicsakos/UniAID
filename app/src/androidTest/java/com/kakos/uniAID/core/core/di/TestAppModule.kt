package com.kakos.uniAID.core.core.di

import android.content.Context
import androidx.room.Room
import com.kakos.uniAID.core.data.local.AppDatabase
import com.kakos.uniAID.core.features.subject.data.repository.FakeAndroidSubjectRepository
import com.kakos.uniAID.core.domain.subject.domain.repository.SubjectRepository
import com.kakos.uniAID.calendar.data.repository.FakeAndroidEventRepository
import com.kakos.uniAID.calendar.domain.repository.EventRepository
import com.kakos.uniAID.notes.data.repository.FakeAndroidNoteRepository
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
object TestAppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).build()
    }

    @Provides
    @Singleton
    fun provideNotesRepository(): NoteRepository {
        return FakeAndroidNoteRepository()
    }

    @Provides
    @Singleton
    fun provideSubjectRepository(): SubjectRepository {
        return FakeAndroidSubjectRepository()
    }

    @Provides
    @Singleton
    fun provideEventRepository(): EventRepository {
        return FakeAndroidEventRepository()
    }
}