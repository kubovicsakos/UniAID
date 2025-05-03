package com.kakos.uniAID.core.presentation.components.texts

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
/**
 * Represents a title text component.
 *
 * This component is used to display a title with a specific style.
 *
 * @param modifier Optional [Modifier] to be applied to the text.
 * @param title The title text to be displayed.
 * @param maxLines The maximum number of lines for the text. Default is 1.
 */
@Composable
fun TitleText(
    modifier: Modifier = Modifier,
    title: String,
    maxLines: Int = 1
) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}