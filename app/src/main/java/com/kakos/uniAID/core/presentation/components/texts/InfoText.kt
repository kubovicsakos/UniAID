package com.kakos.uniAID.core.presentation.components.texts

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
/**
 * A composable function that displays informational text.
 *
 * @param modifier The [Modifier] to be applied to the text.
 * @param text The text to be displayed.
 */
@Composable
fun InfoText(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        text = text, style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Light,
        modifier = modifier
    )
}