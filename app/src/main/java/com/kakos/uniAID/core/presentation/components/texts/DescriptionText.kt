package com.kakos.uniAID.core.presentation.components.texts

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow

/**
 * A composable function that displays a description text.
 *
 * @param modifier The [Modifier] to be applied to the text.
 * @param description The description text to be displayed.
 */
@Composable
fun DescriptionText(
    modifier: Modifier = Modifier,
    description: String
) {
    Text(
        modifier = modifier,
        text = description,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 10,
        overflow = TextOverflow.Ellipsis
    )
}