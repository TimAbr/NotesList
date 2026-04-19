package com.example.noteslist.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "notes")
data class NoteDbo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val text: String,
    val timestamp: Instant,
    val isImportant: Boolean,
    val isRead: Boolean
)