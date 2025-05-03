package com.kakos.uniAID.notes.presentation.notes


import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kakos.uniAID.core.presentation.NavDrawer
import com.kakos.uniAID.core.presentation.WindowType
import com.kakos.uniAID.core.presentation.components.buttons.MenuButton
import com.kakos.uniAID.core.presentation.components.buttons.floating_button.AddFloatingActionButton
import com.kakos.uniAID.core.presentation.components.texts.ScreenTitleText
import com.kakos.uniAID.core.presentation.rememberWindowSize
import com.kakos.uniAID.notes.domain.model.Note
import com.kakos.uniAID.notes.presentation.notes.components.OrderSection
import com.kakos.uniAID.notes.presentation.notes.util.NotesEvent
import com.kakos.uniAID.notes.presentation.notes.util.NotesState
import com.kakos.uniAID.ui.theme.UniAidTheme
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

/**
 * Screen for notes management.
 *
 * @param navController Controller for navigation between screens.
 * @param state Current UI state of the notes screen.
 * @param onEvent Callback for handling note-related events.
 * @param onAddNote Callback for note creation action.
 * @param onEditNote Callback for note editing action with note ID.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    navController: NavController,
    state: NotesState,
    eventFlow: SharedFlow<NotesViewModel.UiEvent>,
    onEvent: (NotesEvent) -> Unit,
    onAddNote: () -> Unit,
    onEditNote: (noteId: Int) -> Unit
) {
    val tag = "NotesScreen"
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val scope = rememberCoroutineScope()
    val windowSize = rememberWindowSize()

    LaunchedEffect(key1 = true) {
        Log.d(tag, "LaunchedEffect launched")
        eventFlow.collectLatest { event ->
            when (event) {
                is NotesViewModel.UiEvent.ShowSnackbar -> {
                    Log.d(tag, "Ui Event Show snackbar: ${event.message}")
                    snackbarHostState.showSnackbar(message = event.message)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        Log.d(tag, "LaunchedEffect launched")
        if (drawerState.isOpen) {
            Log.d(tag, "Drawer is open, closing it")
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
                    modifier = Modifier.fillMaxWidth(),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    navigationIcon = {
                        MenuButton { scope.launch { drawerState.open() } }
                    },
                    title = { ScreenTitleText(title = "Notes") },
                    actions = {
                        IconButton(
                            onClick = {
                                Log.d(tag, "Sort button clicked")
                                onEvent(NotesEvent.ToggleOrderSection)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                contentDescription = "Sort"
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            },
            floatingActionButton = {
                AddFloatingActionButton(
                    onClick = {
                        Log.d(tag, "Add note button clicked")
                        onAddNote()
                    },
                    description = "note"
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { values ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(values)
            ) {
                AnimatedVisibility(
                    visible = state.isOrderSectionVisible,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    Log.d(tag, "Order section visible")
                    OrderSection(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp),
                        noteOrder = state.noteOrder,
                        onOrderChange = { onEvent(NotesEvent.Order(it)) }
                    )
                }

                when (windowSize.width) {
                    WindowType.Compact -> {
                        Log.d(tag, "WindowType is Compact")
                        CompactLayout(
                            notes = state.notes,
                            onEditNote = { noteId ->
                                onEditNote(noteId)
                            },
                            onDeleteNote = {
                                onEvent(NotesEvent.DeleteNote(it))
                                scope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "Note deleted",
                                        actionLabel = "Undo"
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        onEvent(NotesEvent.RestoreNote)
                                    }
                                }
                            }
                        )
                    }

                    WindowType.Medium -> {
                        Log.d(tag, "WindowType is Medium")
                        MediumLayout(
                            notes = state.notes,
                            onEditNote = { noteId ->
                                onEditNote(noteId)
                            },
                            onDeleteNote = {
                                onEvent(NotesEvent.DeleteNote(it))
                                scope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "Note deleted",
                                        actionLabel = "Undo"
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        onEvent(NotesEvent.RestoreNote)
                                    }
                                }
                            }
                        )
                    }

                    WindowType.Expanded -> {
                        Log.d(tag, "WindowType is Expanded")
                        ExpandedLayout(
                            notes = state.notes,
                            onEditNote = { noteId ->
                                onEditNote(noteId)
                            },
                            onDeleteNote = {
                                onEvent(NotesEvent.DeleteNote(it))
                                scope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "Note deleted",
                                        actionLabel = "Undo"
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        onEvent(NotesEvent.RestoreNote)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Composable that displays notes in a single column layout for Compact.
 *
 * @param notes List of notes to display.
 * @param onEditNote Callback when user edits a note.
 * @param onDeleteNote Callback when user deletes a note.
 */
@Composable
private fun CompactLayout(
    notes: List<Note>,
    onEditNote: (Int) -> Unit,
    onDeleteNote: (Note) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(8.dp)
    ) {
        items(
            items = notes,
            key = { it.id ?: 0 }
        ) { note ->
            NoteItem(
                note = note,
                onEdit = { onEditNote(note.id ?: -1) },
                onDelete = onDeleteNote
            )
        }
    }
}

/**
 * Composable that displays notes in a two-column grid for Medium.
 *
 * @param notes List of notes to display.
 * @param onEditNote Callback when user edits a note.
 * @param onDeleteNote Callback when user deletes a note.
 */
@Composable
private fun MediumLayout(
    notes: List<Note>,
    onEditNote: (Int) -> Unit,
    onDeleteNote: (Note) -> Unit
) {
    val groupedNotes = notes.chunked(2)

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        items(groupedNotes) { rowNotes ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowNotes.forEach { note ->
                    Box(modifier = Modifier.weight(1f)) {
                        NoteItem(
                            note = note,
                            onEdit = { onEditNote(note.id ?: -1) },
                            onDelete = onDeleteNote
                        )
                    }
                }
                // Add empty boxes if the row is not complete
                repeat(2 - rowNotes.size) {
                    Box(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/**
 * Composable that displays notes in a three-column grid for Expanded.
 *
 * @param notes List of notes to display.
 * @param onEditNote Callback when user edits a note.
 * @param onDeleteNote Callback when user deletes a note.
 */
@Composable
private fun ExpandedLayout(
    notes: List<Note>,
    onEditNote: (Int) -> Unit,
    onDeleteNote: (Note) -> Unit
) {
    val groupedNotes = notes.chunked(3)

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        items(groupedNotes) { rowNotes ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowNotes.forEach { note ->
                    Box(modifier = Modifier.weight(1f)) {
                        NoteItem(
                            note = note,
                            onEdit = { onEditNote(note.id ?: -1) },
                            onDelete = onDeleteNote
                        )
                    }
                }
                // Add empty boxes if the row is not complete
                repeat(3 - rowNotes.size) {
                    Box(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/**
 * Composable that displays a single note card.
 *
 * @param note Note data to display.
 * @param onEdit Callback when note is selected for editing.
 * @param onDelete Callback when note is deleted.
 * @param modifier Modifier for styling and layout adjustments.
 */
@Composable
private fun NoteItem(
    note: Note,
    onEdit: (Int) -> Unit,
    onDelete: (Note) -> Unit,
    modifier: Modifier = Modifier
) {
    val tag = "NotesScreen - NoteItem"
    ListItem(
        modifier = modifier
            .animateContentSize(
                animationSpec = tween(durationMillis = 250)
            )
            .padding(8.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable {
                Log.d(tag, "Note clicked: ${note.id} with title: ${note.title}")
                onEdit(note.id ?: -1)
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
        overlineContent = {
            if (note.subjectId != null) {
                Text(
                    text = note.subjectName ?: "",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        supportingContent = {
            Column {
                Text(
                    text = note.content,
                    maxLines = 10,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    text = "Modified: ${note.lastModified.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}",
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = "Created: ${note.creationTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        },
        trailingContent = {
            IconButton(
                onClick = {
                    Log.d(
                        tag,
                        "Delete button clicked for note: ${note.id} with title: ${note.title}"
                    )
                    onDelete(note)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete note"
                )
            }
        }
    )
}

@Preview
@Composable
private fun NotesScreenP() {
    UniAidTheme {
        Surface {
            val context = LocalContext.current
            NotesScreen(
                navController = NavController(context),
                state = NotesState(
                    notes = listOf(
                        Note(
                            id = 1,
                            title = "Note 1",
                            content = "Content 1",
                            subjectId = 1,
                            subjectName = "Subject 1",
                            creationTime = java.time.LocalDateTime.now(),
                            lastModified = java.time.LocalDateTime.now(),
                            darkTheme = false,
                        ),
                        Note(
                            id = 2,
                            title = "Note 2",
                            content = "Content 2",
                            subjectId = 2,
                            subjectName = "Subject 2",
                            creationTime = java.time.LocalDateTime.now(),
                            lastModified = java.time.LocalDateTime.now(),
                            darkTheme = false,
                        ),
                        Note(
                            id = 3,
                            title = "Note 3",
                            content = "Content 3",
                            subjectId = 3,
                            subjectName = "Subject 3",
                            creationTime = java.time.LocalDateTime.now(),
                            lastModified = java.time.LocalDateTime.now(),
                            darkTheme = false,
                        ),
                        Note(
                            id = 4,
                            title = "Note 4",
                            content = "Content 4",
                            subjectId = 4,
                            subjectName = "Subject 4",
                            creationTime = java.time.LocalDateTime.now(),
                            lastModified = java.time.LocalDateTime.now(),
                            darkTheme = false,
                        )
                    )
                ),
                onEvent = {},
                onAddNote = {},
                onEditNote = {},
                eventFlow = MutableSharedFlow()
            )
        }
    }
}