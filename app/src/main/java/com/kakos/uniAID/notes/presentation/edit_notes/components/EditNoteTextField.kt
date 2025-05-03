package com.kakos.uniAID.notes.presentation.edit_notes.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import com.kakos.uniAID.ui.theme.dark_note_color
import com.kakos.uniAID.ui.theme.light_note_color

/**
 * Composable that displays a customizable text field for note editing.
 *
 * @param text The current text value of the text field.
 * @param hint The hint text to display when the field is empty and not focused.
 * @param modifier Modifier to apply to the text field.
 * @param isHintVisible Controls visibility of the hint text.
 * @param isDarkTheme Determines the text color based on theme mode.
 * @param fontSize The font size for the text field content.
 * @param onValueChange Callback invoked when text value changes.
 * @param singleLine Whether the text field allows only a single line of text.
 * @param onFocusChange Callback invoked when focus state changes.
 */
@Composable
fun EditNoteTextField(
    text: String,
    hint: String,
    modifier: Modifier = Modifier,
    isHintVisible: Boolean = true,
    isDarkTheme: Boolean,
    fontSize: TextUnit = MaterialTheme.typography.headlineMedium.fontSize,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = false,
    onFocusChange: (FocusState) -> Unit,
) {
    Column(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            BasicTextField(
                value = text,
                onValueChange = onValueChange,
                singleLine = singleLine,
                textStyle = TextStyle(
                    color = if (isDarkTheme) light_note_color else dark_note_color,
                    fontSize = fontSize
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .matchParentSize()
                    .onFocusChanged {
                        onFocusChange(it)
                    }
            )
            if (isHintVisible) {
                Text(
                    text = hint,
                    style = TextStyle(
                        color = if (isDarkTheme) light_note_color else dark_note_color,
                        fontSize = fontSize
                    )
                )
            }
        }
    }
}