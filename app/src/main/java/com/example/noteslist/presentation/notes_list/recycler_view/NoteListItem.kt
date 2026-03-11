package com.example.noteslist.presentation.notes_list.recycler_view

import com.example.noteslist.domain.models.Note
import java.time.LocalDate

sealed class NoteListItem {
    data class DateHeader(val title: String) : NoteListItem()
    data class SingleNote(val note: Note) : NoteListItem()
    data class NoteStack(
        val notes: List<Note>,
        val isExpanded: Boolean,
        val date: LocalDate
    ) : NoteListItem()
}