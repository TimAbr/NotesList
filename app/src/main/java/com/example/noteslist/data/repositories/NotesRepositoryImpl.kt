package com.example.noteslist.data.repositories

import com.example.noteslist.data.datasources.NotesDataSource
import com.example.noteslist.domain.models.Note
import com.example.noteslist.domain.repositories.NotesRepository

class NotesRepositoryImpl(
    private val dataSource: NotesDataSource
) : NotesRepository {

    override fun getAllNotes(): List<Note> = dataSource.getAllNotes()

    override fun updateNote(note: Note) {
        dataSource.updateNote(note)
    }

    override fun addNote(note: Note) {
        dataSource.addNote(note)
    }

    override fun deleteNote(id: Int) {
        dataSource.deleteNote(id.toLong())
    }
}
