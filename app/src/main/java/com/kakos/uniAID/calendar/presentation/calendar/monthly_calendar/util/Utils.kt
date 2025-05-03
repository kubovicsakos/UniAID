package com.kakos.uniAID.calendar.presentation.calendar.monthly_calendar.util

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.semantics.Role
import com.kizitonwose.calendar.compose.CalendarLayoutInfo
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.core.CalendarMonth
import kotlinx.coroutines.flow.filterNotNull
import java.time.DayOfWeek
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

/**
 * Extension function to display a YearMonth as a formatted string.
 *
 * Converts a YearMonth instance into a human-readable string representation
 * following the pattern "Month Year" with configurable month name length.
 *
 * @param short Whether to use abbreviated month names (e.g., "Jan" vs "January").
 * @return String representation in "Month Year" format.
 */
fun YearMonth.displayText(short: Boolean = false): String {
    return "${this.month.displayText(short = short)} ${this.year}"
}


/**
 * Extension function to obtain a Month's localized display name.
 *
 * Provides a consistent method to retrieve a month's textual representation
 * with configurable abbreviation level for uniform display across the application.
 *
 * @param short Whether to return an abbreviated month name.
 * @return Localized textual representation of the month.
 */
fun Month.displayText(short: Boolean = true): String {
    val style = if (short) TextStyle.SHORT else TextStyle.FULL
    return getDisplayName(style, Locale.ENGLISH)
}

/**
 * Extension function to obtain a DayOfWeek's localized display name.
 *
 * Provides configurable text formatting for weekday names, supporting
 * different text styles and case transformations for calendar headers
 * and navigation elements.
 *
 * @param uppercase Whether to convert the result to uppercase.
 * @param narrow Whether to use the narrowest text form (single letter) instead of short form.
 * @return Localized textual representation of the day of week.
 */
fun DayOfWeek.displayText(uppercase: Boolean = false, narrow: Boolean = false): String {
    val style = if (narrow) TextStyle.NARROW else TextStyle.SHORT
    return getDisplayName(style, Locale.ENGLISH).let { value ->
        if (uppercase) value.uppercase(Locale.ENGLISH) else value
    }
}

/**
 * Extension function for custom clickable modifier implementation.
 *
 * Provides enhanced control over the standard clickable behavior, allowing
 * selective enabling of ripple effects and other interaction properties
 * for consistent touch behavior across calendar elements.
 *
 * @param enabled Whether the clickable behavior is active.
 * @param showRipple Whether to display the ripple effect feedback.
 * @param onClickLabel Accessibility description for the clickable action.
 * @param role Semantic role of the clickable element.
 * @param onClick Callback invoked when the element is clicked.
 * @return Modified Modifier with custom clickable behavior.
 */
fun Modifier.clickable(
    enabled: Boolean = true,
    showRipple: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit,
): Modifier = composed {
    clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = if (showRipple) LocalIndication.current else null,
        enabled = enabled,
        onClickLabel = onClickLabel,
        role = role,
        onClick = onClick,
    )
}

/**
 * Composable function for tracking the most visible month in a calendar view.
 *
 * Monitors scrolling within the calendar and identifies which month
 * occupies the greatest portion of the visible viewport, enabling
 * synchronized header updates and month-based data loading.
 *
 * @param state The calendar state containing layout information.
 * @param viewportPercent Percentage threshold for considering a month as "most visible".
 * @return The CalendarMonth determined to be most prominently displayed.
 */
@Composable
fun rememberFirstMostVisibleMonth(
    state: CalendarState,
    viewportPercent: Float = 50f,
): CalendarMonth {
    val visibleMonth = remember(state) { mutableStateOf(state.firstVisibleMonth) }
    LaunchedEffect(state) {
        snapshotFlow { state.layoutInfo.firstMostVisibleMonth(viewportPercent) }
            .filterNotNull()
            .collect { month -> visibleMonth.value = month }
    }
    return visibleMonth.value
}

/**
 * Extension function to calculate the most visible month in a calendar layout.
 *
 * Determines which month occupies the largest portion of the visible viewport
 * based on offset and size measurements, enabling accurate tracking of
 * the displayed month during scroll operations.
 *
 * @param viewportPercent Percentage threshold for visibility calculation.
 * @return The most visible CalendarMonth or null if no months are visible.
 */
private fun CalendarLayoutInfo.firstMostVisibleMonth(viewportPercent: Float = 50f): CalendarMonth? {
    return if (visibleMonthsInfo.isEmpty()) {
        null
    } else {
        val viewportSize = (viewportEndOffset + viewportStartOffset) * viewportPercent / 100f
        visibleMonthsInfo.firstOrNull { itemInfo ->
            if (itemInfo.offset < 0) {
                itemInfo.offset + itemInfo.size >= viewportSize
            } else {
                itemInfo.size - itemInfo.offset >= viewportSize
            }
        }?.month
    }
}