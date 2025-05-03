package com.kakos.uniAID.notes.presentation.notes.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kakos.uniAID.notes.domain.util.NoteOrder
import com.kakos.uniAID.notes.domain.util.OrderType

/**
 * Composable that displays note ordering controls.
 *
 * @param modifier Modifier for styling and layout adjustments.
 * @param noteOrder Current ordering configuration.
 * @param onOrderChange Callback for when order selection changes.
 */
@Composable
fun OrderSection(
    modifier: Modifier = Modifier,
    noteOrder: NoteOrder = NoteOrder.Date(OrderType.Descending),
    onOrderChange: (NoteOrder) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            RadioButtonDefault(
                text = "Date",
                select = noteOrder is NoteOrder.Date,
                onSelect = { onOrderChange(NoteOrder.Date(noteOrder.orderType)) }
            )

            Spacer(modifier = Modifier.width(8.dp))
            RadioButtonDefault(
                text = "Title",
                select = noteOrder is NoteOrder.Title,
                onSelect = { onOrderChange(NoteOrder.Title(noteOrder.orderType)) }
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {

            RadioButtonDefault(
                text = "Descending",
                select = noteOrder.orderType is OrderType.Descending,
                onSelect = {
                    onOrderChange(noteOrder.copy(OrderType.Descending))
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            RadioButtonDefault(
                text = "Ascending",
                select = noteOrder.orderType is OrderType.Ascending,
                onSelect = {
                    onOrderChange(noteOrder.copy(OrderType.Ascending))
                }
            )
        }
    }
}