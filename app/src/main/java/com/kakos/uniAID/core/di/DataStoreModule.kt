package com.kakos.uniAID.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "feature_settings")

/**
 * Dagger Hilt module for providing DataStore dependencies.
 *
 * Installed in SingletonComponent, provides DataStore-related dependencies
 * for application feature_settings persistence.
 *
 * Responsibilities:
 * - Provides DataStore<Preferences> instance as singleton
 * - Ensures consistent feature_settings access across the application
 */
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @Singleton
    fun provideSettingsDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.settingsDataStore
    }
}