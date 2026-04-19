package com.example.noteslist.presentation.notes_list.recycler_view.mapper

import com.example.noteslist.domain.models.Note
import com.example.noteslist.presentation.notes_list.recycler_view.NoteListItem
import javax.inject.Inject

class SearchListMapper @Inject constructor() {
    fun mapToAdapterItems(notes: List<Note>): List<NoteListItem> {
        return notes.map { NoteListItem.SingleNote(it) }
    }
}
