package com.example.noteslist.presentation.notes_list.recycler_view

import java.time.LocalDate

interface NoteDateFormatter {
    fun format(date: LocalDate): String
}
