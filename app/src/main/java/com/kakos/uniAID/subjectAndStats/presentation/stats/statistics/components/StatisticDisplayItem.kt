package com.kakos.uniAID.subjectAndStats.presentation.stats.statistics.components


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kakos.uniAID.ui.theme.UniAidTheme

/**
 * Composable that displays a statistic item card.
 *
 * Presents a key metric in a card layout with a title and value, designed
 * for dashboard interfaces where statistics need to be displayed prominently.
 *
 * @param modifier Modifier to be applied to the card container.
 * @param title The descriptive label for the statistic.
 * @param openDialog Callback to execute when the card is clicked.
 * @param value The numerical statistic value to display (Int or floating-point number).
 */
@Composable
fun StatisticDisplayItem(
    modifier: Modifier = Modifier,
    title: String,
    openDialog: () -> Unit,
    value: Number
) {
    ElevatedCard(
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable { openDialog() }
            .wrapContentSize()
            .padding(8.dp)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                modifier = modifier
                    .padding(8.dp)

            )
            Spacer(modifier = Modifier.height(8.dp))
            // Display the value
            Box(
                modifier = modifier
                    .padding(8.dp)
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Text(
                    text = if (value is Int) value.toString() else "%.2f".format(value),
                    modifier = modifier
                        .align(Alignment.Center),
                )
            }
        }
    }
}

@Preview
@Composable
private fun StatisticDisplayItemP() {
    UniAidTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize()
        ) {
            StatisticDisplayItem(
                title = "Average Grade",
                value = 3.5f,
                openDialog = {}
            )
        }
    }
}