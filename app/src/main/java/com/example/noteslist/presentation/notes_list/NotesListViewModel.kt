package com.example.noteslist.presentation.notes_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteslist.domain.models.Note
import com.example.noteslist.domain.usecases.ObserveAllNotesUseCase
import com.example.noteslist.domain.usecases.UpdateNoteUseCase
import com.example.noteslist.presentation.notes_list.recycler_view.NoteListItem
import com.example.noteslist.presentation.notes_list.recycler_view.mapper.NoteListMapper
import com.example.noteslist.presentation.notes_list.recycler_view.mapper.NoteStackKey
import com.example.noteslist.presentation.notes_list.recycler_view.mapper.NoteStackStateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesListViewModel @Inject constructor(
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val mapper: NoteListMapper,
    private val stateManager: NoteStackStateManager,
    private val observeAllNotesUseCase: ObserveAllNotesUseCase
) : ViewModel() {

    private val _items = MutableStateFlow<List<NoteListItem>>(emptyList())
    val items = _items.asStateFlow()

    private var cachedNotes: List<Note> = emptyList()

    init {
        observeNotes()
    }

    private fun observeNotes() {
        viewModelScope.launch {
            observeAllNotesUseCase().collect { notes ->
                cachedNotes = notes
                mapList(notes)
            }
        }
    }


    fun onNoteClick(id: Long) {
        val note = cachedNotes.find { it.id == id } ?: return
        val updatedNote = note.copy(isRead = !note.isRead)
        updateNoteUseCase(updatedNote)
    }

    fun onStackClick(key: NoteStackKey) {
        stateManager.toggle(key)
        mapList(cachedNotes)
    }


    private fun mapList(list: List<Note>) {
        _items.value = mapper.mapToAdapterItems(list)
    }
}
