package com.example.noteslist.domain.models

import java.time.Instant

data class Note(
    val id: Long = 0L,
    val title: String,
    val text: String,
    val timestamp: Instant = Instant.now(),
    val isImportant: Boolean = false,
    val isRead: Boolean = false
)
