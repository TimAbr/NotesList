package com.example.noteslist.presentation.note_details

import android.os.Parcelable


sealed interface NoteDetailsScreenMode {
    object Create : NoteDetailsScreenMode

    data class Edit(val noteId: Long) : NoteDetailsScreenMode
}
