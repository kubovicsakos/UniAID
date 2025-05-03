package com.kakos.uniAID.calendar.presentation.edit_event.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Composable that displays a labeled text field for event editing.
 *
 * Provides a styled text input component with integrated label and optional error
 * messaging, enabling user input for event details with visual feedback for
 * validation errors in a consistent formatting style.
 *
 * @param text Current text value to display in the field.
 * @param label Text label describing the field's purpose.
 * @param modifier Modifier to be applied to the layout.
 * @param error Optional error message to display when validation fails.
 * @param onValueChange Callback invoked when text content changes.
 * @param textStyle Style to be applied to the input text.
 * @param singleLine Whether to restrict input to a single line.
 */
@Composable
fun EditEventTextField(
    text: String,
    label: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    onValueChange: (String) -> Unit,
    textStyle: TextStyle = TextStyle(),
    singleLine: Boolean = false,
) {
    Column(modifier = modifier.wrapContentSize()) {
        TextField(
            value = text,
            onValueChange = onValueChange,
            label = { Text(text = label) },
            singleLine = singleLine,
            textStyle = textStyle,
            isError = error != null,
            modifier = Modifier.fillMaxSize(),
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            )
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

@Preview
@Composable
private fun EditEventP() {
    EditEventTextField(
        text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean ac est mattis, pretium felis luctus, volutpat sapien. Nunc eget tristique nunc. Mauris dui eros, tempus id ex in, semper auctor justo. Phasellus egestas, diam sed molestie maximus, magna sem condimentum tellus, at elementum risus risus at eros. Vivamus sed nisi elementum, pretium justo id, iaculis nunc. Donec vel enim consequat elit feugiat cursus ut et velit. Sed auctor dolor vel pellentesque ultrices. Nunc euismod libero eu pharetra euismod. Morbi sodales elit eu erat condimentum pellentesque non et leo. Aliquam eget vehicula est.\n" +
                "\n" +
                "Cras ac tempor ligula. Quisque non cursus urna. Mauris nec consequat nunc. Nulla in vehicula sapien. Interdum et malesuada fames ac ante ipsum primis in faucibus. Vivamus gravida consequat vestibulum. Donec dapibus blandit aliquet. Fusce non lacinia ex. Nullam elit mauris, consequat eget libero sit amet, egestas euismod ligula. Duis mauris libero, iaculis sit amet scelerisque at, blandit ac massa. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Morbi eros nibh, ultrices vitae dui id, pellentesque consectetur libero. Quisque et gravida felis. Suspendisse ac dapibus ipsum. Duis id fermentum elit. Nunc vel hendrerit massa.\n" +
                "\n" +
                "Morbi commodo placerat justo, in porttitor lorem. Quisque at commodo erat. Duis eget risus non nulla laoreet dignissim at in augue. Morbi nec tellus imperdiet, semper velit at, elementum sem. Suspendisse quis fringilla mi. Sed venenatis dui eget quam dapibus, id vehicula urna bibendum. Sed aliquam ultricies lorem, ac ultrices nibh aliquet quis. Proin tincidunt est a consectetur cursus. Donec a lacinia justo. Cras vel dui pulvinar, luctus est id, lobortis neque. Cras accumsan sed arcu eget pellentesque. Nulla a molestie odio. Vivamus eget lobortis quam.",
        label = "what is this",
        onValueChange = {},
        error = "This is an error",
        textStyle = MaterialTheme.typography.bodyLarge,
        singleLine = false,
        modifier = Modifier.padding(16.dp)
    )

}