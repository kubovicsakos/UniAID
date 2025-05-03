package com.kakos.uniAID.calendar.presentation.event_details

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kakos.uniAID.calendar.domain.model.Event
import com.kakos.uniAID.calendar.domain.model.Repeat
import com.kakos.uniAID.calendar.presentation.event_details.components.DeleteEventDialog
import com.kakos.uniAID.calendar.presentation.event_details.util.EventDetailsEvent
import com.kakos.uniAID.calendar.presentation.event_details.util.EventDetailsState
import com.kakos.uniAID.core.presentation.WindowType
import com.kakos.uniAID.core.presentation.components.buttons.CancelButton
import com.kakos.uniAID.core.presentation.components.buttons.DeleteButton
import com.kakos.uniAID.core.presentation.components.buttons.EditButton
import com.kakos.uniAID.core.presentation.components.texts.DescriptionText
import com.kakos.uniAID.core.presentation.components.texts.TitleText
import com.kakos.uniAID.core.presentation.rememberWindowSize
import com.kakos.uniAID.ui.theme.UniAidTheme
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Screen for displaying event details.
 *
 * Presents comprehensive view of an event with options to edit or delete it.
 * Adapts layout based on screen size and handles event operations through
 * the provided view model.
 *
 * @param state Current UI state containing event details.
 * @param onEvent Callback to handle user interactions.
 * @param eventId ID of the event to display, defaults to -1.
 * @param onNavigateBack Callback to navigate back to previous screen.
 * @param onEditEvent Callback to navigate to edit screen with specified event ID.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsScreen(
    state: EventDetailsState,
    onEvent: (EventDetailsEvent) -> Unit,
    eventFlow: SharedFlow<EventDetailsViewModel.UiEvent>,
    eventId: Int? = -1,
    onNavigateBack: () -> Unit,
    onEditEvent: (Int) -> Unit
) {

    val tag = "EventDetailsScreen"

    val windowSize = rememberWindowSize()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var showDeleteDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        Log.d(tag, "LaunchedEffect launched")
        eventFlow.collectLatest { event ->
            when (event) {
                is EventDetailsViewModel.UiEvent.ShowSnackbar -> {
                    Log.d(tag, "Ui Event Show snackbar: ${event.message}")
                    snackbarHostState.showSnackbar(message = event.message)
                }
            }
        }
    }

    LaunchedEffect(eventId) {
        Log.d(tag, "LaunchedEffect called, with eventId: $eventId")
        if (eventId != -1) {
            onEvent(EventDetailsEvent.GetEventById(eventId ?: -1))
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
                    Log.d(tag, "Cancel icon clicked")
                    CancelButton { onNavigateBack() }
                },
                title = { Text(text = "") },
                actions = {
                    EditButton(
                        onClick = {
                            Log.d(tag, "Edit button clicked")
                            onEditEvent(state.event.id ?: -1)
                        },
                        description = "Event"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    DeleteButton(
                        onClick = {
                            Log.d(tag, "Delete button clicked")
                            showDeleteDialog = true
                        },
                        description = "Event"
                    )
                },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { values ->
        when (windowSize.width) {
            WindowType.Compact -> {
                Log.d(tag, "WindowType is Compact")
                CompactLayout(state, values)
            }

            else -> {
                Log.d(tag, "WindowType is ${windowSize.width.name}")
                ExpandedLayout(state, values)
            }
        }
    }

    if (showDeleteDialog) {
        Log.d(tag, "Showing delete dialog")
        DeleteEventDialog(
            singleEvent = state.event.repeat == Repeat.NONE,
            onDismiss = { showDeleteDialog = false },
            onDelete = { deleteOption ->
                onEvent(EventDetailsEvent.DeleteEvent(state.event, deleteOption))
                showDeleteDialog = false
                onNavigateBack()
            }
        )
    }
}

/**
 * Composable that displays event details in a compact layout.
 *
 * Arranges event information vertically with header and details sections,
 * optimized for smaller screen sizes.
 *
 * @param state The state containing event data to display.
 * @param paddingValues Padding values to apply to the layout.
 */
@Composable
private fun CompactLayout(
    state: EventDetailsState,
    paddingValues: PaddingValues
) {
    LazyColumn(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
    ) {
        item { EventHeaderInfo(state.event) }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item { EventDetailsInfo(state.event) }
    }
}

/**
 * Composable that displays event details in an expanded layout.
 *
 * Arranges event information in two columns with header and details sections,
 * optimized for larger screen sizes.
 *
 * @param state The state containing event data to display.
 * @param paddingValues Padding values to apply to the layout.
 */
@Composable
private fun ExpandedLayout(
    state: EventDetailsState,
    paddingValues: PaddingValues
) {
    Row(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        ) {
            item {
                EventHeaderInfo(state.event)
            }
        }
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        ) {
            item { EventDetailsInfo(state.event) }
        }
    }
}

/**
 * Composable that displays event's header information.
 *
 * Shows the event's color indicator, title, subject, date range and time
 * in a structured layout with appropriate typography styles.
 *
 * @param event Event object containing the header information to display.
 */
@Composable
private fun EventHeaderInfo(event: Event) {
    Row {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Color(Event.eventColors[event.color].first.value))
            )
        }
        Column {
            Text(
                text = event.title,
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                overflow = TextOverflow.Ellipsis,
                maxLines = 5
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = event.subjectName ?: "",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
    Column {
        TitleText(
            title = if (event.startDate == event.endDate) "${event.startDate}" else "${event.startDate} - ${event.endDate}",
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        TitleText(
            title = if (!event.allDay)
                "${event.startTime.format(DateTimeFormatter.ofPattern("HH:mm"))} - ${
                    event.endTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                }"
            else "All day",
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp)
        )
    }
}

/**
 * Composable that displays detailed information about an event.
 *
 * Presents the event's location, recurrence pattern (if applicable),
 * and description in a structured layout with conditional visibility
 * for repeat information based on the event's configuration.
 *
 * @param event Event object containing all details to be displayed.
 */
@Composable
private fun EventDetailsInfo(event: Event) {
    Column {
        TitleText(
            title = "Location: ${event.location}",
            modifier = Modifier.padding(8.dp),
            maxLines = 2
        )
        AnimatedVisibility(visible = event.repeat != Repeat.NONE) {
            DescriptionText(
                description = "Repeat: ${
                    when (event.repeat) {
                        Repeat.NONE -> ""
                        Repeat.DAILY -> "Every ${event.repeatDifference} day"
                        Repeat.WEEKLY -> {
                            "Every ${event.repeatDifference} week on ${
                                event.repeatDays.joinToString(separator = ", ") { day ->
                                    day.name.substring(0, 2).lowercase()
                                        .replaceFirstChar { it.uppercase() }
                                }
                            }"
                        }

                        Repeat.MONTHLY -> "Every ${event.repeatDifference} month"
                        Repeat.YEARLY -> "Every ${event.repeatDifference} year"
                    }
                }",
                modifier = Modifier.padding(8.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        DescriptionText(
            description = "Description:\n${event.description}",
            modifier = Modifier.padding(8.dp),

            )
    }
}

@Preview
@Composable
private fun EventDetailsScreenP() {
    UniAidTheme {
        Surface {
            EventDetailsScreen(
                state = EventDetailsState(
                    event = Event(
                        id = 1,
                        title = "Event",
                        subjectId = 1,
                        startDate = LocalDate.of(2022, 12, 12),
                        endDate = LocalDate.of(2022, 12, 12),
                        startTime = LocalTime.of(12, 0),
                        endTime = LocalTime.of(13, 0),
                        allDay = false,
                        repeat = Repeat.NONE,
                        repeatDifference = 1,
                        repeatEndDate = LocalDate.of(2022, 12, 12),
                        location = "Location",
                        description = "Description",
                        color = 6
                    )
                ),
                onEvent = {},
                onNavigateBack = {},
                onEditEvent = {},
                eventFlow = MutableSharedFlow()
            )
        }
    }
}
