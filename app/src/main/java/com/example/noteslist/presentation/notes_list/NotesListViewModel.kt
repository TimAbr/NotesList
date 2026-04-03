package com.example.noteslist.presentation.notes_list

import androidx.lifecycle.ViewModel
import com.example.noteslist.domain.models.Note
import com.example.noteslist.domain.usecases.GetAllNotesUseCase
import com.example.noteslist.domain.usecases.UpdateNoteUseCase
import com.example.noteslist.presentation.notes_list.recycler_view.NoteListItem
import com.example.noteslist.presentation.notes_list.recycler_view.mapper.NoteListMapper
import com.example.noteslist.presentation.notes_list.recycler_view.mapper.NoteStackKey
import com.example.noteslist.presentation.notes_list.recycler_view.mapper.NoteStackStateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class NotesListViewModel @Inject constructor(
    private val getAllNotesUseCase: GetAllNotesUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val mapper: NoteListMapper,
    private val stateManager: NoteStackStateManager
) : ViewModel() {

    private val _items = MutableStateFlow<List<NoteListItem>>(emptyList())
    val items = _items.asStateFlow()

    private var cachedNotes: List<Note> = emptyList()

    init {
        updateList()
    }

    fun onNoteClick(id: Long) {
        val note = cachedNotes.find { it.id == id } ?: return
        val updatedNote = note.copy(isRead = !note.isRead)
        updateNoteUseCase(updatedNote)
        updateList()
    }

    fun onStackClick(key: NoteStackKey) {
        stateManager.toggle(key)
        mapList(cachedNotes)
    }

    private fun updateList() {
        val notes = getAllNotesUseCase()
        cachedNotes = notes
        mapList(notes)
    }

    private fun mapList(list: List<Note>) {
        _items.value = mapper.mapToAdapterItems(list)
    }
}
