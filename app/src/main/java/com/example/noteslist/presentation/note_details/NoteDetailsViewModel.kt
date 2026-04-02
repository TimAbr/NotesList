package com.example.noteslist.presentation.note_details

import androidx.lifecycle.ViewModel
import com.example.noteslist.domain.models.Note
import com.example.noteslist.domain.usecases.AddNoteUseCase
import com.example.noteslist.domain.usecases.GetNoteByIdUseCase
import com.example.noteslist.domain.usecases.UpdateNoteUseCase
import com.example.noteslist.presentation.common.NoteDateFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.Instant

class NoteDetailsViewModel(
    private val mode: NoteDetailsScreenMode,
    private val getNoteByIdUseCase: GetNoteByIdUseCase,
    private val addNoteUseCase: AddNoteUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val dateFormatter: NoteDateFormatter
) : ViewModel() {

    private val _state = MutableStateFlow(
        when (mode) {
            is NoteDetailsScreenMode.Edit -> {
                getNoteDetailsState(mode.noteId)
            }
            is NoteDetailsScreenMode.Create -> {
                NoteDetailsScreenState(
                    mode = mode
                )
            }
        }
    )
    val state: StateFlow<NoteDetailsScreenState> = _state.asStateFlow()


    private fun getNoteDetailsState(noteId: Long): NoteDetailsScreenState {
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

    fun onTitleChange(newTitle: String) {
        _state.update { it.copy(title = newTitle, titleError = false) }
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
            _state.update { it.copy(titleError = true) }
            return
        }

        when (val mode = currentState.mode) {
            is NoteDetailsScreenMode.Create -> {
                val newNote = Note(
                    id = System.currentTimeMillis(),
                    title = currentState.title,
                    text = currentState.text,
                    timestamp = Instant.now(),
                    isImportant = currentState.isImportant,
                    isRead = currentState.isRead
                )
                addNoteUseCase(newNote)
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
            }
        }
    }
}

data class NoteDetailsScreenState(
    val title: String = "",
    val text: String = "",
    val isImportant: Boolean = false,
    val isRead: Boolean = false,
    val creationTimestamp: Instant? = null,
    val formattedDate: String = "",
    val titleError: Boolean = false,
    val mode: NoteDetailsScreenMode = NoteDetailsScreenMode.Create
)
