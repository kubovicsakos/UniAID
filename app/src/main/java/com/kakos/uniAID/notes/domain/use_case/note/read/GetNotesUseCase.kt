package com.kakos.uniAID.notes.domain.use_case.note.read

import android.util.Log
import com.kakos.uniAID.notes.domain.model.Note
import com.kakos.uniAID.notes.domain.repository.NoteRepository
import com.kakos.uniAID.notes.domain.util.NoteOrder
import com.kakos.uniAID.notes.domain.util.OrderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Use case for retrieving a list of notes.
 *
 * Encapsulates the logic for fetching notes from the repository,
 * handling ordering by title or date, and providing a flow of the results.
 *
 * @property repository The repository used for accessing note data.
 */
class GetNotesUseCase(
    private var repository: NoteRepository
) {
    private val tag = "GetNotesUseCase"

    operator fun invoke(
        noteOrder: NoteOrder = NoteOrder.Date(OrderType.Descending) // by default, order notes by date in descending order
    ): Flow<List<Note>> {
        Log.d(tag, "Getting notes with order: $noteOrder")

        return repository.getNotes().map { notes ->
            when (noteOrder.orderType) {
                is OrderType.Ascending -> {
                    when (noteOrder) {
                        is NoteOrder.Title -> notes.sortedBy { it.title.lowercase() }
                        is NoteOrder.Date -> notes.sortedBy { it.lastModified }
                    }
                }

                is OrderType.Descending -> {
                    when (noteOrder) {
                        is NoteOrder.Title -> notes.sortedByDescending { it.title.lowercase() }
                        is NoteOrder.Date -> notes.sortedByDescending { it.lastModified }
                    }
                }
            }
        }
    }

}