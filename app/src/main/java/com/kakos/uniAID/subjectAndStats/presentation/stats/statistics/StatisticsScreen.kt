package com.kakos.uniAID.subjectAndStats.presentation.stats.statistics

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.kakos.uniAID.R
import com.kakos.uniAID.core.domain.subject.domain.model.Subject
import com.kakos.uniAID.core.presentation.NavDrawer
import com.kakos.uniAID.core.presentation.WindowType
import com.kakos.uniAID.core.presentation.components.buttons.MenuButton
import com.kakos.uniAID.core.presentation.components.texts.InfoText
import com.kakos.uniAID.core.presentation.components.texts.ScreenTitleText
import com.kakos.uniAID.core.presentation.rememberWindowSize
import com.kakos.uniAID.subjectAndStats.presentation.components.NumberPickerDropdown
import com.kakos.uniAID.subjectAndStats.presentation.stats.statistics.components.InfoDialog
import com.kakos.uniAID.subjectAndStats.presentation.stats.statistics.components.StatisticDisplayItem
import com.kakos.uniAID.subjectAndStats.presentation.stats.statistics.util.StatisticsEvent
import com.kakos.uniAID.subjectAndStats.presentation.stats.statistics.util.StatisticsState
import com.kakos.uniAID.ui.theme.UniAidTheme
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Screen for statistics visualization and management.
 *
 * Displays subject statistics, grade management controls, and academic performance
 * indicators across different layout configurations based on window size.
 *
 * @param navController Navigation controller to handle screen transitions.
 * @param state Current UI state containing statistics and subjects data.
 * @param onEvent Callback to handle user interactions and UI events.
 * @param onAllStatistics Callback to navigate to detailed statistics view.
 * @param onSubjectDetails Callback to navigate to subject details view.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    navController: NavController,
    state: StatisticsState,
    eventFlow: SharedFlow<StatisticsViewModel.UiEvent>,
    onEvent: (StatisticsEvent) -> Unit,
    // navigation
    onAllStatistics: () -> Unit,
    onSubjectDetails: (subjectId: Int) -> Unit
) {

    val tag = "StatisticsScreen"

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val scope = rememberCoroutineScope()
    val windowSize = rememberWindowSize()
    var showInfoDialog by remember { mutableStateOf(false) }
    var infoDialogTitle by remember { mutableStateOf("") }
    var infoDialogDescription by remember { mutableStateOf("") }

    LaunchedEffect(key1 = true) {
        Log.d(tag, "LaunchedEffect launched")
        eventFlow.collectLatest { event ->
            when (event) {
                is StatisticsViewModel.UiEvent.ShowSnackbar -> {
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
                    title = { ScreenTitleText(title = "Statistics") },
                    actions = {
                        IconButton(
                            onClick = {
                                Log.d(tag, "All statistics button clicked")
                                onAllStatistics()
                            }
                        ) {
                            Icon(
                                //imageVector = Icons.Default.Home,
                                painter = painterResource(R.drawable.all_stats),
                                contentDescription = "All statistics"
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior
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
                    onSemesterChange = {
                        Log.d(tag, "Semester changed to $it")
                        onEvent(StatisticsEvent.SetSemester(it))
                    }
                )

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    item {
                        StatisticDisplayItem(
                            title = "Weighted Avg",
                            value = state.weightedAverage,
                            openDialog = {
                                Log.d(tag, "Weighted Avg button clicked")
                                infoDialogTitle = "Weighted Average"
                                infoDialogDescription = "Weighted Average is the sum of the " +
                                        "products of the grades and credits of all " +
                                        "subjects in the semester with a passing grade " +
                                        "(greater than 1), divided by the completed credits.\n" +
                                        "Weighted Avg = (sum of (completed credit * grade) / " +
                                        "completed credits)"
                                showInfoDialog = true
                            }
                        )
                    }
                    item {
                        StatisticDisplayItem(
                            title = "CCI",
                            value = state.cci,
                            openDialog = {
                                Log.d(tag, "CCI button clicked")
                                infoDialogTitle = "CCI"
                                infoDialogDescription = "CCI is the Corrected Credit Index. " +
                                        "It is the sum of the products of the grades and credits" +
                                        " of all subjects in the semester divided by 30, " +
                                        " times (completed credits/commited credits).\n" +
                                        " CCI = CI * (completed credits / committed credits)"
                                showInfoDialog = true
                            }
                        )
                    }
                    item {
                        StatisticDisplayItem(
                            title = "CI",
                            value = state.ci,
                            openDialog = {
                                Log.d(tag, "CI button clicked")
                                infoDialogTitle = "CI"
                                infoDialogDescription = "CI is the Credit index. It is the " +
                                        "sum of the products of the grades and credits of all " +
                                        "subjects in the semester divided by 30.\n " +
                                        "CI = (sum of (grade * credit)) / 30"
                                showInfoDialog = true
                            }

                        )
                    }
                    item {
                        StatisticDisplayItem(
                            title = "Committed Credits",
                            value = state.committedCredit,
                            openDialog = {
                                Log.d(tag, "Committed Credits button clicked")
                                infoDialogTitle = "Committed Credits"
                                infoDialogDescription = "Committed Credits is the sum of the " +
                                        "credits of all subjects in the semester."
                                showInfoDialog = true
                            }
                        )
                    }
                    item {
                        StatisticDisplayItem(
                            title = "Completed Credits",
                            value = state.completedCredit,
                            openDialog = {
                                Log.d(tag, "Completed Credits button clicked")
                                infoDialogTitle = "Completed Credits"
                                infoDialogDescription = "Completed Credits is the sum of the " +
                                        "credits of all subjects in the semester with a passing grade " +
                                        "(greater than 1)."
                                showInfoDialog = true
                            }
                        )
                    }
                }

                when (windowSize.width) {
                    WindowType.Compact -> {
                        Log.d(tag, "WindowType is Compact")
                        CompactLayout(
                            subjects = state.subjects,
                            onEvent = onEvent,
                            onSubjectDetails = onSubjectDetails
                        )
                    }

                    WindowType.Medium -> {
                        Log.d(tag, "WindowType is Medium")
                        MediumLayout(
                            subjects = state.subjects,
                            onEvent = onEvent,
                            onSubjectDetails = onSubjectDetails
                        )
                    }

                    WindowType.Expanded -> {
                        Log.d(tag, "WindowType is Expanded")
                        ExpandedLayout(
                            subjects = state.subjects,
                            onEvent = onEvent,
                            onSubjectDetails = onSubjectDetails
                        )
                    }
                }
            }
        }
    }

    if (showInfoDialog) {
        Log.d(tag, "Showing info dialog")
        InfoDialog(
            title = infoDialogTitle,
            description = infoDialogDescription,
            onDismiss = { showInfoDialog = false }
        )
    }
}

/**
 * Composable that displays subjects in a single column layout for WindowType Compact.
 *
 * @param subjects List of user's enrolled subjects to display.
 * @param onEvent Callback to handle statistics-related events.
 * @param onSubjectDetails Callback to navigate to subject details.
 */
@Composable
private fun CompactLayout(
    subjects: List<Subject>,
    onEvent: (StatisticsEvent) -> Unit,
    onSubjectDetails: (Int) -> Unit
) {

    val tag = "StatisticsScreen - CompactLayout"
    Log.d(tag, "CompactLayout displaying")

    LazyColumn(
        contentPadding = PaddingValues(8.dp)
    ) {
        items(subjects) { subject ->
            SubjectItem(
                subject = subject,
                onSubjectDetails = onSubjectDetails,
                onEvent = onEvent
            )
        }
    }
}

/**
 * Composable that displays subjects in a two-column grid layout for WindowType Medium.
 *
 * @param subjects List of user's enrolled subjects to display.
 * @param onEvent Callback to handle statistics-related events.
 * @param onSubjectDetails Callback to navigate to subject details.
 */
@Composable
private fun MediumLayout(
    subjects: List<Subject>,
    onEvent: (StatisticsEvent) -> Unit,
    onSubjectDetails: (Int) -> Unit
) {
    val tag = "StatisticsScreen - MediumLayout"
    Log.d(tag, "MediumLayout displaying")

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
                            onSubjectDetails = onSubjectDetails,
                            onEvent = onEvent
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

/**
 * Composable that displays subjects in a three-column grid layout for WindowType Expanded.
 *
 * @param subjects List of user's enrolled subjects to display.
 * @param onEvent Callback to handle statistics-related events.
 * @param onSubjectDetails Callback to navigate to subject details.
 */
@Composable
private fun ExpandedLayout(
    subjects: List<Subject>,
    onEvent: (StatisticsEvent) -> Unit,
    onSubjectDetails: (Int) -> Unit
) {
    val tag = "StatisticsScreen - ExpandedLayout"
    Log.d(tag, "ExpandedLayout displaying")

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
                            onSubjectDetails = onSubjectDetails,
                            onEvent = onEvent
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

/**
 * Composable that displays an individual subject card with grade selection.
 *
 * @param subject The subject data to display.
 * @param onEvent Callback to handle statistics-related events.
 * @param modifier Optional modifier for styling and layout.
 * @param onSubjectDetails Callback to navigate to subject details.
 */
@Composable
private fun SubjectItem(
    subject: Subject,
    onEvent: (StatisticsEvent) -> Unit,
    modifier: Modifier = Modifier,
    onSubjectDetails: (Int) -> Unit,
) {

    val tag = "StatisticsScreen - SubjectItem"

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
            key(subject.grade) {
                NumberPickerDropdown(
                    lowerBound = 1,
                    upperBound = 5,
                    defaultNumber = subject.grade,
                    onValueChange = { newGrade ->
                        Log.d(tag, "Grade changed to $newGrade")
                        subject.id?.let { id ->
                            onEvent(
                                StatisticsEvent.SetGrade(
                                    subjectId = id,
                                    grade = newGrade
                                )
                            )
                        }
                    }
                )
            }
        }
    )
}

/**
 * Composable that displays the current semester with navigation controls.
 *
 * @param currentSemester The active semester number.
 * @param onSemesterChange Callback when user changes semester selection.
 * @param modifier Optional modifier for styling and layout.
 */
@Composable
private fun SemesterHeader(
    currentSemester: Int,
    onSemesterChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    val tag = "StatisticsScreen - SemesterHeader"

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

@Preview
@Composable
private fun StatisticsScreenP() {
    UniAidTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize()
        ) {
            StatisticsScreen(
                navController = rememberNavController(),
                state = StatisticsState(
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
                onAllStatistics = {},
                onSubjectDetails = {},
                eventFlow = MutableSharedFlow()
            )
        }
    }
}