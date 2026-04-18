package com.example.noteslist.domain.usecases

import com.example.noteslist.domain.models.Note
import com.example.noteslist.domain.repositories.NotesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAllNotesUseCase @Inject constructor(private val repository: NotesRepository) {
    operator fun invoke(): Flow<List<Note>> {
        return repository.observeAllNotes()
    }
}
