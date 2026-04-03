package com.example.noteslist.presentation.common.date_formatter

import java.time.Instant

interface NoteDateFormatter {
    fun format(instant: Instant): String
    fun parse(dateString: String): Instant
}