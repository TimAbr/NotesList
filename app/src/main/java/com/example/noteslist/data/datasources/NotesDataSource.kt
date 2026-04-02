package com.example.noteslist.data.datasources

import com.example.noteslist.domain.models.Note

interface NotesDataSource {
    fun getAllNotes(): List<Note>
    fun updateNote(note: Note)
    fun addNote(note: Note)
    fun deleteNote(id: Long)
}
