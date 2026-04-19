package com.example.noteslist.data.mappers

import com.example.noteslist.data.models.NoteDbo
import com.example.noteslist.domain.models.Note

fun NoteDbo.toDomainModel(): Note {
    return Note(
        id = id,
        title = title,
        text = text,
        timestamp = timestamp,
        isImportant = isImportant,
        isRead = isRead
    )
}

fun Note.toEntity(): NoteDbo {
    return NoteDbo(
        id = id,
        title = title,
        text = text,
        timestamp = timestamp,
        isImportant = isImportant,
        isRead = isRead
    )
}
