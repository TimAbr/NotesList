package com.example.noteslist.presentation.notes_list.recycler_view.mapper

fun interface NoteStackStateProvider {
    fun isExpanded(key: NoteStackKey): Boolean
}