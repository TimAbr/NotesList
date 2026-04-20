package com.example.noteslist.presentation.note_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteslist.domain.models.Note
import com.example.noteslist.domain.usecases.notes.AddNoteUseCase
import com.example.noteslist.domain.usecases.notes.GetNoteByIdUseCase
import com.example.noteslist.domain.usecases.notes.UpdateNoteUseCase
import com.example.noteslist.presentation.common.date_formatter.NoteDateFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class NoteDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getNoteByIdUseCase: GetNoteByIdUseCase,
    private val addNoteUseCase: AddNoteUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val dateFormatter: NoteDateFormatter
) : ViewModel() {

    private val mode: NoteDetailsScreenMode = NoteDetailsFragmentArgs
        .fromSavedStateHandle(savedStateHandle)
        .mode

    private var initialNote: Note? = null

    private val _state = MutableStateFlow(
        NoteDetailsScreenState(mode = mode)
    )
    
    init {
        if (mode is NoteDetailsScreenMode.Edit) {
            viewModelScope.launch {
                val state = getNoteDetailsState(mode.noteId)
                initialNote = getNoteByIdUseCase(mode.noteId)
                _state.value = state
            }
        }
    }
    val state: StateFlow<NoteDetailsScreenState> = _state.asStateFlow()

    private val _navigationChannel = Channel<Unit>(Channel.BUFFERED)
    val navigationFlow = _navigationChannel.receiveAsFlow()

    private suspend fun getNoteDetailsState(noteId: Long): NoteDetailsScreenState {
        val note = getNoteByIdUseCase(noteId)
        if (note != null) {
            return NoteDetailsScreenState(
                title = note.title,
                text = note.text,
                isImportant = note.isImportant,
                isRead = note.isRead,
                creationTimestamp = note.timestamp,
                formattedDate = dateFormatter.format(note.timestamp),
                mode = NoteDetailsScreenMode.Edit(noteId)
            )
        }
        return NoteDetailsScreenState()
    }

    fun isDirty(): Boolean {
        val currentState = _state.value
        return if (currentState.mode is NoteDetailsScreenMode.Edit) {
            initialNote?.let { initial ->
                currentState.title != initial.title ||
                        currentState.text != initial.text ||
                        currentState.isImportant != initial.isImportant ||
                        currentState.isRead != initial.isRead
            } ?: false
        } else {
            currentState.title.isNotBlank() || currentState.text.isNotBlank()
        }
    }

    private var validationJob: Job? = null

    fun onTitleChange(newTitle: String) {
        _state.update { it.copy(title = newTitle) }

        validationJob?.cancel()

        viewModelScope.launch(Dispatchers.Default) {
            _state.update {
                it.copy(
                    title = newTitle,
                    titleError = if (newTitle.length > MAX_TITLE_LENGTH)
                        TitleValidationError.TOO_LONG
                    else
                        null

                )
            }
        }
    }

    fun onTextChange(newText: String) {
        _state.update { it.copy(text = newText) }
    }

    fun onImportantToggle(important: Boolean) {
        _state.update { it.copy(isImportant = important) }
    }

    fun onReadToggle(read: Boolean) {
        _state.update { it.copy(isRead = read) }
    }

    fun onSave() {
        val currentState = _state.value
        if (currentState.title.isBlank()) {
            _state.update { it.copy(titleError = TitleValidationError.EMPTY) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }

            when (val mode = currentState.mode) {
                is NoteDetailsScreenMode.Create -> {
                    val newNote = Note(
                        title = currentState.title,
                        text = currentState.text,
                        isImportant = currentState.isImportant,
                        isRead = currentState.isRead
                    )
                    val id = addNoteUseCase(newNote)
                    val insertedNote = getNoteByIdUseCase(id)
                    if (insertedNote != null) {
                        initialNote = insertedNote
                        _state.value = getNoteDetailsState(id)
                    }
                }

                is NoteDetailsScreenMode.Edit -> {
                    val updatedNote = Note(
                        id = mode.noteId,
                        title = currentState.title,
                        text = currentState.text,
                        timestamp = currentState.creationTimestamp ?: Instant.now(),
                        isImportant = currentState.isImportant,
                        isRead = currentState.isRead
                    )
                    updateNoteUseCase(updatedNote)
                    initialNote = updatedNote
                }
            }

            _state.update { it.copy(isSaving = false) }
            _navigationChannel.send(Unit)
        }
    }

    companion object {
        private const val MAX_TITLE_LENGTH = 50
    }
}

data class NoteDetailsScreenState(
    val title: String = "",
    val text: String = "",
    val isImportant: Boolean = false,
    val isRead: Boolean = false,
    val creationTimestamp: Instant? = null,
    val formattedDate: String = "",
    val titleError: TitleValidationError? = null,
    val mode: NoteDetailsScreenMode = NoteDetailsScreenMode.Create,
    val isSaving: Boolean = false,
)

enum class TitleValidationError {
    EMPTY,
    TOO_LONG,
}
