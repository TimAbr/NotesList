package com.example.noteslist.presentation.notes_list.views.note

data class NoteViewConfig(
    val cornerRadius: Float,
    val noteElevation: Float,
    val titleTextSize: Float,
    val bodyTextSize: Float,
    val dateTextSize: Float,
    val unreadColors: NoteColors,
    val readColors: NoteColors
)

