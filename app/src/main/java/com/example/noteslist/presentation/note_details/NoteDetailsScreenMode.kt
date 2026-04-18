package com.example.noteslist.presentation.note_details

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface NoteDetailsScreenMode : Parcelable {
    @Parcelize
    data object Create : NoteDetailsScreenMode

    @Parcelize
    data class Edit(val noteId: Long) : NoteDetailsScreenMode
}
