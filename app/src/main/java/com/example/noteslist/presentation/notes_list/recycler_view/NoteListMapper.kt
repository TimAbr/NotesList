package com.example.noteslist.presentation.notes_list.recycler_view

import com.example.noteslist.domain.models.Note
import com.example.noteslist.presentation.notes_list.NoteDateFormatter
import java.time.ZoneId

class NoteListMapper(
    private val dateFormatter: NoteDateFormatter
) {
    fun mapToAdapterItems(notes: List<Note>): List<NoteListItem> {
        val result = mutableListOf<NoteListItem>()
        
        val groupedByDate = notes.groupBy { 
            it.timestamp.atZone(ZoneId.systemDefault()).toLocalDate() 
        }.toSortedMap(compareByDescending { it })

        groupedByDate.forEach { (date, notesInDate) ->
            result.add(
                NoteListItem.DateHeader(
                    dateFormatter.format(
                        date.atStartOfDay(SYSTEM_ZONE
                        ).toInstant()
                    )
                )
            )

            val sortedNotes = notesInDate.sortedByDescending { it.timestamp }

            val (importantNotes, regularNotes) = sortedNotes.partition { it.isImportant }

            importantNotes.forEach { 
                result.add(NoteListItem.SingleNote(it)) 
            }

            if (regularNotes.isNotEmpty()) {
                result.add(NoteListItem.NoteStack(regularNotes))
            }
        }

        return result
    }

    companion object{
        private val SYSTEM_ZONE = ZoneId.systemDefault()
    }
}
