package com.example.noteslist.data.datasources

import com.example.noteslist.domain.models.Note
import kotlinx.coroutines.flow.Flow

interface NotesDataSource {
    fun getAllNotes(): List<Note>
    fun updateNote(note: Note)
    fun addNote(note: Note): Long
    fun deleteNote(id: Long)
    val notesFlow: Flow<List<Note>>
}
