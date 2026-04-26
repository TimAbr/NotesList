package com.example.noteslist.presentation.notes_list

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteslist.domain.models.Note
import com.example.noteslist.domain.usecases.app_status.CompleteFirstLaunchUseCase
import com.example.noteslist.domain.usecases.app_status.IsFirstLaunchUseCase
import com.example.noteslist.domain.usecases.notes.ObserveAllNotesUseCase
import com.example.noteslist.domain.usecases.notes.UpdateNoteUseCase
import com.example.noteslist.domain.usecases.search.SearchNotesUseCase
import com.example.noteslist.presentation.notes_list.recycler_view.NoteListItem
import com.example.noteslist.presentation.notes_list.recycler_view.mapper.NoteListMapper
import com.example.noteslist.presentation.notes_list.recycler_view.mapper.NoteStackKey
import com.example.noteslist.presentation.notes_list.recycler_view.mapper.NoteStackStateManager
import com.example.noteslist.presentation.notes_list.recycler_view.mapper.SearchListMapper
import com.example.noteslist.domain.usecases.settings.ObserveAppSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
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
    private val searchMapper: SearchListMapper,
    private val isFirstLaunchUseCase: IsFirstLaunchUseCase,
    private val completeFirstLaunchUseCase: CompleteFirstLaunchUseCase,
    private val observeAppSettingsUseCase: ObserveAppSettingsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<NotesListUIState>(
        NotesListUIState.Loading
    )
    val state = _state.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _viewEffect = Channel<ViewEffect>(Channel.BUFFERED)
    val viewEffect = _viewEffect.receiveAsFlow()

    private var cachedNotes: List<Note> = emptyList()
    var scrollState: Parcelable? = null
    private var preSearchScrollState: Parcelable? = null

    private val stackUpdateChannel = Channel<Unit>(Channel.BUFFERED)

    init {
        if (!isFirstLaunchUseCase()){
            _state.value = NotesListUIState.Content(emptyList())
        }

        initScreen()
    }

    private fun initScreen() {
        viewModelScope.launch {
            observeNotes()
        }
    }

    private fun observeNotes() {
        var shouldHandleFirstLaunch = isFirstLaunchUseCase()
        val startTime = System.currentTimeMillis()

        combine(
            observeAllNotesUseCase(),
            _searchQuery
                .debounce(300L)
                .distinctUntilChanged(),
            observeAppSettingsUseCase(),
            stackUpdateChannel.receiveAsFlow().onStart { emit(Unit) }
        ) { notes, query, settings, _ ->
            cachedNotes = notes
            val filteredNotes = searchNotesUseCase(query, notes)
            val adapterItems = if (query.isBlank()) {
                mapper.mapToAdapterItems(filteredNotes, settings)
            } else {
                searchMapper.mapToAdapterItems(filteredNotes)
            }
            adapterItems
        }
        .onEach { adapterItems ->
            if (shouldHandleFirstLaunch) {
                handleFirstLaunch(startTime)
                shouldHandleFirstLaunch = false
            }

            _state.value = if (adapterItems.isEmpty() && _searchQuery.value.isEmpty()) {
                NotesListUIState.Empty
            } else {
                NotesListUIState.Content(adapterItems)
            }
        }
        .launchIn(viewModelScope)
    }

    private suspend fun handleFirstLaunch(startTime: Long) {
        val currentTime = System.currentTimeMillis()
        val duration = currentTime - startTime
        if (duration < SHIMMER_MIN_DURATION_MS) {
            delay(SHIMMER_MIN_DURATION_MS - duration)
        }

        completeFirstLaunchUseCase()
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

    fun onStackClick(key: NoteStackKey) {
        stateManager.toggle(key)
        stackUpdateChannel.trySend(Unit)
    }

    sealed class NotesListUIState {
        object Loading : NotesListUIState()
        data class Content(val items: List<NoteListItem>) : NotesListUIState()
        object Empty : NotesListUIState()
    }

    sealed class ViewEffect {
        object SaveScroll : ViewEffect()
        object ScrollToTop : ViewEffect()
    }

    companion object {
        private const val SHIMMER_MIN_DURATION_MS = 500L
    }
}
