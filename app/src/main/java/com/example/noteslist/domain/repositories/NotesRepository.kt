package com.example.noteslist.domain.repositories

import com.example.noteslist.domain.models.Note

interface NotesRepository {
    fun getAllNotes(): List<Note>
}