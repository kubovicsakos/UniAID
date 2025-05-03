package com.kakos.uniAID.notes.presentation.edit_notes.components

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
import androidx.compose.ui.unit.dp
import com.kakos.uniAID.core.domain.subject.domain.model.Subject

/**
 * Composable that displays a subject selection dropdown.
 *
 * @param modifier Modifier to be applied to the layout.
 * @param subjectName The currently selected subject name or null if none selected.
 * @param filteredSubjects List of available subjects to choose from.
 * @param onSubjectSelected Callback invoked when a subject is selected, receives subject ID or null.
 */
@Composable
fun NoteSubjectPickerDropdown(
    modifier: Modifier = Modifier,
    subjectName: String?,
    filteredSubjects: List<Subject>,
    onSubjectSelected: (Int?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Button(
            onClick = { expanded = true },
            content = { Text(subjectName ?: "None") }
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
                    text = { Text(subject.title) },
                    onClick = {
                        onSubjectSelected(subject.id)
                        expanded = false
                    }
                )
            }
        }
    }
}