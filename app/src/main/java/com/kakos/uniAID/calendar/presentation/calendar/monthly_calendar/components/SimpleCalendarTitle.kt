package com.kakos.uniAID.calendar.presentation.calendar.monthly_calendar.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kakos.uniAID.calendar.presentation.calendar.monthly_calendar.util.displayText
import java.time.YearMonth


/**
 * Composable for displaying calendar title with navigation controls.
 *
 * Encapsulates the rendering of a month-year header with directional
 * navigation icons, providing consistent title formatting and intuitive
 * month traversal functionality.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param currentMonth The currently displayed month-year combination.
 * @param isHorizontal Whether to display navigation arrows horizontally (true) or vertically (false).
 * @param goToPrevious Callback invoked when previous month navigation is requested.
 * @param goToNext Callback invoked when next month navigation is requested.
 */
@Composable
fun SimpleCalendarTitle(
    modifier: Modifier,
    currentMonth: YearMonth,
    isHorizontal: Boolean = true,
    goToPrevious: () -> Unit,
    goToNext: () -> Unit,
) {
    Row(
        modifier = modifier.height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CalendarNavigationIcon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = "Back",
            onClick = goToPrevious,
            isHorizontal = isHorizontal,
        )
        Text(
            modifier = Modifier
                .weight(1f),
            text = currentMonth.displayText(),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
        )
        CalendarNavigationIcon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Next",
            onClick = goToNext,
            isHorizontal = isHorizontal,
        )
    }
}