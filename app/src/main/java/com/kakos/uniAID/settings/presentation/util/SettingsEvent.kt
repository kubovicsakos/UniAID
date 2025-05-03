package com.kakos.uniAID.settings.presentation.util

import java.time.DayOfWeek
import java.time.LocalDate

/**
 * Sealed class representing events for feature_settings.
 *
 * Defines possible user interactions or system events
 * that can occur within the feature_settings feature.
 */
sealed class SettingsEvent {
    data class SaveCurrentSemester(val semester: Int) : SettingsEvent()
    data class SaveWeekStartDay(val day: DayOfWeek) : SettingsEvent()
    data class SaveDefaultEventColor(val color: Int) : SettingsEvent()
    data class SaveDefaultScreen(val screen: Any) : SettingsEvent()
    data class SaveSemesterEndDate(val date: LocalDate?) : SettingsEvent()
    data class SaveThemeMode(val mode: String) : SettingsEvent()
    data class SaveColorScheme(val scheme: String) : SettingsEvent()
}