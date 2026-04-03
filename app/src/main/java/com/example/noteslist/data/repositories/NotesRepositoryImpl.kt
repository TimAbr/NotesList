package com.example.noteslist.data.repositories

import com.example.noteslist.data.datasources.NotesDataSource
import com.example.noteslist.domain.models.Note
import com.example.noteslist.domain.repositories.NotesRepository
import dagger.hilt.components.SingletonComponent
import it.czerwinski.android.hilt.annotations.BoundTo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@BoundTo(supertype = NotesRepository::class, component = SingletonComponent::class)
class NotesRepositoryImpl @Inject constructor(
    private val dataSource: NotesDataSource
) : NotesRepository {

    override fun getAllNotes(): List<Note> = dataSource.getAllNotes()
    override fun observeAllNotes(): Flow<List<Note>> =
        dataSource.notesFlow


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
