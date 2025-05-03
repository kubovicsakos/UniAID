package com.kakos.uniAID

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kakos.uniAID.calendar.presentation.calendar.CalendarScreen
import com.kakos.uniAID.calendar.presentation.calendar.CalendarViewModel
import com.kakos.uniAID.calendar.presentation.edit_event.EditEventScreen
import com.kakos.uniAID.calendar.presentation.edit_event.EditEventViewModel
import com.kakos.uniAID.calendar.presentation.event_details.EventDetailsScreen
import com.kakos.uniAID.calendar.presentation.event_details.EventDetailsViewModel
import com.kakos.uniAID.core.presentation.Screen
import com.kakos.uniAID.notes.presentation.edit_notes.EditNoteScreen
import com.kakos.uniAID.notes.presentation.edit_notes.EditNoteViewModel
import com.kakos.uniAID.notes.presentation.notes.NotesScreen
import com.kakos.uniAID.notes.presentation.notes.NotesViewModel
import com.kakos.uniAID.settings.presentation.SettingsScreen
import com.kakos.uniAID.settings.presentation.SettingsViewModel
import com.kakos.uniAID.subjectAndStats.presentation.stats.all_statistics.AllStatisticsScreen
import com.kakos.uniAID.subjectAndStats.presentation.stats.all_statistics.AllStatisticsViewModel
import com.kakos.uniAID.subjectAndStats.presentation.stats.statistics.StatisticsScreen
import com.kakos.uniAID.subjectAndStats.presentation.stats.statistics.StatisticsViewModel
import com.kakos.uniAID.subjectAndStats.presentation.subject.edit_subjects.EditSubjectScreen
import com.kakos.uniAID.subjectAndStats.presentation.subject.edit_subjects.EditSubjectViewModel
import com.kakos.uniAID.subjectAndStats.presentation.subject.subject_details.SubjectDetailsScreen
import com.kakos.uniAID.subjectAndStats.presentation.subject.subject_details.SubjectDetailsViewModel
import com.kakos.uniAID.subjectAndStats.presentation.subject.subjects.SubjectsScreen
import com.kakos.uniAID.subjectAndStats.presentation.subject.subjects.SubjectsViewModel
import com.kakos.uniAID.ui.theme.UniAidTheme
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

const val TAG = "MA_MainActivity"
const val transitionSpecIn: Int = 500
const val transitionSpecOut: Int = 250

/**
 * Main entry point for the application.
 *
 * Manages navigation between screens and initializes the app theme
 * based on user preferences.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //TODO: Fix theme change to be in real time not on restart
            val settingsViewModel = hiltViewModel<SettingsViewModel>()
            val settingsState by settingsViewModel.state

            var startDestination by remember { mutableStateOf<Any>(Screen.NotesScreen) }
            var isReady by remember { mutableStateOf(false) }


            // Set the default screen once per app launch (prevents unwanted screen changes)
            LaunchedEffect(Unit) {
                startDestination = settingsViewModel.getDefaultScreenOnce()
                Log.d(TAG, "Default screen is set to $startDestination")
                isReady = true
            }

            if (settingsState.isLoading) {
                Log.d(TAG, "Loading screen is called")
                LoadingScreen()
            } else {
                UniAidTheme(
                    themeMode = settingsState.themeMode,
                    colorScheme = settingsState.colorScheme
                ) {
                    Log.d(
                        TAG,
                        "UniAidTheme is called with $settingsState.themeMode-$settingsState.colorScheme"
                    )
                    Log.d(TAG, "Theme is set to ${settingsState.themeMode}")
                    Log.d(TAG, "Color scheme is set to ${settingsState.colorScheme}")

                    if (!isReady) {
                        Log.d(TAG, "Loading screen is called")
                        LoadingScreen()
                    } else {
                        Surface(color = MaterialTheme.colorScheme.background) {
                            Log.d(TAG, "AppNavHost is called")
                            AppNavHost(startDestination = startDestination)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Main navigation host for the application.
 *
 * Manages navigation between different screens.
 *
 * @param startDestination The initial screen to be displayed.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun AppNavHost(startDestination: Any) {
    val tag = "MA_AppNavHost"
    Log.d(tag, "AppNavHost is displayed with startDestination: $startDestination")
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {


        // Main screens
        composable<Screen.NotesScreen>(
            enterTransition = {
                fadeIn(animationSpec = tween(transitionSpecIn))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(transitionSpecOut))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(transitionSpecIn))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(transitionSpecOut))
            }
        ) {
            val notesViewModel = hiltViewModel<NotesViewModel>()
            val state = notesViewModel.state.value
            val eventFlow = notesViewModel.eventFlow
            val tagNote = "MA_NotesScreen"

            if (state.isLoading) {
                Log.d(tagNote, "Loading screen is displayed")
                LoadingScreen()
            } else {
                Log.d(tagNote, "NotesScreen is displayed")
                NotesScreen(
                    navController = navController,
                    state = state,
                    eventFlow = eventFlow,
                    onEvent = { notesViewModel.onEvent(it) },
                    //navigation logic
                    onAddNote = {
                        Log.d(tagNote, "Try to navigate to EditNoteScreen with no noteId")
                        navController.navigate(Screen.EditNoteScreen())
                    },
                    onEditNote = { noteId ->
                        Log.d(tagNote, "Try to navigate to EditNoteScreen with noteId: $noteId")
                        navController.navigate(Screen.EditNoteScreen(noteId))
                    }
                )
            }
        }
        composable<Screen.CalendarScreen>(
            enterTransition = {
                fadeIn(animationSpec = tween(transitionSpecIn))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(transitionSpecOut))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(transitionSpecIn))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(transitionSpecOut))
            }
        ) {
            val calendarViewModel = hiltViewModel<CalendarViewModel>()
            val state = calendarViewModel.state.value
            val eventFlow = calendarViewModel.eventFlow
            val tagCalendar = "MA_CalendarScreen"

            if (state.isLoading) {
                Log.d(tagCalendar, "Loading screen is displayed")
                LoadingScreen()
            } else {
                Log.d(tagCalendar, "CalendarScreen is displayed")
                CalendarScreen(
                    navController = navController,
                    state = state,
                    eventFlow = eventFlow,
                    eventsByDate = calendarViewModel.eventsByDate.value,
                    onEvent = { calendarViewModel.onEvent(it) },
                    //navigation logic
                    onAddEvent = { startDate ->
                        Log.d(
                            tagCalendar,
                            "Try to navigate to EditEventScreen with startDate: $startDate"
                        )
                        navController.navigate(Screen.EditEventScreen(startDate = startDate))
                    },
                    onEditEvent = { eventId ->
                        Log.d(
                            tagCalendar,
                            "Try to navigate to EditEventScreen with eventId: $eventId"
                        )
                        navController.navigate(Screen.EditEventScreen(eventId = eventId))
                    },
                    onEventDetails = { eventId ->
                        Log.d(
                            tagCalendar,
                            "Try to navigate to EventDetailsScreen with eventId: $eventId"
                        )
                        navController.navigate(Screen.EventDetailsScreen(eventId))
                    }
                )
            }
        }
        composable<Screen.SubjectScreen>(
            enterTransition = {
                fadeIn(animationSpec = tween(transitionSpecIn))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(transitionSpecOut))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(transitionSpecIn))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(transitionSpecOut))
            }
        ) {
            val viewModel = hiltViewModel<SubjectsViewModel>()
            val state = viewModel.state.value
            val eventFlow = viewModel.eventFlow
            val tagSubject = "MA_SubjectScreen"
            if (state.isLoading) {
                Log.d(tagSubject, "Loading screen is displayed")
                LoadingScreen()
            } else {
                Log.d(tagSubject, "SubjectsScreen is displayed")
                SubjectsScreen(
                    navController = navController,
                    state = state,
                    eventFlow = eventFlow,
                    onEvent = viewModel::onEvent,
                    //navigation logic
                    onAddSubject = {
                        Log.d(tagSubject, "Try to navigate to EditSubjectScreen with no subjectId")
                        navController.navigate(Screen.EditSubjectScreen())
                    },
                    onEditSubject = { subjectId ->
                        Log.d(
                            tagSubject,
                            "Try to navigate to EditSubjectScreen with subjectId: $subjectId"
                        )
                        navController.navigate(Screen.EditSubjectScreen(subjectId))
                    },
                    onSubjectDetails = { subjectId ->
                        Log.d(
                            tagSubject,
                            "Try to navigate to SubjectDetailsScreen with subjectId: $subjectId"
                        )
                        navController.navigate(Screen.SubjectDetailsScreen(subjectId))
                    }

                )
            }
        }
        composable<Screen.StatisticsScreen>(
            enterTransition = {
                fadeIn(animationSpec = tween(transitionSpecIn))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(transitionSpecOut))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(transitionSpecIn))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(transitionSpecOut))
            }
        ) {
            val viewModel = hiltViewModel<StatisticsViewModel>()
            val state = viewModel.state.value
            val eventFlow = viewModel.eventFlow
            val tagStat = "MA_StatisticsScreen"

            if (state.isLoading) {
                Log.d(tagStat, "Loading screen is displayed")
                LoadingScreen()
            } else {
                Log.d(tagStat, "StatisticsScreen is displayed")
                StatisticsScreen(
                    navController = navController,
                    state = state,
                    eventFlow = eventFlow,
                    onEvent = viewModel::onEvent,
                    //navigation logic
                    onAllStatistics = {
                        Log.d(tagStat, "Try to navigate to AllStatisticsScreen")
                        navController.navigate(Screen.AllStatisticsScreen)
                    },
                    onSubjectDetails = { subjectId ->
                        Log.d(
                            tagStat,
                            "Try to navigate to SubjectDetailsScreen with subjectId: $subjectId"
                        )
                        navController.navigate(Screen.SubjectDetailsScreen(subjectId))
                    }
                )
            }
        }
        composable<Screen.SettingsScreen>(
            enterTransition = {
                fadeIn(animationSpec = tween(transitionSpecIn))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(transitionSpecOut))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(transitionSpecIn))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(transitionSpecOut))
            }
        ) {
            val viewModel = hiltViewModel<SettingsViewModel>()
            val state = viewModel.state.value
            val eventFlow = viewModel.eventFlow

            val tagSettings = "MA_SettingsScreen"

            if (state.isLoading) {
                Log.d(tagSettings, "Loading screen is displayed")
                LoadingScreen()
            } else {
                Log.d(tagSettings, "SettingsScreen is displayed")
                SettingsScreen(
                    navController = navController,
                    state = state,
                    eventFlow = eventFlow,
                    onEvent = viewModel::onEvent
                )
            }
        }

        // Edit screens
        composable<Screen.EditNoteScreen>(
            enterTransition = {
                fadeIn(animationSpec = tween(transitionSpecIn))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(transitionSpecOut))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(transitionSpecIn))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(transitionSpecOut))
            }
        ) {
            val editNoteViewModel = hiltViewModel<EditNoteViewModel>()
            val state = editNoteViewModel.state.value
            val eventFlow = editNoteViewModel.eventFlow

            val tagEditNote = "MA_EditNoteScreen"

            Log.d(tagEditNote, "EditNoteScreen is displayed")
            EditNoteScreen(
                state = state,
                onEvent = { event ->
                    editNoteViewModel.onEvent(event)
                },
                eventFlow = eventFlow,
                noteId = it.arguments?.getInt("noteId"),
                // navigation logic
                onNavigateBack = {
                    Log.d(tagEditNote, "Try to navigate back")
                    navController.navigateUp()
                }
            )
        }

        composable<Screen.EventDetailsScreen>(
            enterTransition = {
                fadeIn(animationSpec = tween(transitionSpecIn))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(transitionSpecOut))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(transitionSpecIn))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(transitionSpecOut))
            }
        ) {
            val eventDetailsViewModel = hiltViewModel<EventDetailsViewModel>()
            val state = eventDetailsViewModel.state.value
            val eventFlow = eventDetailsViewModel.eventFlow
            val tagEventDetails = "MA_EventDetailsScreen"

            Log.d(tagEventDetails, "EventDetailsScreen is displayed")
            EventDetailsScreen(
                state = state,
                eventFlow = eventFlow,
                onEvent = { onEvent -> eventDetailsViewModel.onEvent(onEvent) },
                eventId = it.arguments?.getInt("eventId"),
                // navigation logic
                onNavigateBack = {
                    Log.d(tagEventDetails, "Try to navigate back")
                    navController.navigateUp()
                },
                onEditEvent = { eventId ->
                    Log.d(
                        tagEventDetails,
                        "Try to navigate to EditEventScreen with eventId: $eventId"
                    )
                    navController.navigate(Screen.EditEventScreen(eventId))
                }
            )
        }

        composable<Screen.EditEventScreen>(
            enterTransition = {
                fadeIn(animationSpec = tween(transitionSpecIn))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(transitionSpecOut))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(transitionSpecIn))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(transitionSpecOut))
            }
        ) {
            val editEventViewModel = hiltViewModel<EditEventViewModel>()
            val state = editEventViewModel.state.value
            val eventFlow = editEventViewModel.eventFlow
            val initialRepeatState = editEventViewModel.initialRepeatState
            val tagEditEvent = "MA_EditEventScreen"

            Log.d(tagEditEvent, "EditEventScreen is displayed")
            EditEventScreen(
                state = state,
                eventFlow = eventFlow,
                initialRepeatState = initialRepeatState,
                onEvent = { onEvent -> editEventViewModel.onEvent(onEvent) },
                eventId = it.arguments?.getInt("eventId"),
                initialStartDate = it.arguments?.getString("startDate")
                    ?.let { date -> LocalDate.parse(date) },
                // navigation logic
                onNavigateBack = {
                    Log.d(tagEditEvent, "Try to navigate back")
                    navController.navigateUp()
                }
            )
        }

        composable<Screen.SubjectDetailsScreen>(
            enterTransition = {
                fadeIn(animationSpec = tween(transitionSpecIn))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(transitionSpecOut))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(transitionSpecIn))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(transitionSpecOut))
            }
        ) {
            val viewModel = hiltViewModel<SubjectDetailsViewModel>()
            val state = viewModel.state.value
            val eventFlow = viewModel.eventFlow
            val tagSubjectDetails = "MA_SubjectDetailsScreen"

            Log.d(tagSubjectDetails, "SubjectDetailsScreen is displayed")
            SubjectDetailsScreen(
                state = state,
                eventFlow = eventFlow,
                onEvent = viewModel::onEvent,
                subjectId = it.arguments?.getInt("subjectId"),
                onNavigateBack = {
                    Log.d(tagSubjectDetails, "Try to navigate back")
                    navController.navigateUp()
                },
                onEditSubject = { subjectId ->
                    Log.d(
                        tagSubjectDetails,
                        "Try to navigate to EditSubjectScreen with subjectId: $subjectId"
                    )
                    navController.navigate(Screen.EditSubjectScreen(subjectId))
                },
                onViewNote = { noteId ->
                    Log.d(
                        tagSubjectDetails,
                        "Try to navigate to EditNoteScreen with noteId: $noteId"
                    )
                    navController.navigate(Screen.EditNoteScreen(noteId))
                },
                onViewEvent = { eventId ->
                    Log.d(
                        tagSubjectDetails,
                        "Try to navigate to EventDetailsScreen with eventId: $eventId"
                    )
                    navController.navigate(Screen.EventDetailsScreen(eventId))
                }
            )
        }

        composable<Screen.EditSubjectScreen>(
            enterTransition = {
                fadeIn(animationSpec = tween(transitionSpecIn))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(transitionSpecOut))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(transitionSpecIn))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(transitionSpecOut))
            }
        ) {
            val viewModel = hiltViewModel<EditSubjectViewModel>()
            val state = viewModel.state.value
            val eventFlow = viewModel.eventFlow
            val tagEditSubject = "MA_EditSubjectScreen"

            Log.d(tagEditSubject, "EditSubjectScreen is displayed")
            EditSubjectScreen(
                state = state,
                eventFlow = eventFlow,
                onEvent = viewModel::onEvent,
                subjectId = it.arguments?.getInt("subjectId"),
                // navigation logic
                onNavigateBack = {
                    Log.d(tagEditSubject, "Try to navigate back")
                    navController.navigateUp()
                }
            )
        }

        composable<Screen.AllStatisticsScreen>(
            enterTransition = {
                fadeIn(animationSpec = tween(transitionSpecIn))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(transitionSpecOut))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(transitionSpecIn))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(transitionSpecOut))
            }
        ) {
            val viewModel = hiltViewModel<AllStatisticsViewModel>()
            val state = viewModel.state.value
            val eventFlow = viewModel.eventFlow
            val tagAllStat = "MA_AllStatisticsScreen"

            Log.d(tagAllStat, "AllStatisticsScreen is displayed")
            AllStatisticsScreen(
                state = state,
                eventFlow = eventFlow,
                //navigation logic
                onNavigateBack = {
                    Log.d(tagAllStat, "Try to navigate back")
                    navController.navigateUp()
                },
            )
        }
    }
}

/**
 * Loading screen displayed while data is being loaded.
 */
@Composable
fun LoadingScreen() {
    val tag = "MA_LoadingScreen"
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Log.d(tag, "Loading screen is displayed")
        CircularProgressIndicator()
    }
}



