package com.kakos.uniAID.subjectAndStats.presentation.subject.edit_subjects

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kakos.uniAID.core.presentation.components.buttons.CancelButton
import com.kakos.uniAID.core.presentation.components.texts.OptionText
import com.kakos.uniAID.subjectAndStats.presentation.components.NumberPickerDropdown
import com.kakos.uniAID.subjectAndStats.presentation.subject.edit_subjects.components.EditSubjectTextField
import com.kakos.uniAID.subjectAndStats.presentation.subject.edit_subjects.util.EditSubjectEvent
import com.kakos.uniAID.subjectAndStats.presentation.subject.edit_subjects.util.EditSubjectState
import com.kakos.uniAID.ui.theme.UniAidTheme
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

/**
 * Screen for editing or creating a subject.
 *
 * @param state The current UI state containing subject data.
 * @param onEvent Callback for handling user interactions and state changes.
 * @param subjectId ID of the subject being edited, or -1 for creating a new subject.
 * @param onNavigateBack Callback to handle navigation back to previous screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSubjectScreen(
    state: EditSubjectState,
    onEvent: (EditSubjectEvent) -> Unit,
    eventFlow: SharedFlow<EditSubjectViewModel.UiEvent>,
    subjectId: Int? = -1,
    //navigation
    onNavigateBack: () -> Unit
) {

    val tag = "EditSubjectScreen"

    val titleState = state.title
    val descriptionState = state.description
    val semesterState = state.semester
    val creditState = state.credit
    val finalGradeState = state.finalGrade

    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    //val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        Log.d(tag, "LaunchedEffect launched")
        eventFlow.collectLatest { event ->
            when (event) {
                is EditSubjectViewModel.UiEvent.ShowSnackbar -> {
                    Log.d(tag, "Ui Event Show snackbar: ${event.message}")
                    snackbarHostState.showSnackbar(message = event.message)
                }

                is EditSubjectViewModel.UiEvent.SaveSubject -> {
                    Log.d(tag, "Ui Event Saved subject successfully")
                    onNavigateBack()
                }
            }
        }
    }

    LaunchedEffect(subjectId) {
        Log.d(tag, "LaunchedEffect called with subjectId: $subjectId")
        onEvent(EditSubjectEvent.GetSubjectById(subjectId ?: -1))
        Log.d(tag, "LaunchedEffect finished")
    }

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
                    CancelButton { onNavigateBack() }
                },
                title = {
                    Text(
                        text = ""
                    )
                },
                actions = {
                    Button(
                        onClick = {
                            Log.d(tag, "Save button clicked")
                            onEvent(EditSubjectEvent.SaveSubject)
                        }
                    ) {
                        Text(text = "Save")
                    }
                },

                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { values ->
        LazyColumn(
            modifier = Modifier
                .padding(values)
        ) {
            item {

                EditSubjectTextField(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    text = titleState.text,
                    error = titleState.error,
                    label = "Subject Title",
                    onValueChange = {
                        Log.d(tag, "Title changed to: $it")
                        onEvent(EditSubjectEvent.EnteredTitle(it))
                    },
                )
            }
            item {
                EditSubjectTextField(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    text = descriptionState,
                    label = "Description",
                    onValueChange = {
                        Log.d(tag, "Description changed to: $it")
                        onEvent(EditSubjectEvent.EnteredDescription(it))
                    },
                )
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OptionText(
                        text = "Credit",
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                    )
                    Spacer(modifier = Modifier.width(42.dp))
                    key(state.credit) { // key is used to make sure the composable is recomposed when the state changes
                        NumberPickerDropdown(
                            lowerBound = 0,
                            upperBound = 100,
                            defaultNumber = creditState,
                            hasNullOption = false,
                            onValueChange = {
                                Log.d(tag, "Credit changed to: $it")
                                onEvent(EditSubjectEvent.EnteredCredit(it))
                            },
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                        )
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OptionText(
                        text = "Semester",
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    key(state.semester) {
                        NumberPickerDropdown(
                            lowerBound = 1,
                            upperBound = 100,
                            defaultNumber = semesterState,
                            hasNullOption = false,
                            onValueChange = {
                                Log.d(tag, "Semester changed to: $it")
                                onEvent(EditSubjectEvent.EnteredSemester(it))
                            },
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                        )
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Final grade",
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                    )
                    key(state.finalGrade) {
                        NumberPickerDropdown(
                            lowerBound = 1,
                            upperBound = 5,
                            defaultNumber = finalGradeState,
                            onValueChange = {
                                Log.d(tag, "Final grade changed to: $it")
                                onEvent(EditSubjectEvent.EnteredFinalGrade(it))
                            },
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                        )
                    }
                }
            }
        }
    }
}


@Preview
@Composable
private fun EditSubjectScreenP() {
    UniAidTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize()
        ) {
            EditSubjectScreen(
                onNavigateBack = {},
                state = EditSubjectState(),
                onEvent = {},
                eventFlow = MutableSharedFlow()
            )
        }
    }
}