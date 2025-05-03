package com.kakos.uniAID.calendar.presentation.edit_event


import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kakos.uniAID.calendar.domain.model.Repeat
import com.kakos.uniAID.calendar.domain.util.SaveOption
import com.kakos.uniAID.calendar.presentation.edit_event.components.CalenderPickerSwitch
import com.kakos.uniAID.calendar.presentation.edit_event.components.EditEventTextField
import com.kakos.uniAID.calendar.presentation.edit_event.components.WeeklyRepeatDaySelector
import com.kakos.uniAID.calendar.presentation.edit_event.components.dialogs.CalendarDatePickerDialog
import com.kakos.uniAID.calendar.presentation.edit_event.components.dialogs.CalendarTimePickerDialog
import com.kakos.uniAID.calendar.presentation.edit_event.components.dialogs.SaveEventDialog
import com.kakos.uniAID.calendar.presentation.edit_event.components.dropdowns.CalendarColorPickerDropdown
import com.kakos.uniAID.calendar.presentation.edit_event.components.dropdowns.CalendarDifferencePickerDropdown
import com.kakos.uniAID.calendar.presentation.edit_event.components.dropdowns.CalendarRepeatOptionsDropdown
import com.kakos.uniAID.calendar.presentation.edit_event.components.dropdowns.CalendarSubjectPickerDropdown
import com.kakos.uniAID.calendar.presentation.edit_event.util.EditCalendarEventEvent
import com.kakos.uniAID.calendar.presentation.edit_event.util.EditEventState
import com.kakos.uniAID.core.presentation.WindowType
import com.kakos.uniAID.core.presentation.components.buttons.CancelButton
import com.kakos.uniAID.core.presentation.components.buttons.DefaultButton
import com.kakos.uniAID.core.presentation.rememberWindowSize
import com.kakos.uniAID.ui.theme.UniAidTheme
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter


/**
 * Screen for calendar event creation and editing.
 *
 * Provides a comprehensive form interface for managing event details including
 * title, date/time feature_settings, recurrence patterns, subject association,
 * visual styling, and additional metadata with validation feedback.
 *
 * @param state Current UI state containing all form field values and validation status.
 * @param onEvent Callback for dispatching user actions to the ViewModel.
 * @param initialRepeatState Original recurrence pattern when editing an existing event.
 * @param initialStartDate Starting date for the event, defaults to current date.
 * @param eventId Identifier of event being edited, or null when creating new event.
 * @param onNavigateBack Callback to return to previous screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEventScreen(
    state: EditEventState,
    eventFlow: SharedFlow<EditEventViewModel.UiEvent>,
    onEvent: (EditCalendarEventEvent) -> Unit,
    initialRepeatState: Repeat,
    // passed on nav
    initialStartDate: LocalDate? = LocalDate.now(),
    eventId: Int?,
    // navigation
    onNavigateBack: () -> Unit,
) {
    val tag = "EditEventScreen"
    // to prevent screen recomposition on screen rotation
    var isInitialized by remember { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }
    val windowSize = rememberWindowSize()
    val scope = rememberCoroutineScope()

    LaunchedEffect(eventId) {
        Log.d(
            tag,
            "LaunchedEffect called, with eventId: $eventId and isInitialized: $isInitialized"
        )
        if (!isInitialized) {
            scope.launch {
                Log.d(tag, "LaunchedEffect called, with eventId: $eventId")
                onEvent(EditCalendarEventEvent.GetEventById(eventId ?: -1))
            }

            if (eventId == -1) {
                // Set initial color from default value
                Log.d(tag, "getting default event color")
                onEvent(EditCalendarEventEvent.GetDefaultEventColor)
                Log.d(tag, "new default event color: ${state.color}")
                // Set initial dates from navigation arguments
                Log.d(tag, "setting initial dates with startDate: $initialStartDate")
                onEvent(
                    EditCalendarEventEvent.InitializeStartDate(
                        initialStartDate ?: LocalDate.now()
                    )
                )
                Log.d(tag, "new StartDate: ${state.startDate}")
                onEvent(
                    EditCalendarEventEvent.InitializeEndDate(
                        initialStartDate ?: LocalDate.now()
                    )
                )
                Log.d(tag, "new EndDate: ${state.endDate}")
                Log.d(tag, "setting initial repeat end date")
                onEvent(EditCalendarEventEvent.SetCurrentSemesterEndDate)
                Log.d(tag, "new RepeatEndDate: ${state.repeatEndDate}")
            }
        }
        isInitialized = true
        Log.d(tag, "LaunchedEffect finished")
    }

    val dateTimeSize = when (windowSize.height) {
        WindowType.Compact -> {
            Log.d(tag, "WindowType is Compact")
            true
        }

        WindowType.Medium -> {
            Log.d(tag, "WindowType is Medium")
            false
        }

        WindowType.Expanded -> {
            Log.d(tag, "WindowType is Expanded")
            false
        }
    }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    var showRepeatEndDatePicker by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }



    LaunchedEffect(key1 = true) {
        eventFlow.collectLatest { event ->
            when (event) {
                is EditEventViewModel.UiEvent.ShowSnackbar -> {
                    Log.d(tag, "Showing snackbar: ${event.message}")
                    snackbarHostState.showSnackbar(message = event.message)
                }

                is EditEventViewModel.UiEvent.SaveEvent -> {
                    Log.d(tag, "Event saved successfully")
                    onNavigateBack()
                }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                navigationIcon = {
                    CancelButton { onNavigateBack() }
                },
                title = { Text(text = "") },
                actions = {
                    DefaultButton(
                        text = "Save",
                        onClick = {
                            Log.d(tag, "Save button clicked")
                            if (state.repeat != Repeat.NONE && initialRepeatState == Repeat.NONE) {
                                // if the event is initially not repeating and the user changes it to repeating, its without dialog
                                onEvent(EditCalendarEventEvent.SaveEvent(SaveOption.THIS_AND_FUTURE))
                                Log.d(tag, "Saving with: ${SaveOption.THIS_AND_FUTURE.name}")
                            } else if (state.repeat == Repeat.NONE) {
                                // if the event is not repeating, save it without dialog
                                onEvent(EditCalendarEventEvent.SaveEvent(SaveOption.THIS))
                                Log.d(tag, "Saving with: ${SaveOption.THIS.name}")
                            } else {
                                // if the event is repeating initially, show the dialog
                                Log.d(tag, "Saving with: ${SaveOption.ALL.name}")
                                showSaveDialog = true
                            }
                        }
                    )
                },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { values ->
        LazyColumn(
            modifier = Modifier
                .padding(values)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            // TITLE
            item {
                EditEventTextField(
                    modifier = Modifier.padding(16.dp),
                    text = state.title.text,
                    label = "Title",
                    singleLine = false,
                    error = state.title.error,
                    onValueChange = {
                        Log.d(tag, "TitleTextField clicked")
                        onEvent(EditCalendarEventEvent.EnteredTitle(it))
                    }
                )
            }
            // ALL DAY
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 8.dp),
                        text = "All day",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    CalenderPickerSwitch(
                        modifier = Modifier
                            .padding(horizontal = 8.dp),
                        checked = state.allDay,
                        onCheckedChange = {
                            Log.d(tag, "All day switch clicked")
                            onEvent(EditCalendarEventEvent.EnteredAllDay(it))
                        }
                    )
                }
            }
            // START
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(
                            if (state.dateTimeError != null) MaterialTheme.colorScheme.errorContainer
                            else MaterialTheme.colorScheme.background
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Button(
                        modifier = Modifier
                            .padding(horizontal = 8.dp),
                        onClick = {
                            Log.d(tag, "Start date button clicked")
                            showStartDatePicker = true
                        },
                    ) {
                        Text(text = state.startDate.toString())
                    }
                    if (!state.allDay) {
                        Button(
                            modifier = Modifier
                                .padding(horizontal = 8.dp),
                            onClick = {
                                Log.d(tag, "Start time button clicked")
                                showStartTimePicker = true
                            },
                        ) {
                            Text(text = state.startTime.format(DateTimeFormatter.ofPattern("HH:mm")))
                        }
                    }
                }
            }
            item {
                state.dateTimeError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
            // END
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Button(
                        modifier = Modifier
                            .padding(horizontal = 8.dp),
                        onClick = {
                            Log.d(tag, "End date button clicked")
                            showEndDatePicker = true
                        },
                    ) {
                        Text(text = state.endDate.toString())
                    }
                    if (!state.allDay) {
                        Button(
                            modifier = Modifier
                                .padding(horizontal = 8.dp),
                            onClick = {
                                Log.d(tag, "End time button clicked")
                                showEndTimePicker = true
                            },
                        ) {
                            Text(text = state.endTime.format(DateTimeFormatter.ofPattern("HH:mm")))
                        }
                    }
                }
            }
            // REPEAT
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "Repeat ",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                        )
                        CalendarRepeatOptionsDropdown(
                            modifier = Modifier
                                .padding(horizontal = 8.dp),
                            selectedRepeat = state.repeat,
                            onRepeatSelected = {
                                Log.d(tag, "Repeat selection clicked")
                                onEvent(
                                    EditCalendarEventEvent.EnteredRepeat(
                                        it
                                    )
                                )
                            }
                        )
                    }
                    AnimatedVisibility(
                        visible = state.repeat != Repeat.NONE,
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Text(
                                    text = "Every ",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp)
                                )
                                CalendarDifferencePickerDropdown(
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp),
                                    selectedNumber = state.repeatDifference,
                                    onNumberSelected = {
                                        onEvent(
                                            EditCalendarEventEvent.EnteredRepeatDifference(it)
                                        )
                                    }
                                )
                                Text(
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    text = when (state.repeat) {
                                        Repeat.DAILY -> "day"
                                        Repeat.WEEKLY -> "week"
                                        Repeat.MONTHLY -> "month"
                                        Repeat.YEARLY -> "year"
                                        else -> ""
                                    },

                                    )
                            }
                            AnimatedVisibility(
                                visible = state.repeat == Repeat.WEEKLY
                            ) {
                                WeeklyRepeatDaySelector(
                                    initialSelectedDays = state.selectedDays,
                                    onSelectedDaysChanged = { updatedDays ->
                                        onEvent(
                                            EditCalendarEventEvent.EnteredSelectedDays(updatedDays)
                                        )
                                    }
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clip(MaterialTheme.shapes.medium)
                                    .background(
                                        if (state.repeatError != null) MaterialTheme.colorScheme.errorContainer
                                        else MaterialTheme.colorScheme.background
                                    ),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Text(
                                    text = "Repeat End Date: ",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp)
                                )
                                Button(
                                    onClick = {
                                        Log.d(tag, "Repeat end date button clicked")
                                        showRepeatEndDatePicker = true
                                    }
                                ) {
                                    Text(text = state.repeatEndDate.toString())
                                }
                            }
                        }
                    }

                }
            }
            // REPEAT ERROR
            item {
                state.repeatError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
            // SUBJECT
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Subject ",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                    )
                    // In the SUBJECT item section
                    CalendarSubjectPickerDropdown(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        subjectName = state.subjectName,
                        filteredSubjects = state.filteredSubjects,
                        onSubjectSelected = {
                            Log.d(tag, "Subject selection clicked")
                            onEvent(EditCalendarEventEvent.EnteredSubject(it))
                        }
                    )
                }
            }
            // COLOR
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Color ",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                    )
                    CalendarColorPickerDropdown(
                        modifier = Modifier
                            .padding(horizontal = 8.dp),
                        selectedColor = state.color,
                        onColorSelected = {
                            Log.d(tag, "Color selection clicked")
                            onEvent(EditCalendarEventEvent.EnteredColor(it))
                        }
                    )
                }
            }
            // LOCATION
            item {
                EditEventTextField(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    text = state.location.text,
                    label = "Location",
                    singleLine = false,
                    onValueChange = {
                        Log.d(tag, "LocationTextField clicked")
                        onEvent(EditCalendarEventEvent.EnteredLocation(it))
                    }
                )
            }
            // DESCRIPTION
            item {
                EditEventTextField(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    text = state.description.text,
                    label = "Description",
                    singleLine = false,
                    onValueChange = {
                        Log.d(tag, "DescriptionTextField clicked")
                        onEvent(EditCalendarEventEvent.EnteredDescription(it))
                    }
                )
            }
        }
    }

    // Dialogs

    if (showStartTimePicker) {
        Log.d(tag, "Displaying start time picker")
        CalendarTimePickerDialog(
            selectedTime = state.startTime,
            onConfirm = {
                onEvent(EditCalendarEventEvent.EnteredStartTime(it))
                Log.d(tag, "Start time selected: $it")
                showStartTimePicker = false
            },
            onDismiss = {
                showStartTimePicker = false
                Log.d(tag, "Start time dialog dismissed")
            },
            isLandScape = dateTimeSize
        )
    }

    if (showEndTimePicker) {
        Log.d(tag, "Displaying end time picker")
        CalendarTimePickerDialog(
            selectedTime = state.endTime,
            onConfirm = {
                onEvent(EditCalendarEventEvent.EnteredEndTime(it))
                Log.d(tag, "End time selected: $it")
                showEndTimePicker = false
            },
            onDismiss = {
                showEndTimePicker = false
                Log.d(tag, "End time dialog dismissed")
            },
            isLandScape = dateTimeSize
        )
    }

    if (showStartDatePicker) {
        Log.d(tag, "Displaying start date picker")
        CalendarDatePickerDialog(
            selectedDate = state.startDate,
            onDateSelected = {
                onEvent(EditCalendarEventEvent.EnteredStartDate(it ?: LocalDate.now()))
                Log.d(tag, "Start date selected: $it")
                showStartDatePicker = false
            },
            onDismiss = {
                showStartDatePicker = false
                Log.d(tag, "Start date dialog dismissed")
            },
            isLandScape = dateTimeSize
        )
    }

    if (showEndDatePicker) {
        Log.d(tag, "Displaying end date picker")
        CalendarDatePickerDialog(
            selectedDate = state.endDate,
            onDateSelected = {
                onEvent(EditCalendarEventEvent.EnteredEndDate(it ?: LocalDate.now()))
                Log.d(tag, "End date selected: $it")
                showEndDatePicker = false
            },
            onDismiss = {
                showEndDatePicker = false
                Log.d(tag, "End date dialog dismissed")
            },
            isLandScape = dateTimeSize
        )
    }

    if (showRepeatEndDatePicker) {
        Log.d(tag, "Displaying repeat end date picker")
        CalendarDatePickerDialog(
            selectedDate = state.repeatEndDate,
            onDateSelected = {
                onEvent(
                    EditCalendarEventEvent.EnteredRepeatEndDate(
                        it ?: LocalDate.now()
                    )
                )
                showRepeatEndDatePicker = false
                Log.d(tag, "Repeat end date selected: $it")
            },
            onDismiss = {
                showRepeatEndDatePicker = false
                Log.d(tag, "Repeat end date dialog dismissed")
            },
            isLandScape = dateTimeSize
        )
    }

    if (showSaveDialog) {
        Log.d(tag, "Displaying save dialog")
        SaveEventDialog(
            onDismiss = { showSaveDialog = false },
            onSave = { saveOption ->
                onEvent(EditCalendarEventEvent.SaveEvent(saveOption))
                Log.d(tag, "Saving with: ${saveOption.name}")
                showSaveDialog = false
            }
        )
    }
}

@Preview(
    widthDp = 500,
    heightDp = 610,
)
@Composable
private fun EditEventP() {
    UniAidTheme {
        Surface {
            EditEventScreen(
                state = EditEventState(),
                onEvent = {},
                initialRepeatState = Repeat.NONE,
                initialStartDate = LocalDate.now(),
                eventId = null,
                onNavigateBack = {},
                eventFlow = MutableSharedFlow(),
            )
        }
    }
}