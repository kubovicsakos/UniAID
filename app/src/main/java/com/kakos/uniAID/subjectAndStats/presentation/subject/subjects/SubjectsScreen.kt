package com.kakos.uniAID.subjectAndStats.presentation.subject.subjects


import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.kakos.uniAID.core.domain.subject.domain.model.Subject
import com.kakos.uniAID.core.presentation.NavDrawer
import com.kakos.uniAID.core.presentation.WindowType
import com.kakos.uniAID.core.presentation.components.buttons.MenuButton
import com.kakos.uniAID.core.presentation.components.buttons.floating_button.AddFloatingActionButton
import com.kakos.uniAID.core.presentation.components.texts.InfoText
import com.kakos.uniAID.core.presentation.components.texts.ScreenTitleText
import com.kakos.uniAID.core.presentation.rememberWindowSize
import com.kakos.uniAID.subjectAndStats.presentation.subject.subjects.util.SubjectsEvent
import com.kakos.uniAID.subjectAndStats.presentation.subject.subjects.util.SubjectsState
import com.kakos.uniAID.ui.theme.UniAidTheme
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Screen for displaying subjects list.
 *
 * @param navController Controller for handling navigation.
 * @param state Current UI state containing subjects data.
 * @param onEvent Callback for handling user interactions and state changes.
 * @param onAddSubject Callback to navigate to add subject screen.
 * @param onEditSubject Callback to navigate to edit screen with subject ID.
 * @param onSubjectDetails Callback to navigate to subject details with subject ID.
 */
@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalAnimationApi
@Composable
fun SubjectsScreen(
    navController: NavController,
    state: SubjectsState,
    eventFlow: SharedFlow<SubjectsViewModel.UiEvent>,
    onEvent: (SubjectsEvent) -> Unit,
    //navigation
    onAddSubject: () -> Unit,
    onEditSubject: (subjectId: Int) -> Unit,
    onSubjectDetails: (subjectId: Int) -> Unit
) {
    val tag = "SubjectsScreen"

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val scope = rememberCoroutineScope()
    val windowSize = rememberWindowSize()

    LaunchedEffect(key1 = true) {
        Log.d(tag, "LaunchedEffect launched")
        eventFlow.collectLatest { event ->
            when (event) {
                is SubjectsViewModel.UiEvent.ShowSnackbar -> {
                    Log.d(tag, "Ui Event Show snackbar: ${event.message}")
                    snackbarHostState.showSnackbar(message = event.message)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        Log.d(tag, "LaunchedEffect called")
        if (drawerState.isOpen) {
            Log.d(tag, "Navigation Drawer was open, Closing drawer")
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
                    title = { ScreenTitleText(title = "Subjects") },
                    scrollBehavior = scrollBehavior
                )
            },
            floatingActionButton = {
                AddFloatingActionButton(
                    onClick = {
                        Log.d(tag, "Floating action button clicked")
                        onAddSubject()
                    },
                    description = "subject"
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { values ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(values)
            ) {
                SemesterHeader(
                    currentSemester = state.currentSemester,
                    onSemesterChange = { onEvent(SubjectsEvent.SetSemester(it)) }
                )

                when (windowSize.width) {
                    WindowType.Compact -> {
                        Log.d(tag, "WindowType is Compact")
                        CompactLayout(
                            subjects = state.subjects,
                            onEditSubject = onEditSubject,
                            onSubjectDetails = onSubjectDetails
                        )
                    }

                    WindowType.Medium -> {
                        Log.d(tag, "WindowType is Medium")
                        MediumLayout(
                            subjects = state.subjects,
                            onEditSubject = onEditSubject,
                            onSubjectDetails = onSubjectDetails
                        )
                    }

                    WindowType.Expanded -> {
                        Log.d(tag, "WindowType is Expanded")
                        ExpandedLayout(
                            subjects = state.subjects,
                            onEditSubject = onEditSubject,
                            onSubjectDetails = onSubjectDetails
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SemesterHeader(
    currentSemester: Int,
    onSemesterChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    val tag = "SubjectsScreen - SemesterHeader"

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        AnimatedVisibility(
            visible = currentSemester > 1,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            IconButton(
                onClick = {
                    Log.d(tag, "Previous semester button clicked")
                    if (currentSemester > 1) {
                        onSemesterChange(currentSemester - 1)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous semester"
                )
            }
        }

        Text(
            text = "Semester $currentSemester",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.Center)
        )

        IconButton(
            onClick = {
                Log.d(tag, "Next semester button clicked")
                if (currentSemester < Int.MAX_VALUE - 1) {
                    onSemesterChange(currentSemester + 1)
                }
            },
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Next semester"
            )
        }
    }
}

@Composable
private fun CompactLayout(
    subjects: List<Subject>,
    onEditSubject: (Int) -> Unit,
    onSubjectDetails: (Int) -> Unit
) {

    val tag = "SubjectsScreen - CompactLayout"
    Log.d(tag, "CompactLayout called")

    LazyColumn(
        contentPadding = PaddingValues(8.dp)
    ) {
        items(subjects) { subject ->
            SubjectItem(
                subject = subject,
                onEditSubject = onEditSubject,
                onSubjectDetails = onSubjectDetails
            )
        }
    }
}

@Composable
private fun MediumLayout(
    subjects: List<Subject>,
    onEditSubject: (Int) -> Unit,
    onSubjectDetails: (Int) -> Unit
) {

    val tag = "SubjectsScreen - MediumLayout"
    Log.d(tag, "MediumLayout called")

    val groupedSubjects = subjects.chunked(2)

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        items(groupedSubjects) { rowSubjects ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowSubjects.forEach { subject ->
                    Box(modifier = Modifier.weight(1f)) {
                        SubjectItem(
                            subject = subject,
                            onEditSubject = onEditSubject,
                            onSubjectDetails = onSubjectDetails
                        )
                    }
                }
                repeat(2 - rowSubjects.size) {
                    Box(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ExpandedLayout(
    subjects: List<Subject>,
    onEditSubject: (Int) -> Unit,
    onSubjectDetails: (Int) -> Unit
) {

    val tag = "SubjectsScreen - ExpandedLayout"
    Log.d(tag, "ExpandedLayout called")

    val groupedSubjects = subjects.chunked(3)

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        items(groupedSubjects) { rowSubjects ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowSubjects.forEach { subject ->
                    Box(modifier = Modifier.weight(1f)) {
                        SubjectItem(
                            subject = subject,
                            onEditSubject = onEditSubject,
                            onSubjectDetails = onSubjectDetails
                        )
                    }
                }
                repeat(3 - rowSubjects.size) {
                    Box(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun SubjectItem(
    subject: Subject,
    onEditSubject: (Int) -> Unit,
    onSubjectDetails: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    val tag = "SubjectsScreen - SubjectItem"

    ListItem(
        modifier = modifier
            .animateContentSize(
                animationSpec = tween(durationMillis = 250)
            )
            .padding(8.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable {
                Log.d(tag, "Subject item clicked")
                onSubjectDetails(subject.id ?: -1)
            },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        leadingContent = {
            Column {
                Text(text = "${subject.credit} credits")
                InfoText(text = "${subject.semester} semester")
            }
        },
        headlineContent = {
            Text(
                text = subject.title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        },
        supportingContent = {
            Text(
                text = subject.description,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        },
        trailingContent = {
            IconButton(
                onClick = {
                    Log.d(tag, "Edit subject button clicked")
                    onEditSubject(subject.id ?: -1)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit subject"
                )
            }
        }
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Preview
@Composable
private fun SubjectScreenPreview() {
    UniAidTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize()
        ) {
            SubjectsScreen(
                navController = rememberNavController(),
                state = SubjectsState(
                    subjects = List(10) {
                        Subject(
                            id = it,
                            title = "Subject $it",
                            description = "Description of subject $it",
                            credit = it,
                            semester = it
                        )
                    }
                ),
                onEvent = {},
                onAddSubject = {},
                onEditSubject = {},
                onSubjectDetails = {},
                eventFlow = MutableSharedFlow()
            )
        }
    }
}