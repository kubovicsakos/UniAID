package com.kakos.uniAID.core.presentation.components.texts

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
/**
 * Represents a screen title text component.
 *
 * This component is used to display the title of a screen in the application.
 *
 * @param modifier Optional [Modifier] to be applied to the text.
 * @param title The title text to be displayed.
 */
@Composable
fun ScreenTitleText(
    modifier: Modifier = Modifier,
    title: String
) {
    Text(
        text = title,
        style = MaterialTheme.typography.displaySmall,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = modifier
            .padding(8.dp)
    )
}