package com.kakos.uniAID.calendar.presentation.calendar


import android.util.Log
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kakos.uniAID.R
import com.kakos.uniAID.calendar.domain.model.Event
import com.kakos.uniAID.calendar.presentation.calendar.monthly_calendar.MonthlyCalendar
import com.kakos.uniAID.calendar.presentation.calendar.util.CalendarEventState
import com.kakos.uniAID.calendar.presentation.calendar.util.CalendarEventsEvent
import com.kakos.uniAID.core.presentation.NavDrawer
import com.kakos.uniAID.core.presentation.WindowSize
import com.kakos.uniAID.core.presentation.WindowType
import com.kakos.uniAID.core.presentation.components.buttons.MenuButton
import com.kakos.uniAID.core.presentation.components.buttons.floating_button.AddFloatingActionButton
import com.kakos.uniAID.core.presentation.components.texts.ScreenTitleText
import com.kakos.uniAID.core.presentation.rememberWindowSize
import com.kakos.uniAID.ui.theme.UniAidTheme
import com.kizitonwose.calendar.compose.CalendarState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter


/**
 * Composable function to display the calendar screen.
 *
 * Provides a full-featured calendar interface with monthly navigation,
 * daily event listings, and responsive layout adjustments based on screen size,
 * enabling efficient schedule visualization and event management.
 *
 * @param navController The NavController for navigation between screens.
 * @param state The state containing calendar display configuration and selected events.
 * @param eventsByDate Mapping of dates to their associated event collections.
 * @param onEvent Callback to handle user interactions with calendar components.
 * @param onAddEvent Callback invoked when event creation is requested.
 * @param onEditEvent Callback invoked when event modification is requested.
 * @param onEventDetails Callback invoked when viewing event details is requested.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    navController: NavController,
    state: CalendarEventState,
    eventsByDate: Map<LocalDate, List<Event>>,
    onEvent: (CalendarEventsEvent) -> Unit,
    eventFlow: SharedFlow<CalendarViewModel.UiEvent>,
    //navigation
    onAddEvent: (startDate: String) -> Unit,
    onEditEvent: (eventId: Int) -> Unit,
    onEventDetails: (eventId: Int) -> Unit,
) {
    val tag = "CalendarScreen"
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val windowSize = rememberWindowSize()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val calendarState = remember { mutableStateOf<CalendarState?>(null) }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val selectedDay = state.selectedDate

    LaunchedEffect(key1 = true) {
        Log.d(tag, "LaunchedEffect launched")
        eventFlow.collectLatest { event ->
            when (event) {
                is CalendarViewModel.UiEvent.ShowSnackbar -> {
                    Log.d(tag, "Ui Event Show snackbar: ${event.message}")
                    snackbarHostState.showSnackbar(message = event.message)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (drawerState.isOpen) {
            Log.d(tag, "On start navigation drawer is open, closing it")
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
        drawerState = drawerState
    ) {
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
                        MenuButton { scope.launch { drawerState.open() } }
                    },
                    title = { ScreenTitleText(title = "Calendar") },
                    actions = {
                        Button(onClick = {
                            Log.d(tag, "Today Button pressed, current day set to today")
                            calendarState.value?.let { state ->
                                scope.launch {
                                    state.animateScrollToMonth(YearMonth.now())
                                    onEvent(CalendarEventsEvent.GetEventByDate(LocalDate.now()))
                                }
                            }
                        }) {
                            Text("Today")
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            },
            floatingActionButton = {
                AddFloatingActionButton(
                    onClick = {
                        Log.d(
                            tag,
                            "Try to navigate to add event screen, with selectedDay: $selectedDay"
                        )
                        onAddEvent(selectedDay.toString())
                    },
                    description = "event"
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { padding ->
            when (windowSize.width) {
                WindowType.Compact -> {
                    Log.d(tag, "WindowType is Compact")
                    CompactCalendarLayout(
                        padding = padding,
                        state = state,
                        eventsByDate = eventsByDate,
                        calendarState = calendarState,
                        onEvent = onEvent,
                        onEditEvent = onEditEvent,
                        onEventDetails = onEventDetails,
                        //windowSize = windowSize
                    )
                }

                else -> {
                    Log.d(tag, "WindowType is ${windowSize.width.name}")
                    ExpandedCalendarLayout(
                        padding = padding,
                        state = state,
                        eventsByDate = eventsByDate,
                        calendarState = calendarState,
                        onEvent = onEvent,
                        onEditEvent = onEditEvent,
                        onEventDetails = onEventDetails,
                        windowSize = windowSize
                    )
                }
            }
        }
    }
}


@Composable
private fun CompactCalendarLayout(
    padding: PaddingValues,
    state: CalendarEventState,
    eventsByDate: Map<LocalDate, List<Event>>,
    calendarState: MutableState<CalendarState?>,
    onEvent: (CalendarEventsEvent) -> Unit,
    onEditEvent: (Int) -> Unit,
    onEventDetails: (Int) -> Unit,
    //windowSize: WindowSize
) {
    Column(Modifier.padding(padding)) {
        CalendarSection(
            modifier = Modifier
                .fillMaxWidth()
                .height((0.55f * LocalConfiguration.current.screenHeightDp).dp),
            state = state,
            eventsByDate = eventsByDate,
            calendarState = calendarState,
            onEvent = onEvent
        )
        EventsListSection(
            modifier = Modifier.weight(1f),
            state = state,
            onEditEvent = onEditEvent,
            onEventDetails = onEventDetails
        )
    }
}

@Composable
private fun ExpandedCalendarLayout(
    padding: PaddingValues,
    state: CalendarEventState,
    eventsByDate: Map<LocalDate, List<Event>>,
    calendarState: MutableState<CalendarState?>,
    onEvent: (CalendarEventsEvent) -> Unit,
    onEditEvent: (Int) -> Unit,
    onEventDetails: (Int) -> Unit,
    windowSize: WindowSize
) {
    val tag = "CalendarScreen - ExpandedCalendarLayout"
    Row(Modifier.padding(padding)) {
        CalendarSection(
            modifier = Modifier
                .width(
                    if (windowSize.height == WindowType.Compact) {
                        Log.d(
                            tag,
                            "height WindowType is Compact, width Calendar width set to 250.dp"
                        )
                        250.dp
                    } else {
                        Log.d(
                            tag, "height WindowType is ${windowSize.height.name}, calendar width " +
                                    "set to ${(0.4f * LocalConfiguration.current.screenWidthDp).dp}"
                        )
                        (0.4f * LocalConfiguration.current.screenWidthDp).dp
                    }
                )
                .fillMaxHeight(),
            state = state,
            eventsByDate = eventsByDate,
            calendarState = calendarState,
            onEvent = onEvent
        )
        EventsListSection(
            modifier = Modifier.weight(1f),
            state = state,
            onEditEvent = onEditEvent,
            onEventDetails = onEventDetails
        )
    }
}

@Composable
private fun DateHeader(selectedDate: LocalDate) {
    val today = LocalDate.now()
    val dateString = selectedDate.dayOfWeek.toString()
        .lowercase() + ", " + selectedDate.month.toString()
        .lowercase() + " " + selectedDate.dayOfMonth.toString()

    Text(
        text = if (selectedDate == today) "Today\n$dateString" else dateString,
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
private fun EventsListSection(
    modifier: Modifier,
    state: CalendarEventState,
    onEditEvent: (Int) -> Unit,
    onEventDetails: (Int) -> Unit
) {
    Column(modifier) {
        DateHeader(state.selectedDate)
        LazyColumn(Modifier.fillMaxSize()) {
            items(state.events) { event ->
                EventListItem(
                    event = event,
                    onEditEvent = onEditEvent,
                    onEventDetails = onEventDetails,
                    modifier = Modifier.animateItem(
                        fadeInSpec = tween(250),
                        fadeOutSpec = tween(100),
                        placementSpec = spring(
                            stiffness = Spring.StiffnessLow,
                            dampingRatio = Spring.DampingRatioMediumBouncy
                        )
                    )
                )
            }
        }
    }
}

@Composable
private fun CalendarSection(
    modifier: Modifier,
    state: CalendarEventState,
    eventsByDate: Map<LocalDate, List<Event>>,
    calendarState: MutableState<CalendarState?>,
    onEvent: (CalendarEventsEvent) -> Unit
) {
    Box(modifier) {
        MonthlyCalendar(
            selectedDay = state.selectedDate,
            onDaySelected = { onEvent(CalendarEventsEvent.GetEventByDate(it)) },
            calendarState = calendarState,
            eventsByDate = eventsByDate,
            onMonthChanged = { onEvent(CalendarEventsEvent.GetEventsByMonth(it)) },
            weekStartDay = state.weekStartDay
        )
    }
}


@Composable
private fun EventListItem(
    event: Event,
    //navController: NavController,
    modifier: Modifier = Modifier,
    //navigation
    onEditEvent: (Int) -> Unit,
    onEventDetails: (Int) -> Unit,
) {

    val tag = "CalendarScreen - EventListItem"

    ListItem(
        modifier = modifier
            .padding(8.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable {
                Log.d(tag, "Try to navigate with eventId: ${event.id}")
                onEventDetails(event.id ?: -1)
            },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        leadingContent = {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(CircleShape)
                    .background(Color(Event.eventColors[event.color].first.value))
                    .size(24.dp)
            )
        },
        headlineContent = {
            Text(
                text = event.title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        },
        supportingContent = {
            Column {
                if (event.startDate == event.endDate) {
                    Text(
                        text = if (!event.allDay) "${
                            event.startTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                        } - ${event.endTime.format(DateTimeFormatter.ofPattern("HH:mm"))}"
                        else "All day",
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    Text(
                        text = "Start: ${
                            event.startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        } ${
                            if (!event.allDay) event.startTime.format(
                                DateTimeFormatter.ofPattern("HH:mm")
                            ) else "All day"
                        }",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "End: ${
                            event.endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        } ${
                            if (!event.allDay) event.endTime.format(
                                DateTimeFormatter.ofPattern("HH:mm")
                            ) else "All day"
                        }",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (event.subjectName.isNullOrBlank()) {
                        ""
                    } else {
                        "Subject: ${event.subjectName}"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (event.location.isNullOrBlank()) {
                        ""
                    } else {
                        "Location: ${event.location}"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
            }
        },
        trailingContent = {
            IconButton(
                onClick = {
                    Log.d(
                        tag,
                        "Try to navigate to edit event screen with id: ${event.id}"
                    )
                    onEditEvent(event.id ?: -1)
                }
            ) {
                Icon(
                    //imageVector = Icons.Default.Edit,
                    painter = painterResource(R.drawable.calendar_edit),
                    contentDescription = "Edit event"
                )
            }
        },
    )
}

@Preview(
    showSystemUi = true,
    widthDp = 400,
    heightDp = 800,
)
@Composable
private fun CalendarScreenP() {
    UniAidTheme {
        Surface {
            val context = LocalContext.current
            CalendarScreen(
                navController = NavController(context),
                state = CalendarEventState(
                    events = List(10) {
                        Event(
                            id = it,
                            repeatEndDate = LocalDate.now(),
                            repeatId = it,
                            repeatDifference = 1,
                            repeatDays = emptyList(),

                            title = "Event $it",
                            startDate = LocalDate.now().plusDays(it.toLong()),
                            endDate = LocalDate.now().plusDays(it.toLong()),
                            startTime = java.time.LocalTime.now(),
                            endTime = java.time.LocalTime.now().plusHours(1),
                            allDay = false,
                            color = it % Event.eventColors.size,
                            subjectName = "Subject $it",
                            description = "Description $it",
                            location = "Location $it",

                            )
                    }
                ),
                eventsByDate = emptyMap(),
                onEvent = {},
                onAddEvent = {},
                onEditEvent = {},
                onEventDetails = {},
                eventFlow = MutableSharedFlow()
            )
        }
    }
}
