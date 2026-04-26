package com.example.noteslist.presentation.notes_list.recycler_view

import com.example.noteslist.domain.models.Note
import com.example.noteslist.presentation.notes_list.recycler_view.mapper.NoteStackKey

sealed class NoteListItem {
    data class DateHeader(val title: String) : NoteListItem()
    data class SingleNote(val note: Note) : NoteListItem()
    data class NoteStack(
        val notes: List<Note>,
        val isExpanded: Boolean,
        val key: NoteStackKey,
        val stackSpacing: Int,
        val stackMaxVisible: Int,
    ) : NoteListItem()
}