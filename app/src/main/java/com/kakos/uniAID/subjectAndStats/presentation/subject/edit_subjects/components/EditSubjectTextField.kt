package com.kakos.uniAID.subjectAndStats.presentation.subject.edit_subjects.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

/**
 * Composable that displays a styled text field for subject editing.
 *
 * @param text The current text value to display.
 * @param label The label text shown above the field.
 * @param modifier Optional modifier for styling and layout.
 * @param error Optional error message to display below the field.
 * @param onValueChange Callback triggered when text content changes.
 * @param textStyle Optional text style applied to the field content.
 * @param singleLine Whether text should be constrained to a single line.
 */
@Composable
fun EditSubjectTextField(
    text: String,
    label: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    onValueChange: (String) -> Unit,
    textStyle: TextStyle = TextStyle(),
    singleLine: Boolean = false,
) {
    Column(
        modifier = modifier
            .wrapContentSize()
            .fillMaxWidth()
    ) {
        TextField(
            value = text,
            onValueChange = onValueChange,
            label = { Text(text = label) },
            singleLine = singleLine,
            textStyle = textStyle,
            isError = error != null,
            modifier = modifier
                .wrapContentSize()
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
        )

        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}