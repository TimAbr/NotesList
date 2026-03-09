package com.example.noteslist.presentation.notes_list.recycler_view

import com.example.noteslist.domain.models.Note
import java.time.ZoneId

class NoteListMapper {
    fun mapToAdapterItems(notes: List<Note>): List<NoteListItem> {
        val result = mutableListOf<NoteListItem>()
        
        val groupedByDate = notes.groupBy { 
            it.timestamp.atZone(ZoneId.systemDefault()).toLocalDate() 
        }.toSortedMap(compareByDescending { it })

        groupedByDate.forEach { (date, notesInDate) ->
            result.add(NoteListItem.DateHeader(date))

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
}
