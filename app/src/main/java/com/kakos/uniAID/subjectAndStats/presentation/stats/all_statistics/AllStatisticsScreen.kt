package com.kakos.uniAID.subjectAndStats.presentation.stats.all_statistics

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kakos.uniAID.core.presentation.WindowType
import com.kakos.uniAID.core.presentation.rememberWindowSize
import com.kakos.uniAID.subjectAndStats.presentation.stats.all_statistics.util.AllStatisticsState
import com.kakos.uniAID.subjectAndStats.presentation.stats.all_statistics.util.SemesterStatistics
import com.kakos.uniAID.subjectAndStats.presentation.stats.all_statistics.util.TotalStatistics
import com.kakos.uniAID.subjectAndStats.presentation.stats.statistics.components.InfoDialog
import com.kakos.uniAID.subjectAndStats.presentation.stats.statistics.components.StatisticDisplayItem
import com.kakos.uniAID.ui.theme.UniAidTheme
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

/**
 * Screen for displaying academic performance statistics.
 *
 * Shows a comprehensive view of academic statistics organized by semester,
 * including a summary of total performance metrics at the top.
 *
 * @param onNavigateBack Callback for handling navigation back to previous screen.
 * @param state The UI state containing statistics data to display.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllStatisticsScreen(
    state: AllStatisticsState,
    eventFlow: SharedFlow<AllStatisticsViewModel.UiEvent>,
    onNavigateBack: () -> Unit
) {

    val tag = "AllStatisticsScreen"

    val windowSize = rememberWindowSize()
    var showInfoDialog by remember { mutableStateOf(false) }
    val infoDialogTitle by remember { mutableStateOf("") }
    val infoDialogDescription by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        Log.d(tag, "LaunchedEffect launched")
        eventFlow.collectLatest { event ->
            when (event) {
                is AllStatisticsViewModel.UiEvent.ShowSnackbar -> {
                    Log.d(tag, "Ui Event Show snackbar: ${event.message}")
                    snackbarHostState.showSnackbar(message = event.message)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Statistics") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            Log.d(tag, "Back button clicked")
                            onNavigateBack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (windowSize.width) {
                WindowType.Compact -> {
                    Log.d(tag, "WindowType is Compact")
                    CompactSemesterLayout(state)
                }

                WindowType.Medium -> {
                    Log.d(tag, "WindowType is Medium")
                    MediumSemesterLayout(state)
                }

                WindowType.Expanded -> {
                    Log.d(tag, "WindowType is Expanded")
                    ExpandedSemesterLayout(state)
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

@Composable
fun CompactSemesterLayout(
    state: AllStatisticsState,
) {
    val tag = "AllStatisticsScreen - CompactSemesterLayout"
    Log.d(tag, "CompactSemesterLayout called")
    LazyColumn(
        contentPadding = PaddingValues(8.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                TotalStatsSection(
                    totalStats = state.totalStats,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        items(state.semesterStats) { stats ->
            SemesterStatsSection(semesterStats = stats)
        }
    }
}

@Composable
fun MediumSemesterLayout(
    state: AllStatisticsState
) {
    val tag = "AllStatisticsScreen - MediumSemesterLayout"
    Log.d(tag, "MediumSemesterLayout called")
    val groupedStats = state.semesterStats.chunked(2)

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                TotalStatsSection(
                    totalStats = state.totalStats,
                    modifier = Modifier.width(350.dp)
                )
            }
        }
        items(groupedStats) { rowStats ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowStats.forEach { stats ->
                    Box(modifier = Modifier.weight(1f)) {
                        SemesterStatsSection(semesterStats = stats)
                    }
                }
                repeat(2 - rowStats.size) {
                    Box(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun ExpandedSemesterLayout(state: AllStatisticsState) {

    val tag = "AllStatisticsScreen - ExpandedSemesterLayout"
    Log.d(tag, "ExpandedSemesterLayout called")
    val groupedStats = state.semesterStats.chunked(3)

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                TotalStatsSection(
                    totalStats = state.totalStats,
                    modifier = Modifier.width(500.dp)
                )
            }
        }
        items(groupedStats) { rowStats ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowStats.forEach { stats ->
                    Box(modifier = Modifier.weight(1f)) {
                        SemesterStatsSection(semesterStats = stats)
                    }
                }
                repeat(3 - rowStats.size) {
                    Box(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/**
 * Displays the semester statistics in a card format.
 *
 * Presents semester-specific statistics including CI (Credit Index) and
 * CCI (Cumulative Credit Index) values. Each statistic is interactive
 * and opens a dialog with detailed information when clicked.
 *
 * @param semesterStats The semester statistics data to display.
 */
@Composable
fun SemesterStatsSection(
    semesterStats: SemesterStatistics
) {

    val tag = "AllStatisticsScreen - SemesterStatsSection"

    var showInfoDialog by remember { mutableStateOf(false) }
    var infoDialogTitle by remember { mutableStateOf("") }
    var infoDialogDescription by remember { mutableStateOf("") }
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Semester ${semesterStats.semester}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Column(modifier = Modifier.padding(top = 8.dp)) {
                StatisticDisplayItem(
                    title = "CI",
                    value = semesterStats.ci,
                    openDialog = {
                        Log.d(tag, "CI clicked")
                        showInfoDialog = true
                        infoDialogTitle = "CI"
                        infoDialogDescription =
                            "This is the CI for semester ${semesterStats.semester}."
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                StatisticDisplayItem(
                    title = "CCI",
                    value = semesterStats.cci,
                    openDialog = {
                        Log.d(tag, "CCI clicked")
                        showInfoDialog = true
                        infoDialogTitle = "CCI"
                        infoDialogDescription =
                            "This is the CCI for semester ${semesterStats.semester}."
                    }
                )
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
 * Displays the total statistics in a card format.
 *
 * Shows aggregate statistics across all semesters including total CI,
 * total CCI, completed credits, and committed credits. Provides
 * information dialogs for CI and CCI when clicked.
 *
 * @param totalStats The combined statistics data across all semesters.
 * @param modifier The modifier to be applied to the component.
 */
@Composable
fun TotalStatsSection(
    totalStats: TotalStatistics,
    modifier: Modifier
) {

    val tag = "AllStatisticsScreen - TotalStatsSection"

    var showInfoDialog by remember { mutableStateOf(false) }
    var infoDialogTitle by remember { mutableStateOf("") }
    var infoDialogDescription by remember { mutableStateOf("") }
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Totals",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Column(modifier = Modifier.padding(top = 8.dp)) {
                StatisticDisplayItem(
                    title = "Total CI",
                    value = totalStats.ci,
                    openDialog = {
                        Log.d(tag, "Total CI clicked")
                        showInfoDialog = true
                        infoDialogTitle = "Total CI"
                        infoDialogDescription = "This is the total CI for all semesters."
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                StatisticDisplayItem(
                    title = "Total CCI",
                    value = totalStats.cci,
                    openDialog = {
                        Log.d(tag, "Total CCI clicked")
                        showInfoDialog = true
                        infoDialogTitle = "Total CCI"
                        infoDialogDescription = "This is the total CCI for all semesters."
                    }
                )
            }
            Text(
                text = "Completed Credits: ${totalStats.totalCompleted}",
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = "Committed Credits: ${totalStats.totalCommitted}",
                modifier = Modifier.padding(top = 4.dp)
            )
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

@Preview
@Composable
private fun TotalStatsP() {
    UniAidTheme {
        Surface {
            AllStatisticsScreen(
                onNavigateBack = {},
                state = AllStatisticsState(
                    semesterStats = (1..10).map { semester ->
                        SemesterStatistics(
                            semester = semester,
                            ci = when (semester) {
                                1 -> 2.5f
                                2 -> 3.0f
                                3 -> 2.8f
                                4 -> 3.2f
                                5 -> 3.5f
                                6 -> 3.3f
                                7 -> 3.6f
                                8 -> 3.7f
                                9 -> 3.9f
                                10 -> 4.0f
                                else -> 0f
                            },
                            cci = when (semester) {
                                1 -> 2.0f
                                else -> semester.toFloat() / 3
                            },
                            weightedAverage = semester.toFloat() / 2,
                            completedCredit = semester * 10,
                            committedCredit = semester * 20

                        )
                    },
                    totalStats = TotalStatistics(
                        ci = 2.5f,
                        cci = 2.0f,
                        totalCompleted = 30,
                        totalCommitted = 60
                    )
                ),
                eventFlow = MutableSharedFlow()
            )
        }
    }
}