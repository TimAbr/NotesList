package com.example.noteslist.domain.usecases.notes

import com.example.noteslist.domain.models.Note
import com.example.noteslist.domain.repositories.NotesRepository
import javax.inject.Inject

class GetAllNotesUseCase @Inject constructor(private val repository: NotesRepository) {
    suspend operator fun invoke(): List<Note> {
        return repository.getAllNotes()
    }
}
