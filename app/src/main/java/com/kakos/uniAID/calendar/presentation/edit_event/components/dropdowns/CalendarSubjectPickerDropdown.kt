package com.kakos.uniAID.calendar.presentation.edit_event.components.dropdowns

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kakos.uniAID.core.domain.subject.domain.model.Subject


/**
 * Composable that displays a subject picker dropdown for event association wth subject.
 *
 * Provides a button-activated dropdown interface for selecting academic subjects,
 * enabling users to associate calendar events with specific courses or select
 * none when no specific subject association is desired.
 *
 * @param modifier Modifier to be applied to the layout.
 * @param subjectName The currently selected subject name to display on the button.
 * @param filteredSubjects List of available subjects to display in the dropdown.
 * @param onSubjectSelected Callback invoked when user selects a subject or none.
 */
@Composable
fun CalendarSubjectPickerDropdown(
    modifier: Modifier = Modifier,
    subjectName: String?,
    filteredSubjects: List<Subject>,
    onSubjectSelected: (Int?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Button(
            onClick = { expanded = true },
            content = {
                Text(
                    subjectName ?: "None",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = modifier.height(200.dp)
        ) {
            DropdownMenuItem(
                text = { Text("None") },
                onClick = {
                    onSubjectSelected(null)
                    expanded = false
                }
            )
            filteredSubjects.forEach { subject ->
                DropdownMenuItem(
                    text = {
                        Text(
                            subject.title,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                        )
                    },
                    onClick = {
                        onSubjectSelected(subject.id!!)
                        expanded = false
                    }
                )
            }
        }
    }
}