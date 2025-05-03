package com.kakos.uniAID.settings.presentation

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakos.uniAID.core.presentation.Screen
import com.kakos.uniAID.settings.presentation.util.SettingsEvent
import com.kakos.uniAID.settings.presentation.util.SettingsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

/**
 * Screen for application feature_settings configuration.
 *
 * Provides options to customize:
 * - Semester feature_settings (current semester, end date)
 * - Calendar preferences (week start day, default event color)
 * - Theme preferences (mode, color scheme)
 * - General preferences (default screen)
 *
 * @param navController Controls navigation between screens.
 * @param state Current feature_settings state to display.
 * @param onEvent Callback for handling setting change events.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val tag = "SettingsViewModel"

    private val _state = mutableStateOf(SettingsState())
    val state: State<SettingsState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        Log.d(tag, "Initializing")

        viewModelScope.launch(Dispatchers.Main) {
            Log.d(tag, "Loading feature_settings")
            loadSettings()
            Log.d(tag, "Settings loaded")
        }
        Log.d(tag, "Initialization done")
    }

    // Handle events from UI components
    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.SaveCurrentSemester -> {
                viewModelScope.launch {
                    Log.d(tag, "Saving current semester: ${event.semester}")
                    saveCurrentSemester(event.semester)
                }
            }

            is SettingsEvent.SaveWeekStartDay -> {
                viewModelScope.launch {
                    Log.d(tag, "Saving week start day: ${event.day}")
                    saveWeekStartDay(event.day)
                }
            }

            is SettingsEvent.SaveDefaultEventColor -> {
                viewModelScope.launch {
                    Log.d(tag, "Saving default event color: ${event.color}")
                    saveDefaultEventColor(event.color)
                }
            }

            is SettingsEvent.SaveDefaultScreen -> {
                viewModelScope.launch {
                    Log.d(tag, "Saving default screen: ${event.screen}")
                    saveDefaultScreen(event.screen)
                }
            }

            is SettingsEvent.SaveSemesterEndDate -> {
                viewModelScope.launch {
                    Log.d(tag, "Saving semester end date: ${event.date}")
                    saveSemesterEndDate(event.date)
                }
            }

            is SettingsEvent.SaveThemeMode -> {
                viewModelScope.launch {
                    Log.d(tag, "Saving theme mode: ${event.mode}")
                    saveThemeMode(event.mode)
                }
            }

            is SettingsEvent.SaveColorScheme -> {
                viewModelScope.launch {
                    Log.d(tag, "Saving color scheme: ${event.scheme}")
                    saveColorScheme(event.scheme)
                }
            }
        }
    }

    // Load feature_settings from DataStore
//    private suspend fun loadSettings() {
//        try {
//            Log.d(tag, "loadSettings called")
//            // Process data on IO dispatcher
//                val prefs = dataStore.data.first()
//
//                val currentSem = prefs[currentSemesterKey] ?: 1
//                val weekStart = DayOfWeek.valueOf(prefs[weekStartDayKey] ?: "MONDAY")
//                val defaultEvColor = prefs[defaultEventColorKey] ?: 0
//                val savedScreen = prefs[defaultScreenKey] ?: "NotesScreen"
//                val semesterMap = parseSemesterEndDates(prefs[semesterEndDatesKey])
//                val theme = prefs[themeModeKey] ?: "Auto"
//                val colorScheme = prefs[colorSchemeKey] ?: "Auto"
//
//                SettingsState(
//                    currentSemester = currentSem,
//                    weekStartDay = weekStart,
//                    defaultEventColor = defaultEvColor,
//                    defaultScreen = parseScreen(savedScreen),
//                    semesterEndDate = semesterMap[currentSem]?.let { millis ->
//                        Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
//                    },
//                    themeMode = theme,
//                    colorScheme = colorScheme,
//                    isLoading = false
//                )
//
//                _state.value = loadedState
//            }
//            Log.d(tag, "loadSettings complete")
//        } catch (e: Exception) {
//            Log.e(tag, "Exception while loading feature_settings", e)
//            _state.value = _state.value.copy(isLoading = false)
//        }
//    }
    private suspend fun loadSettings() {
        try {
            Log.d(tag, "loadSettings called")
            dataStore.data.map { prefs ->
                val savedScreen = prefs[defaultScreenKey] ?: "NotesScreen"
                val currentSem = prefs[currentSemesterKey] ?: 1
                val map = parseSemesterEndDates(prefs[semesterEndDatesKey])
                SettingsState(
                    currentSemester = currentSem,
                    weekStartDay = DayOfWeek.valueOf(prefs[weekStartDayKey] ?: "MONDAY"),
                    defaultEventColor = prefs[defaultEventColorKey] ?: 0,
                    defaultScreen = parseScreen(savedScreen),
                    semesterEndDate =
                        map[currentSem]?.let { millis ->
                            Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        },
                    themeMode = prefs[themeModeKey] ?: "Auto",
                    colorScheme = prefs[colorSchemeKey] ?: "Auto",
                    isLoading = false
                )
            }.collect { newState ->
                _state.value = newState
            }.also { Log.d(tag, "loadSettings done with values: ${_state.value}") }
        } catch (e: IOException) {
            Log.e(tag, "IOException: While loading feature_settings", e)
            _eventFlow.emit(UiEvent.ShowSnackbar("Failed to load feature_settings"))
        } catch (e: Exception) {
            Log.e(tag, "EXCEPTION: While loading feature_settings", e)
            _eventFlow.emit(UiEvent.ShowSnackbar("Failed to load feature_settings"))
        } catch (e: Error) {
            Log.e(tag, "ERROR: While loading feature_settings", e)
            _eventFlow.emit(UiEvent.ShowSnackbar("Failed to load feature_settings"))
        }
    }

    // Get the default screen once from DataStore
    suspend fun getDefaultScreenOnce(): Any {
        Log.d(tag, "getDefaultScreenOnce called")
        return dataStore.data.map { prefs ->
            parseScreen(prefs[defaultScreenKey] ?: "NotesScreen")
        }.first()
    }

    // Parse the screen object from a string
    private fun parseScreen(value: String): Any {
        Log.d(tag, "parseScreen called with value: $value")
        return when (value) {
            "NotesScreen" -> Screen.NotesScreen
            "CalendarScreen" -> Screen.CalendarScreen
            "SubjectScreen" -> Screen.SubjectScreen
            "StatisticsScreen" -> Screen.StatisticsScreen
            else -> Screen.NotesScreen
        }
    }

    // Parse the semester end dates from a string to a map
    private fun parseSemesterEndDates(data: String?): Map<Int, Long> {
        Log.d(tag, "parseSemesterEndDates called with data: $data")

        // If data is null, return map with default value for semester 1
        if (data == null) {
            return mapOf(
                1 to LocalDate.now().plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
                    .toEpochMilli()
            )
        }

        return try {
            data.takeIf { it.isNotEmpty() }?.split(",")?.associate {
                val (semesterStr, millisStr) = it.split(":")
                semesterStr.toInt() to millisStr.toLong()
            } ?: emptyMap()
        } catch (e: Exception) {
            Log.e(tag, "Exception while parsing semester end dates", e)
            emptyMap()
        } catch (e: Error) {
            Log.e(tag, "Error while parsing semester end dates", e)
            emptyMap()
        }
    }


    // Save the week start day to DataStore
    private fun saveWeekStartDay(day: DayOfWeek) {
        Log.d(tag, "saveWeekStartDay called with day: $day")
        _state.value = _state.value.copy(weekStartDay = day)
        viewModelScope.launch {
            try {
                dataStore.edit { preferences ->
                    preferences[weekStartDayKey] = day.name
                }
                _eventFlow.emit(UiEvent.ShowSnackbar("Week start day saved"))
            } catch (e: IOException) {
                Log.e(tag, "IOException: While saving current semester", e)
                _eventFlow.emit(UiEvent.ShowSnackbar("Failed to save week start day"))
            } catch (e: Exception) {
                Log.e(tag, "EXCEPTION: While saving current semester", e)
                _eventFlow.emit(UiEvent.ShowSnackbar("Failed to save week start day"))
            } catch (e: Error) {
                Log.e(tag, "ERROR: While saving current semester", e)
                _eventFlow.emit(UiEvent.ShowSnackbar("Failed to save week start day"))
            }
        }
    }

    // Save the current semester to DataStore
    private fun saveCurrentSemester(semester: Int) {
        _state.value = _state.value.copy(currentSemester = semester)
        Log.d(tag, "saveCurrentSemester called with semester: $semester")
        viewModelScope.launch {
            try {
                dataStore.edit { preferences ->
                    preferences[currentSemesterKey] = semester
                }
                _eventFlow.emit(UiEvent.ShowSnackbar("Current semester saved"))
            } catch (e: IOException) {
                Log.e(tag, "IOException: While saving current semester", e)
                _eventFlow.emit(UiEvent.ShowSnackbar("Failed to save current semester"))
            } catch (e: Exception) {
                Log.e(tag, "EXCEPTION: While current semester", e)
                _eventFlow.emit(UiEvent.ShowSnackbar("Failed to save current semester"))
            } catch (e: Error) {
                Log.e(tag, "ERROR: While saving current semester", e)
                _eventFlow.emit(UiEvent.ShowSnackbar("Failed to save current semester"))
            }
        }
    }

    // Save the default screen to DataStore
    private fun saveDefaultScreen(screen: Any) {
        Log.d(tag, "saveDefaultScreen called with screen: $screen")
        _state.value = _state.value.copy(defaultScreen = screen)
        viewModelScope.launch {
            try {
                val screenName = when (screen) {
                    Screen.NotesScreen -> "NotesScreen"
                    Screen.CalendarScreen -> "CalendarScreen"
                    Screen.SubjectScreen -> "SubjectScreen"
                    Screen.StatisticsScreen -> "StatisticsScreen"
                    else -> "NotesScreen"
                }
                dataStore.edit { preferences ->
                    preferences[defaultScreenKey] = screenName
                }
                _eventFlow.emit(UiEvent.ShowSnackbar("Default start screen will change after app restart"))
            } catch (e: IOException) {
                Log.e(tag, "IOException: While saving default screen", e)
                _eventFlow.emit(UiEvent.ShowSnackbar("Failed to save default screen"))
            } catch (e: Exception) {
                Log.e(tag, "EXCEPTION: While saving default screen", e)
                _eventFlow.emit(UiEvent.ShowSnackbar("Failed to save default screen"))
            } catch (e: Error) {
                Log.e(tag, "ERROR: While saving default screen", e)
                _eventFlow.emit(UiEvent.ShowSnackbar("Failed to save default screen"))
            }
        }
    }

    // Save the end date of the current semester to DataStore
    private fun saveSemesterEndDate(date: LocalDate?) {
        Log.d(tag, "saveSemesterEndDate called with date: $date")
        _state.value = _state.value.copy(semesterEndDate = date)

        viewModelScope.launch {
            try {
                dataStore.edit { preferences ->
                    val current = _state.value.currentSemester
                    val currentMap = parseSemesterEndDates(preferences[semesterEndDatesKey])
                    val newMap = currentMap.toMutableMap().apply {
                        if (date != null) {
                            val millis =
                                date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                            put(current, millis)
                        } else {
                            remove(current)
                        }
                    }
                    preferences[semesterEndDatesKey] = newMap.entries.joinToString(",") {
                        "${it.key}:${it.value}"
                    }
                }
                _eventFlow.emit(UiEvent.ShowSnackbar("Semester end date saved"))
            } catch (e: IOException) {
                Log.e(tag, "IOException: While saving semester end date", e)
                _eventFlow.emit(UiEvent.ShowSnackbar("Failed to save semester end date"))
            } catch (e: Exception) {
                Log.e(tag, "EXCEPTION: While saving semester end date", e)
                _eventFlow.emit(UiEvent.ShowSnackbar("Failed to save semester end date"))
            } catch (e: Error) {
                Log.e(tag, "ERROR: While saving semester end date", e)
                _eventFlow.emit(UiEvent.ShowSnackbar("Failed to save semester end date"))
            }
        }
    }

    // Save the selected theme mode to DataStore
    private fun saveThemeMode(mode: String) {
        Log.d(tag, "saveThemeMode called with mode: $mode")
        _state.value = _state.value.copy(themeMode = mode)
        viewModelScope.launch {
            try {
                dataStore.edit { preferences ->
                    preferences[themeModeKey] = mode
                }
                _eventFlow.emit(
                    UiEvent.ThemeChanged(
                        _state.value.themeMode,
                        _state.value.colorScheme
                    )
                )
                _eventFlow.emit(UiEvent.ShowSnackbar("Theme mode saved"))
            } catch (e: Exception) {
                Log.e(tag, "Exception while saving theme mode", e)
            }
        }
    }

    // Save the selected color scheme to DataStore
    private fun saveColorScheme(scheme: String) {
        Log.d(tag, "saveColorScheme called with scheme: $scheme")
        _state.value = _state.value.copy(colorScheme = scheme)
        viewModelScope.launch {
            try {
                dataStore.edit { preferences ->
                    preferences[colorSchemeKey] = scheme
                }
                _eventFlow.emit(
                    UiEvent.ThemeChanged(
                        _state.value.themeMode,
                        _state.value.colorScheme
                    )
                )
                _eventFlow.emit(UiEvent.ShowSnackbar("Color scheme saved"))
            } catch (e: Exception) {
                Log.e(tag, "Exception while saving color scheme", e)
            }
        }
    }

    // Save the default event color to DataStore
    private fun saveDefaultEventColor(color: Int) {
        Log.d(tag, "saveDefaultEventColor called with color: $color")
        _state.value = _state.value.copy(defaultEventColor = color)
        viewModelScope.launch {
            try {
                dataStore.edit { preferences ->
                    preferences[defaultEventColorKey] = color
                }
                _eventFlow.emit(UiEvent.ShowSnackbar("Default event color saved"))
            } catch (e: IOException) {
                Log.e(tag, "IOException: While saving default event color", e)
                _eventFlow.emit(UiEvent.ShowSnackbar("Failed to save default event color"))
            } catch (e: Exception) {
                Log.e(tag, "EXCEPTION: While saving default event color", e)
                _eventFlow.emit(UiEvent.ShowSnackbar("Failed to save default event color"))
            } catch (e: Error) {
                Log.e(tag, "ERROR: While saving default event color", e)
                _eventFlow.emit(UiEvent.ShowSnackbar("Failed to save default event color"))
            }
        }
    }

    // Keys for storing feature_settings in DataStore
    companion object {
        val currentSemesterKey = intPreferencesKey("current_semester")
        val weekStartDayKey = stringPreferencesKey("week_start_day")
        val defaultEventColorKey = intPreferencesKey("event_color")
        val defaultScreenKey = stringPreferencesKey("default_screen")
        val semesterEndDatesKey = stringPreferencesKey("semester_end_dates")
        val themeModeKey = stringPreferencesKey("theme_mode")
        val colorSchemeKey = stringPreferencesKey("color_scheme")
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        data class ThemeChanged(val mode: String, val scheme: String) : UiEvent()
    }
}
