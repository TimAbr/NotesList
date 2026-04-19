package com.example.noteslist.data.datasources

import com.example.noteslist.data.models.NoteDbo
import kotlinx.coroutines.flow.Flow

interface NotesDataSource {
    suspend fun getAllNotes(): List<NoteDbo>
    suspend fun updateNote(note: NoteDbo)
    suspend fun addNote(note: NoteDbo): Long
    suspend fun deleteNote(id: Long)
    val notesFlow: Flow<List<NoteDbo>>
}
