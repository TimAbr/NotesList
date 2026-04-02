package com.example.noteslist.domain.usecases

import com.example.noteslist.domain.models.Note
import com.example.noteslist.domain.repositories.NotesRepository

class AddNoteUseCase(private val repository: NotesRepository) {
    operator fun invoke(note: Note) {
        repository.addNote(note)
    }
}
