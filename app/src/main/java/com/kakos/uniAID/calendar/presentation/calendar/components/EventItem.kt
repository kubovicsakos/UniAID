package com.kakos.uniAID.calendar.presentation.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kakos.uniAID.core.presentation.components.texts.DescriptionText
import com.kakos.uniAID.core.presentation.components.texts.InfoText
import com.kakos.uniAID.core.presentation.components.texts.TitleText
import com.kakos.uniAID.ui.theme.UniAidTheme

/**
 * Composable for displaying calendar event item.
 *
 * Encapsulates the rendering of a single event within the calendar interface,
 * displaying comprehensive event information in a consistently formatted card.
 *
 * @param title The event's primary title text.
 * @param startTime String representation of the event's start time.
 * @param endTime String representation of the event's end time.
 * @param subject The academic subject or category associated with the event.
 * @param location The physical or virtual location where the event occurs.
 * @param repeats Text describing the event's recurrence pattern.
 * @param modifier Optional Modifier for customizing the component's layout and appearance.
 */
@Composable
fun EventItem(
    title: String,
    startTime: String,
    endTime: String,
    subject: String,
    location: String,
    repeats: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .padding(end = 32.dp)
        ) {
            Column {
                TitleText(title = title)
                TitleText(title = subject)
                Spacer(modifier = Modifier.height(8.dp))
                DescriptionText(description = "Start: $startTime")
                DescriptionText(description = "End: $endTime")
                InfoText(text = repeats)
                InfoText(text = location)
            }
        }
    }
}

@Preview
@Composable
private fun EventItemP() {
    UniAidTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize()
        ) {
            EventItem(
                title = "Előadás",
                subject = "Numerikus módszerek II.",
                startTime = "10:00",
                endTime = "12:00",
                location = "F/111",
                repeats = "Every day"
            )
        }
    }
}