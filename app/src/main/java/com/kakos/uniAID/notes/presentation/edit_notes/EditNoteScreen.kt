package com.kakos.uniAID.notes.presentation.edit_notes

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kakos.uniAID.core.presentation.components.buttons.CancelButton
import com.kakos.uniAID.core.presentation.components.buttons.floating_button.SaveFloatingActionButton
import com.kakos.uniAID.notes.presentation.edit_notes.EditNoteViewModel.UiEvent
import com.kakos.uniAID.notes.presentation.edit_notes.components.DeleteNoteDialog
import com.kakos.uniAID.notes.presentation.edit_notes.components.EditNoteTextField
import com.kakos.uniAID.notes.presentation.edit_notes.components.NoteSubjectPickerDropdown
import com.kakos.uniAID.notes.presentation.edit_notes.util.EditNoteEvent
import com.kakos.uniAID.notes.presentation.edit_notes.util.EditNoteState
import com.kakos.uniAID.ui.theme.UniAidTheme
import com.kakos.uniAID.ui.theme.dark_note_color
import com.kakos.uniAID.ui.theme.light_note_color
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


/**
 * Screen for creating and editing notes.
 *
 * @param state Current UI state containing note data and display properties.
 * @param onEvent Callback to handle user interactions and state changes.
 * @param eventFlow Flow of UI events for navigation and user feedback.
 * @param currentNoteId ID of the note being edited or null for new notes.
 * @param onNavigateBack Callback to navigate back to previous screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(
    state: EditNoteState,
    onEvent: (EditNoteEvent) -> Unit,
    eventFlow: SharedFlow<UiEvent>,
    noteId: Int?,
    onNavigateBack: () -> Unit,
) {
    val tag = "EditNoteScreen"
    //Things to 'remember' or pass

    var isInitialized by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    //val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val dropdownExpanded = remember { mutableStateOf(false) }
    val showDeleteDialog = remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(noteId) {
        Log.d(
            tag,
            "LaunchedEffect called, with noteId: $noteId and isInitialized: $isInitialized"
        )
        if (!isInitialized) {
            scope.launch {
                Log.d(tag, "LaunchedEffect called, with noteId: $noteId")
                onEvent(EditNoteEvent.GetNote(noteId ?: -1))
            }
        }
        isInitialized = true
        Log.d(tag, "LaunchedEffect finished")
    }

    //Collecting events
    LaunchedEffect(key1 = true) {
        Log.d(tag, "LaunchedEffect launched")
        eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    Log.d(tag, "Ui Event Show snackbar: ${event.message}")
                    snackbarHostState.showSnackbar(message = event.message)
                }

                is UiEvent.SaveNote -> {
                    Log.d(tag, "Ui Event Save note")
                    onNavigateBack()
                }
            }
        }
    }


    Scaffold(
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
                title = {
                    Text(
                        text = "",
                        style = MaterialTheme.typography.displaySmall,
                        modifier = Modifier
                    )
                },
                navigationIcon = {
                    CancelButton { onNavigateBack() }
                },
                actions = {
                    // Subject picker dropdown
                    NoteSubjectPickerDropdown(
                        subjectName = state.subjectName,
                        filteredSubjects = state.filteredSubjects,
                        onSubjectSelected = {
                            Log.d(tag, "Subject selected with SubjectPickerDropdown: $it")
                            onEvent(EditNoteEvent.SelectSubject(it))
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            Log.d(tag, "Settings button clicked")
                            dropdownExpanded.value = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Settings"
                        )
                    }
                    DropdownMenu(
                        expanded = dropdownExpanded.value,
                        onDismissRequest = {
                            Log.d(tag, "Dropdown menu dismissed")
                            dropdownExpanded.value = false
                        }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Toggle Dark Theme") },
                            onClick = {
                                Log.d(tag, "Toggle dark theme clicked")
                                onEvent(EditNoteEvent.ToggleDarkTheme)
                                dropdownExpanded.value = false
                            }
                        )
                        AnimatedVisibility(visible = !state.isNewNote) {
                            DropdownMenuItem(
                                text = { Text("Delete Note") },
                                onClick = {
                                    Log.d(tag, "Delete note clicked")
                                    showDeleteDialog.value = true
                                    dropdownExpanded.value = false
                                }
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        // Save
        floatingActionButton = {
            SaveFloatingActionButton(
                onClick = {
                    onEvent(EditNoteEvent.SaveNote)
                },
                description = "note"
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { value ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(if (state.isDarkTheme) dark_note_color else light_note_color)
                .padding(value)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            // Title field
            EditNoteTextField(
                isDarkTheme = state.isDarkTheme,
                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                text = state.title.text,
                hint = state.title.hint,
                onValueChange = {
                    Log.d(tag, "Entered title: $it")
                    onEvent(EditNoteEvent.EnteredTitle(it))
                },
                onFocusChange = {
                    Log.d(tag, "Focus changed: $it")
                    onEvent(EditNoteEvent.ChangeTitleFocus(it))
                },
                isHintVisible = state.title.isHintVisible,
                singleLine = true,
                modifier = Modifier.weight(0.075f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Content field
            EditNoteTextField(
                isDarkTheme = state.isDarkTheme,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                text = state.content.text,
                hint = state.content.hint,
                onValueChange = {
                    Log.d(tag, "Entered content: $it")
                    onEvent(EditNoteEvent.EnteredContent(it))
                },
                onFocusChange = {
                    Log.d(tag, "Focus changed: $it")
                    onEvent(EditNoteEvent.ChangeContentFocus(it))
                },
                isHintVisible = state.content.isHintVisible,
                modifier = Modifier.weight(0.925f),
            )
        }
    }
    if (showDeleteDialog.value) {
        Log.d(tag, "Displaying delete dialog")
        DeleteNoteDialog(
            onDismiss = {
                Log.d(tag, "Delete dialog dismissed")
                showDeleteDialog.value = false
            },
            onDelete = {
                Log.d(tag, "Delete note clicked")
                onEvent(EditNoteEvent.DeleteNote(noteId ?: -1))
                showDeleteDialog.value = false
                onNavigateBack()
            }
        )
    }
}

@Preview
@Composable
private fun EditNoteScreenP() {
    UniAidTheme {
        Surface {
            EditNoteScreen(
                state = EditNoteState(),
                onEvent = {},
                eventFlow = MutableSharedFlow(),
                onNavigateBack = {},
                noteId = -1
            )
        }
    }
}
