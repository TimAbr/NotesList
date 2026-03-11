package com.example.noteslist.presentation.notes_list

import java.time.Instant

interface NoteDateFormatter {
    fun format(instant: Instant): String
    fun parse(dateString: String): Instant
}