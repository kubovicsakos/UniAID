package com.kakos.uniAID.settings.presentation.util

import com.kakos.uniAID.core.presentation.Screen
import java.time.DayOfWeek
import java.time.LocalDate

/**
 * Represents UI state for feature_settings screen.
 *
 * @property currentSemester Current academic semester number.
 * @property weekStartDay Day of week that starts the weekly view.
 * @property defaultEventColor Default color used for new events.
 * @property defaultScreen Default screen shown when app launches.
 * @property semesterEndDate End date of current semester.
 * @property themeMode Selected theme mode (Auto, Light, Dark).
 * @property colorScheme Selected color scheme.
 * @property isLoading Indicates if feature_settings are being loaded.
 */
data class SettingsState(
    val currentSemester: Int = 1,
    val weekStartDay: DayOfWeek = DayOfWeek.MONDAY,
    val defaultEventColor: Int = 0,
    val defaultScreen: Any = Screen.NotesScreen,
    val semesterEndDate: LocalDate? = null,
    val themeMode: String = "Auto",
    val colorScheme: String = "Auto",
    val isLoading: Boolean = true
)
