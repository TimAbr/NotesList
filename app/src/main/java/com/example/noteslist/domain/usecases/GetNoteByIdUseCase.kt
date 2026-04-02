package com.example.noteslist.domain.usecases

import com.example.noteslist.domain.models.Note
import com.example.noteslist.domain.repositories.NotesRepository

class GetNoteByIdUseCase(private val repository: NotesRepository) {
    operator fun invoke(id: Long): Note? {
        return repository.getAllNotes().find { it.id == id }
    }
}
