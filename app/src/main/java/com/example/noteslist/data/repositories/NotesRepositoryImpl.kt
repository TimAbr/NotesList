package com.example.noteslist.data.repositories

import com.example.noteslist.data.datasources.NotesDataSource
import com.example.noteslist.data.mappers.toDomainModel
import com.example.noteslist.data.mappers.toEntity
import com.example.noteslist.domain.models.Note
import com.example.noteslist.domain.repositories.NotesRepository
import dagger.hilt.components.SingletonComponent
import it.czerwinski.android.hilt.annotations.BoundTo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

@BoundTo(supertype = NotesRepository::class, component = SingletonComponent::class)
class NotesRepositoryImpl @Inject constructor(
    private val dataSource: NotesDataSource
) : NotesRepository {

    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun getAllNotes(): List<Note> = withContext(dispatcher) {
        dataSource.getAllNotes().map { it.toDomainModel() }
    }

    override fun observeAllNotes(): Flow<List<Note>> =
        dataSource.notesFlow.map { entities -> 
            entities.map { it.toDomainModel() }
        }.flowOn(dispatcher)

    override suspend fun updateNote(note: Note) = withContext(dispatcher) {
        dataSource.updateNote(note.toEntity())
    }

    override suspend fun addNote(note: Note) = withContext(dispatcher) {
        dataSource.addNote(note.toEntity())
    }

    override suspend fun deleteNote(id: Int) = withContext(dispatcher) {
        dataSource.deleteNote(id.toLong())
    }
}
