package com.example.noteslist.domain.repositories

import com.example.noteslist.domain.models.Note
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    fun getAllNotes(): List<Note>
    fun observeAllNotes(): Flow<List<Note>>
    fun updateNote(note: Note)
    fun addNote(note: Note): Long
    fun deleteNote(id: Int)
}