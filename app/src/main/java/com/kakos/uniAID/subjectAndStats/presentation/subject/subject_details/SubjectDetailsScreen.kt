package com.kakos.uniAID.subjectAndStats.presentation.subject.subject_details

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kakos.uniAID.calendar.domain.model.Event
import com.kakos.uniAID.core.domain.subject.domain.model.Subject
import com.kakos.uniAID.core.presentation.WindowType
import com.kakos.uniAID.core.presentation.components.buttons.CancelButton
import com.kakos.uniAID.core.presentation.components.buttons.DeleteButton
import com.kakos.uniAID.core.presentation.components.buttons.EditButton
import com.kakos.uniAID.core.presentation.rememberWindowSize
import com.kakos.uniAID.notes.domain.model.Note
import com.kakos.uniAID.subjectAndStats.presentation.subject.subject_details.components.DeleteSubjectDialog
import com.kakos.uniAID.subjectAndStats.presentation.subject.subject_details.util.SubjectDetailsEvent
import com.kakos.uniAID.subjectAndStats.presentation.subject.subject_details.util.SubjectDetailsState
import com.kakos.uniAID.ui.theme.UniAidTheme
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import java.time.format.DateTimeFormatter

/**
 * Screen for displaying subject details.
 *
 * @param state The current UI state containing subject data and related information.
 * @param onEvent Callback for handling user interactions and state changes.
 * @param subjectId ID of the subject to display, or -1 if no subject should be loaded.
 * @param onNavigateBack Callback to navigate to previous screen.
 * @param onEditSubject Callback to navigate to edit screen with subject ID.
 * @param onViewNote Callback to navigate to note details with note ID.
 * @param onViewEvent Callback to navigate to event details with event ID.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectDetailsScreen(
    state: SubjectDetailsState,
    onEvent: (SubjectDetailsEvent) -> Unit,
    eventFlow: SharedFlow<SubjectDetailsViewModel.UiEvent>,
    subjectId: Int? = -1,
    // navigation
    onNavigateBack: () -> Unit,
    onEditSubject: (Int) -> Unit,
    onViewNote: (Int) -> Unit,
    onViewEvent: (Int) -> Unit
) {

    val tag = "SubjectDetailsScreen"

    val windowSize = rememberWindowSize()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        Log.d(tag, "LaunchedEffect launched")
        eventFlow.collectLatest { event ->
            when (event) {
                is SubjectDetailsViewModel.UiEvent.ShowSnackbar -> {
                    Log.d(tag, "Ui Event Show snackbar: ${event.message}")
                    snackbarHostState.showSnackbar(message = event.message)
                }
            }
        }
    }

    LaunchedEffect(subjectId) {
        Log.d(tag, "LaunchedEffect called with subjectId: $subjectId")
        if (subjectId != -1) {
            Log.d(tag, "Fetching subject with id: $subjectId")
            onEvent(SubjectDetailsEvent.GetSubjectById(subjectId ?: -1))
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
                    CancelButton {
                        Log.d(tag, "Cancel button clicked")
                        onNavigateBack()
                    }
                },
                title = { Text(text = "") },
                actions = {
                    EditButton(
                        onClick = {
                            Log.d(tag, "Edit button clicked")
                            onEditSubject(subjectId ?: -1)
                        },
                        description = "Subject"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    DeleteButton(
                        onClick = {
                            Log.d(tag, "Delete button clicked")
                            showDeleteDialog = true
                        },
                        description = "Subject"
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
                CompactLayout(
                    modifier = Modifier.padding(values),
                    state = state,
                    onViewEvent = onViewEvent,
                    onViewNote = onViewNote
                )
            }

            else -> {
                Log.d(tag, "WindowType is ${windowSize.width}")
                ExpandedLayout(
                    modifier = Modifier.padding(values),
                    state = state,
                    onViewEvent = onViewEvent,
                    onViewNote = onViewNote
                )
            }
        }
    }

    if (showDeleteDialog) {
        Log.d(tag, "Showing delete dialog")
        DeleteSubjectDialog(
            onDismiss = { showDeleteDialog = false },
            onDelete = {
                onEvent(SubjectDetailsEvent.DeleteSubject(state.subject!!))
                showDeleteDialog = false // Dismiss dialog after deletion, before navigation
                onNavigateBack()
            }
        )
    }
}

@Composable
private fun CompactLayout(
    modifier: Modifier = Modifier,
    state: SubjectDetailsState,
    onViewEvent: (Int) -> Unit,
    onViewNote: (Int) -> Unit
) {

    val tag = "SubjectDetailsScreen - CompactLayout"
    Log.d(tag, "CompactLayout called")

    LazyColumn(modifier = modifier.fillMaxSize()) {
        item { SubjectHeader(subject = state.subject) }
        item { SubjectInfo(subject = state.subject) }
        item { SubjectDescription(subject = state.subject) }
        item { FinalGradeSection(subject = state.subject) }
        item { EventsHeader() }
        items(state.events) { event ->
            EventItem(
                event = event,
                onViewEvent = onViewEvent
            )
        }
        item { NotesHeader() }
        items(state.notes) { note ->
            NoteItem(
                note = note,
                onViewNote = onViewNote
            )
        }
    }
}

@Composable
private fun ExpandedLayout(
    modifier: Modifier = Modifier,
    state: SubjectDetailsState,
    onViewEvent: (Int) -> Unit,
    onViewNote: (Int) -> Unit
) {

    val tag = "SubjectDetailsScreen - ExpandedLayout"
    Log.d(tag, "ExpandedLayout called")

    Row(modifier = modifier.fillMaxSize()) {
        // Left column
        Column(
            modifier = Modifier
                .weight(0.4f)
                .padding(end = 16.dp)
        ) {
            SubjectHeader(subject = state.subject)
            SubjectInfo(subject = state.subject)
            SubjectDescription(subject = state.subject)
        }

        // Right column
        Column(
            modifier = Modifier
                .weight(0.6f)
                .padding(start = 16.dp)
        ) {
            FinalGradeSection(subject = state.subject)
            LazyColumn {
                item { EventsHeader() }
                items(state.events) { event ->
                    EventItem(
                        event = event,
                        onViewEvent = onViewEvent
                    )
                }
                item { NotesHeader() }
                items(state.notes) { note ->
                    NoteItem(
                        note = note,
                        onViewNote = onViewNote
                    )
                }
            }
        }
    }
}

@Composable
private fun SubjectHeader(subject: Subject?) {
    Text(
        text = subject?.title ?: "",
        style = MaterialTheme.typography.displaySmall,
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
        overflow = TextOverflow.Ellipsis,
        maxLines = 10
    )
}

@Composable
private fun SubjectInfo(subject: Subject?) {
    Row {
        Text(
            text = "Semester: ${subject?.semester}",
            modifier = Modifier.padding(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Credits: ${subject?.credit}",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun SubjectDescription(subject: Subject?) {
    Text(
        text = "Description:\n${subject?.description ?: "No description"}",
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(8.dp),
        overflow = TextOverflow.Ellipsis,
        maxLines = 50
    )
}

@Composable
private fun FinalGradeSection(subject: Subject?) {
    Spacer(modifier = Modifier.height(16.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Final grade:",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = subject?.finalGrade?.toString() ?: "?",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun EventsHeader() {
    Text(
        text = "Events",
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(8.dp)
    )
}

@Composable
private fun NotesHeader() {
    Text(
        text = "Notes",
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(8.dp)
    )
}

@Composable
private fun EventItem(
    event: Event,
    onViewEvent: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    val tag = "SubjectDetailsScreen - EventItem"

    ListItem(
        modifier = modifier
            .padding(8.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable {
                Log.d(tag, "Event clicked with id: ${event.id}")
                onViewEvent(event.id ?: -1)
            },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        headlineContent = {
            Text(
                text = event.title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        },
        supportingContent = {
            Column {
                Text(
                    text = if (!event.allDay)
                        "${event.startTime.format(DateTimeFormatter.ofPattern("HH:mm"))} - ${
                            event.endTime.format(
                                DateTimeFormatter.ofPattern("HH:mm")
                            )
                        }"
                    else "All day",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = event.location ?: "",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    )
}

@Composable
private fun NoteItem(
    note: Note,
    onViewNote: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    val tag = "SubjectDetailsScreen - NoteItem"

    ListItem(
        modifier = modifier
            .padding(8.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable {
                Log.d(tag, "Note clicked with id: ${note.id}")
                onViewNote(note.id ?: -1)
            },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        headlineContent = {
            Text(
                text = note.title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        },
        supportingContent = {
            Text(
                text = note.content,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    )
}

@Preview
@Composable
private fun SubjectDetailsScreenP() {
    UniAidTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize()
        ) {
            SubjectDetailsScreen(
                onEditSubject = {},
                onViewNote = {},
                onViewEvent = {},
                onNavigateBack = {},
                state = SubjectDetailsState(
                    subject = Subject(
                        title = "Numerical Methods II.",
                        semester = 5,
                        credit = 3,
                        description = "This course is about numerical methods and algorithms. It is a " +
                                "continuation of Numerical Methods I. The course covers topics such as " +
                                "numerical integration, numerical differentiation, and solving differential " +
                                "equations. The course also covers the implementation of algorithms in " +
                                "programming languages such as Python and C++.",
                        finalGrade = 5
                    ),
                ),
                onEvent = {},
                subjectId = -1,
                eventFlow = MutableSharedFlow()
            )
        }
    }
}