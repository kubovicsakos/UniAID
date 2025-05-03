package com.kakos.uniAID.settings.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kakos.uniAID.core.presentation.components.dropdowns.NumberPickerDropdown
import com.kakos.uniAID.core.presentation.components.texts.OptionText
import com.kakos.uniAID.ui.theme.UniAidTheme

/**
 * Composable that displays a feature_settings list item with title and custom content.
 *
 * @param title Text displayed as the setting name or description.
 * @param content Composable content displayed on the right side of the setting item.
 */
@Composable
fun SettingItem(
    //modifier: Modifier = Modifier,
    title: String,
    content: @Composable (BoxScope.() -> Unit)
) {
    Spacer(modifier = Modifier.padding(8.dp))
    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            OptionText(
                text = title,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.weight(0.3f))
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterVertically)
            ) {
                content()
            }
        }
    }
}

@Preview
@Composable
private fun SettingItemP() {
    UniAidTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize()
        ) {
            SettingItem(
                title = "Current Semester",
                content = {
                    NumberPickerDropdown(
                        lowerBound = 1,
                        upperBound = 999,
                        defaultNumber = 1,
                        onNumberSelected = {},
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterEnd)
                    )
                }
            )
        }
    }
}