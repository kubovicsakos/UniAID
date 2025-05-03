package com.kakos.uniAID.calendar.presentation.calendar.monthly_calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kakos.uniAID.calendar.domain.model.Event
import com.kakos.uniAID.calendar.presentation.calendar.monthly_calendar.util.clickable
import com.kakos.uniAID.ui.theme.inactive_text_color
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import java.time.LocalDate

/**
 * Composable for displaying a single calendar day.
 *
 * Encapsulates the rendering of an individual day within the monthly calendar view,
 * handling different visual states and displaying event indicators for dates with
 * scheduled activities.
 *
 * @param day The calendar day to render within the month grid.
 * @param isSelected Whether this day is currently selected by the user.
 * @param today The current date used to highlight today's cell.
 * @param events List of events occurring on this day.
 * @param onClick Callback invoked when the day is clicked.
 */
@Composable
fun Day(
    day: CalendarDay,
    isSelected: Boolean,
    today: LocalDate,
    events: List<Event>, // Add events parameter
    onClick: (CalendarDay) -> Unit
) {
    val isToday = day.date == today
    Box(
        modifier = Modifier
            .aspectRatio(1f) // This is important for square-sizing!
            .testTag("MonthDay")
            .padding(4.dp)
            .clip(CircleShape)
            .background(
                color = when {
                    isSelected -> MaterialTheme.colorScheme.primaryContainer
                    isToday -> MaterialTheme.colorScheme.surfaceContainer
                    else -> Color.Transparent
                }
            )
            // Disable clicks on inDates/outDates
            .clickable(
                enabled = day.position == DayPosition.MonthDate,
                showRipple = !isSelected,
                onClick = { onClick(day) },
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val textColor = when (day.position) {
                // Color.Unspecified will use the default text color from the current theme
                DayPosition.MonthDate -> if (isSelected || isToday) Color.White else Color.Unspecified
                DayPosition.InDate, DayPosition.OutDate -> inactive_text_color
            }
            Text(
                text = day.date.dayOfMonth.toString(),
                color = textColor,
                fontSize = 14.sp,
            )
            if (events.isNotEmpty()) {
                val maxDots = 3
                val visibleEvents = events.take(maxDots)
                //val hasMore = events.size > maxDots

                Row(
                    modifier = Modifier.padding(top = 1.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    visibleEvents.forEach { event ->
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(Color(Event.eventColors[event.color].first.value))
                                .padding(horizontal = 1.dp)
                        )
                    }
                }
            }
        }
    }
}