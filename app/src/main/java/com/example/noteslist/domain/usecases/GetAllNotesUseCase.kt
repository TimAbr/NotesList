package com.example.noteslist.domain.usecases

import com.example.noteslist.domain.models.Note
import com.example.noteslist.domain.repositories.NotesRepository

class GetAllNotesUseCase(private val repository: NotesRepository) {
    operator fun invoke(): List<Note> {
        return repository.getAllNotes()
    }
}
