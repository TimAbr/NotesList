package com.example.noteslist.domain.repositories

import com.example.noteslist.domain.models.Note

interface NotesRepository {
    fun getAllNotes(): List<Note>
    fun updateNote(note: Note)
    fun addNote(note: Note)
    fun deleteNote(id: Int)
}