package com.kakos.uniAID.calendar.presentation.calendar.monthly_calendar.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.kakos.uniAID.calendar.presentation.calendar.monthly_calendar.util.displayText
import java.time.DayOfWeek

/**
 * Composable for displaying monthly calendar header.
 *
 * Encapsulates the rendering of weekday labels across the top of a monthly calendar,
 * providing a consistently formatted row of abbreviated day names aligned with
 * their respective columns.
 *
 * @param daysOfWeek The ordered list of weekdays to display in the header.
 */
@Composable
fun MonthHeader(daysOfWeek: List<DayOfWeek>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("MonthHeader"),
    ) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                text = dayOfWeek.displayText(),
                fontWeight = FontWeight.Medium,
            )
        }
    }
}