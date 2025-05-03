package com.kakos.uniAID.settings.presentation

import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.kakos.uniAID.core.presentation.NavDrawer
import com.kakos.uniAID.core.presentation.WindowType
import com.kakos.uniAID.core.presentation.components.buttons.MenuButton
import com.kakos.uniAID.core.presentation.components.dropdowns.NumberPickerDropdown
import com.kakos.uniAID.core.presentation.components.texts.ScreenTitleText
import com.kakos.uniAID.core.presentation.rememberWindowSize
import com.kakos.uniAID.settings.presentation.components.ColorSchemePicker
import com.kakos.uniAID.settings.presentation.components.DefaultEventColorPicker
import com.kakos.uniAID.settings.presentation.components.DefaultStartScreenPicker
import com.kakos.uniAID.settings.presentation.components.SemesterDatePickerDialog
import com.kakos.uniAID.settings.presentation.components.SettingItem
import com.kakos.uniAID.settings.presentation.components.ThemePicker
import com.kakos.uniAID.settings.presentation.components.WeekStartDayPicker
import com.kakos.uniAID.settings.presentation.util.SettingsEvent
import com.kakos.uniAID.settings.presentation.util.SettingsState
import com.kakos.uniAID.ui.theme.UniAidTheme
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Screen for application feature_settings configuration.
 *
 * Provides UI for customizing:
 * - Semester feature_settings (current semester, end date)
 * - Calendar preferences (week start day, default event color)
 * - Theme preferences (mode, color scheme)
 * - General preferences (default start screen)
 *
 * @param navController Controls navigation between screens.
 * @param state Current feature_settings state to display.
 * @param onEvent Callback for handling setting change events.
 */
@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalAnimationApi
@Composable
fun SettingsScreen(
    navController: NavController,
    state: SettingsState,
    eventFlow: SharedFlow<SettingsViewModel.UiEvent>,
    onEvent: (SettingsEvent) -> Unit
) {
    val tag = "SettingsScreen"

    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val windowSize = rememberWindowSize()
    val dateSize = when (windowSize.width) {
        WindowType.Compact -> false
        WindowType.Medium -> true
        WindowType.Expanded -> true
    }
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var showSemesterDatePickerDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        Log.d(tag, "LaunchedEffect launched")
        eventFlow.collectLatest { event ->
            when (event) {
                is SettingsViewModel.UiEvent.ShowSnackbar -> {
                    Log.d(tag, "Ui Event Show snackbar: ${event.message}")
                    snackbarHostState.showSnackbar(message = event.message)
                }

                else -> {
                    Log.d(tag, "Ui Event: $event")
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (drawerState.isOpen) {
            drawerState.close()
        }
    }

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(8.dp))
                NavDrawer(
                    navController = navController,
                    drawerState = drawerState
                )
            }
        },
        drawerState = drawerState,

        ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    navigationIcon = {
                        MenuButton { scope.launch { drawerState.open() } }
                    },
                    title = {
                        ScreenTitleText(title = "Settings")
                    },
                    scrollBehavior = scrollBehavior
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { values ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(values)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Semester",
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                }
                item {
                    SettingItem(
                        title = "Current Semester",
                        content = {
                            NumberPickerDropdown(
                                lowerBound = 1,
                                upperBound = 99,
                                defaultNumber = state.currentSemester,
                                onNumberSelected = { onEvent(SettingsEvent.SaveCurrentSemester(it)) },
                                modifier = Modifier
                                    .width(100.dp)
                            )
                        }
                    )
                }
                item {
                    SettingItem(
                        title = "Semester end date",
                        content = {
                            Button(
                                modifier = Modifier.width(150.dp),
                                onClick = { showSemesterDatePickerDialog = true }
                            ) {
                                Text(text = state.semesterEndDate?.toString() ?: "Pick date")
                            }
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Calendar",
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                }
                item {
                    SettingItem(
                        title = "Week Start Day",
                        content = {
                            WeekStartDayPicker(
                                selectedDay = state.weekStartDay,
                                onDaySelected = { onEvent(SettingsEvent.SaveWeekStartDay(it)) },
                                modifier = Modifier.width(100.dp)
                            )
                        }
                    )
                }
                item {
                    SettingItem(
                        title = "Default Event Color",
                        content = {
                            DefaultEventColorPicker(
                                selectedColor = state.defaultEventColor,
                                onColorSelected = { onEvent(SettingsEvent.SaveDefaultEventColor(it)) },
                                modifier = Modifier.width(150.dp)
                            )
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Theme",
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                }
                item {
                    SettingItem(
                        title = "Theme",
                        content = {
                            ThemePicker(
                                selectedTheme = state.themeMode,
                                onThemeSelected = { mode ->
                                    onEvent(SettingsEvent.SaveThemeMode(mode))
                                },
                                modifier = Modifier.width(100.dp)
                            )
                        }
                    )
                }
                item {
                    SettingItem(
                        title = "Color Scheme",
                        content = {
                            ColorSchemePicker(
                                selectedScheme = state.colorScheme,
                                onSchemeSelected = { scheme ->
                                    onEvent(SettingsEvent.SaveColorScheme(scheme))
                                },
                                modifier = Modifier.width(150.dp)
                            )
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "General",
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                }
                item {
                    SettingItem(
                        title = "Default Start Screen",
                        content = {
                            DefaultStartScreenPicker(
                                selectedScreen = state.defaultScreen,
                                onScreenSelected = {
                                    onEvent(SettingsEvent.SaveDefaultScreen(it))
                                },
                                modifier = Modifier.width(150.dp)
                            )
                        }
                    )
                }

            }
        }
    }
    if (showSemesterDatePickerDialog) {
        SemesterDatePickerDialog(
            onDismiss = { showSemesterDatePickerDialog = false },
            selectedDate = state.semesterEndDate,
            onDateSelected = { date ->
                onEvent(SettingsEvent.SaveSemesterEndDate(date))
                showSemesterDatePickerDialog = false
            },
            isLandScape = dateSize
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Preview
@Composable
private fun SettingsScreenP() {
    UniAidTheme(
        themeMode = "Auto"
    ) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize()
        ) {
            SettingsScreen(
                navController = rememberNavController(),
                state = SettingsState(),
                onEvent = { },
                eventFlow = MutableSharedFlow()
            )
        }
    }
}