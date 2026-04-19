package com.example.noteslist.data.datasources.notes.local

import com.example.noteslist.data.datasources.NotesDataSource
import com.example.noteslist.data.models.NoteDbo
import dagger.hilt.components.SingletonComponent
import it.czerwinski.android.hilt.annotations.BoundTo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@BoundTo(supertype = NotesDataSource::class, component = SingletonComponent::class)
@Singleton
class RoomNotesDataSource @Inject constructor(
    private val appDatabase: AppDatabase
) : NotesDataSource {

    private val dao = appDatabase.noteDao()

    override val notesFlow: Flow<List<NoteDbo>> = dao.getAllNotesFlow()

    override suspend fun getAllNotes(): List<NoteDbo> {
        return dao.getAllNotes()
    }

    override suspend fun updateNote(note: NoteDbo) {
        dao.updateNote(note)
    }

    override suspend fun addNote(note: NoteDbo): Long {
        return dao.insertNote(note)
    }

    override suspend fun deleteNote(id: Long) {
        dao.deleteNoteById(id)
    }
}
