package com.example.noteslist.domain.usecases

import com.example.noteslist.domain.models.Note
import com.example.noteslist.domain.repositories.NotesRepository
import javax.inject.Inject

class GetNoteByIdUseCase @Inject constructor(private val repository: NotesRepository) {
    suspend operator fun invoke(id: Long): Note? {
        return repository.getAllNotes().find { it.id == id }
    }
}
