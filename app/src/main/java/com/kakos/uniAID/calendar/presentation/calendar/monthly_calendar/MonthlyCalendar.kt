package com.kakos.uniAID.calendar.presentation.calendar.monthly_calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.kakos.uniAID.calendar.domain.model.Event
import com.kakos.uniAID.calendar.presentation.calendar.monthly_calendar.components.Day
import com.kakos.uniAID.calendar.presentation.calendar.monthly_calendar.components.MonthHeader
import com.kakos.uniAID.calendar.presentation.calendar.monthly_calendar.components.SimpleCalendarTitle
import com.kakos.uniAID.calendar.presentation.calendar.monthly_calendar.util.rememberFirstMostVisibleMonth
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

/**
 * Composable for displaying a monthly calendar.
 *
 * Encapsulates a fully-featured calendar view with month navigation controls,
 * day selection functionality, and visual event indicators, providing
 * consistent date selection and browsing capabilities.
 *
 * @param selectedDay The currently selected date in the calendar.
 * @param onDaySelected Callback invoked when a user selects a calendar day.
 * @param calendarState Mutable reference to track and control calendar display state.
 * @param eventsByDate Mapping of dates to their associated event collections.
 * @param onMonthChanged Callback invoked when visible month changes during navigation.
 * @param weekStartDay Configurable first day of the week for consistent calendar display.
 */
@Composable
fun MonthlyCalendar(
    //adjacentMonths: Long = 1500, // 125 years into the past and future
    selectedDay: LocalDate, // user selected date
    onDaySelected: (LocalDate) -> Unit, // callback for when a date is selected -> get data for that date
    calendarState: MutableState<CalendarState?>, // state of the calendar
    eventsByDate: Map<LocalDate, List<Event>>, // Add events parameter
    onMonthChanged: (YearMonth) -> Unit, // Add month change callback
    weekStartDay: DayOfWeek // Add as parameter
) {

//    val currentMonth = remember { YearMonth.now() } // current month
    val startMonth = remember { YearMonth.of(0, 1) }
    val endMonth = remember { YearMonth.of(9999, 12) }
    //val selectedDate = remember { mutableStateOf<CalendarDay?>(null) } // user selected date
    val daysOfWeek = remember(weekStartDay) { daysOfWeek(firstDayOfWeek = weekStartDay) }
    // days of the week
    val today = remember { LocalDate.now() } // today's date
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        // remember the calendar state -> start month, end month, first visible month, first day of the week
        val firstVisibleMonth = YearMonth.from(selectedDay)
        val state = rememberCalendarState(
            startMonth = startMonth,
            endMonth = endMonth,
            firstVisibleMonth = firstVisibleMonth, // Set to selectedDay's month
            firstDayOfWeek = daysOfWeek.first(),
        )
        calendarState.value = state
        val coroutineScope = rememberCoroutineScope()
        val visibleMonth = rememberFirstMostVisibleMonth(
            state,
            viewportPercent = 90f
        ) // get the first most visible month
        SimpleCalendarTitle(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
            currentMonth = visibleMonth.yearMonth, // current month and year
            goToPrevious = { // go to previous month
                coroutineScope.launch {
                    state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.previousMonth)
                }
            },
            goToNext = { // go to next month
                coroutineScope.launch {
                    state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.nextMonth)
                }
            },
        )
        LaunchedEffect(visibleMonth) {
            onMonthChanged(visibleMonth.yearMonth)
        }
        HorizontalCalendar(
            // calendar grid
            modifier = Modifier.testTag("Calendar"),
            state = state,
            dayContent = { day ->
                Day(
                    day = day,
                    isSelected = selectedDay == day.date,
                    today = today,
                    events = eventsByDate[day.date] ?: emptyList(), // Pass events to Day
                    onClick = { clicked ->
                        onDaySelected(clicked.date)
                    }
                )
            },
            monthHeader = {
                MonthHeader(daysOfWeek = daysOfWeek) // days of the week for the header
            },
        )
    }
}
