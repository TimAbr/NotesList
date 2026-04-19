package com.example.noteslist.presentation.notes_list

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteslist.domain.models.Note
import com.example.noteslist.domain.usecases.ObserveAllNotesUseCase
import com.example.noteslist.domain.usecases.SearchNotesUseCase
import com.example.noteslist.domain.usecases.UpdateNoteUseCase
import com.example.noteslist.presentation.notes_list.recycler_view.NoteListItem
import com.example.noteslist.presentation.notes_list.recycler_view.mapper.NoteListMapper
import com.example.noteslist.presentation.notes_list.recycler_view.mapper.SearchListMapper
import com.example.noteslist.presentation.notes_list.recycler_view.mapper.NoteStackKey
import com.example.noteslist.presentation.notes_list.recycler_view.mapper.NoteStackStateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesListViewModel @Inject constructor(
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val mapper: NoteListMapper,
    private val stateManager: NoteStackStateManager,
    private val observeAllNotesUseCase: ObserveAllNotesUseCase,
    private val searchNotesUseCase: SearchNotesUseCase,
    private val searchMapper: SearchListMapper
) : ViewModel() {

    private val _items = MutableStateFlow<List<NoteListItem>>(emptyList())
    val items = _items.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _viewEffect = Channel<ViewEffect>(Channel.BUFFERED)
    val viewEffect = _viewEffect.receiveAsFlow()

    private var cachedNotes: List<Note> = emptyList()
    var scrollState: Parcelable? = null
    private var preSearchScrollState: Parcelable? = null

    init {
        observeNotes()
    }

    @OptIn(FlowPreview::class)
    private fun observeNotes() {
        combine(
            observeAllNotesUseCase(),
            _searchQuery
                .debounce(300L)
                .distinctUntilChanged()
        ) { notes, query ->
            cachedNotes = notes
            val filteredNotes = searchNotesUseCase(query, notes)
            val adapterItems = if (query.isBlank()) {
                mapper.mapToAdapterItems(filteredNotes)
            } else {
                searchMapper.mapToAdapterItems(filteredNotes)
            }
            adapterItems
        }
        .onEach { adapterItems ->
            _items.value = adapterItems
        }
        .launchIn(viewModelScope)
    }

    fun onSearchQueryChange(query: String) {
        val oldQuery = _searchQuery.value
        _searchQuery.value = query

        viewModelScope.launch {
            when {
                oldQuery.isEmpty() && query.isNotEmpty() -> {
                    _viewEffect.send(ViewEffect.SaveScroll)
                    _viewEffect.send(ViewEffect.ScrollToTop)
                }
                oldQuery.isNotEmpty() && query.isEmpty() -> {
                    scrollState = preSearchScrollState
                }
                oldQuery.isNotEmpty() && query.isNotEmpty() -> {
                    _viewEffect.send(ViewEffect.ScrollToTop)
                }
            }
        }
    }

    fun onScrollStateCaptured(state: Parcelable?) {
        if (_searchQuery.value.isNotEmpty()) {
            preSearchScrollState = state
        }
    }

    fun getPreSearchScrollState(): Parcelable? = preSearchScrollState

    sealed class ViewEffect {
        object SaveScroll : ViewEffect()
        object ScrollToTop : ViewEffect()
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
