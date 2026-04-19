package com.example.noteslist.domain.repositories

import com.example.noteslist.domain.models.Note
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    suspend fun getAllNotes(): List<Note>
    fun observeAllNotes(): Flow<List<Note>>
    suspend fun updateNote(note: Note)
    suspend fun addNote(note: Note): Long
    suspend fun deleteNote(id: Int)
}