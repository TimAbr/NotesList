package com.example.noteslist.presentation.notes_list

import androidx.lifecycle.ViewModel
import com.example.noteslist.domain.models.Note
import com.example.noteslist.domain.repositories.NotesRepository
import com.example.noteslist.presentation.notes_list.recycler_view.NoteListItem
import com.example.noteslist.presentation.notes_list.recycler_view.mapper.NoteListMapper
import com.example.noteslist.presentation.notes_list.recycler_view.mapper.NoteStackKey
import com.example.noteslist.presentation.notes_list.recycler_view.mapper.NoteStackStateManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NotesListViewModel(
    private val repository: NotesRepository,
    private val mapper: NoteListMapper,
    private val stateManager: NoteStackStateManager
) : ViewModel() {

    private val _items = MutableStateFlow<List<NoteListItem>>(emptyList())
    val items = _items.asStateFlow()

    init {
        updateList()
    }

    fun onNoteClick(note: Note) {
        val updatedNote = note.copy(isRead = !note.isRead)
        repository.updateNote(updatedNote)
        updateList()
    }

    fun onStackClick(key: NoteStackKey) {
        stateManager.toggle(key)
        updateList()
    }

    private fun updateList() {
        val notes = repository.getAllNotes()
        _items.value = mapper.mapToAdapterItems(notes)
    }
}