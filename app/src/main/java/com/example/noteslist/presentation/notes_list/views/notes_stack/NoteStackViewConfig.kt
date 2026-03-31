package com.example.noteslist.presentation.notes_list.views.notes_stack

import androidx.annotation.ColorInt

data class NoteStackViewConfig(
    val stackSpacing: Int,
    val stackMaxVisible: Int,
    val stackExpandedSpacing: Int,
    @ColorInt val collapseButtonColor: Int
)

