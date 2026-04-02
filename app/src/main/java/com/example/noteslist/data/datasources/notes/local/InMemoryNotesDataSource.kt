package com.example.noteslist.data.datasources.notes.local

import com.example.noteslist.data.datasources.NotesDataSource
import com.example.noteslist.domain.models.Note
import java.time.Instant
import java.time.temporal.ChronoUnit

class InMemoryNotesDataSource : NotesDataSource {

    private val now = Instant.now()
    private val yesterday = now.minus(1, ChronoUnit.DAYS)
    private val twoDaysAgo = now.minus(2, ChronoUnit.DAYS)

    private val notes = mutableListOf(
        Note(
            id = 1,
            title = "Список покупок",
            text = "Молоко, сыр, хлеб, яблоки, курица, овощи для салата и что-нибудь к чаю",
            timestamp = now.minusSeconds(100),
            isImportant = true,
            isRead = false
        ),
        Note(
            id = 2,
            title = "ДЗ по Android",
            text = "Реализовать NoteView и кастомный ViewGroup для стека заметок. Не забыть про onLayout.",
            timestamp = now.minusSeconds(500),
            isImportant = false,
            isRead = true
        ),
        Note(
            id = 3,
            title = "Идея для пет-проекта",
            text = "Приложение для отслеживания полива домашних растений с уведомлениями",
            timestamp = now.minusSeconds(1000),
            isImportant = false,
            isRead = false
        ),
        Note(
            id = 11,
            title = "ДЗ по Android",
            text = "Реализовать NoteView и кастомный ViewGroup для стека заметок. Не забыть про onLayout.",
            timestamp = now.minusSeconds(500),
            isImportant = false,
            isRead = true
        ),
        Note(
            id = 12,
            title = "Идея для пет-проекта",
            text = "Приложение для отслеживания полива домашних растений с уведомлениями",
            timestamp = now.minusSeconds(1000),
            isImportant = false,
            isRead = false
        ),
        Note(
            id = 4,
            title = "Важная встреча",
            text = "Обсуждение архитектуры нового модуля с командой в 14:00. Подготовить вопросы по БД.",
            timestamp = yesterday.plusSeconds(3600),
            isImportant = true,
            isRead = true
        ),
        Note(
            id = 5,
            title = "Рецепт пиццы",
            text = "Мука 500г, вода 300мл, дрожжи, соль, оливковое масло. Тесто должно стоять 2 часа.",
            timestamp = yesterday.minusSeconds(1000),
            isImportant = false,
            isRead = false
        ),
        Note(
            id = 6,
            title = "Заметка 6",
            text = "Короткий текст заметки",
            timestamp = yesterday.minusSeconds(2000),
            isImportant = false,
            isRead = true
        ),
        Note(
            id = 7,
            title = "Тренировка",
            text = "Разминка 10 мин, бег 5 км, растяжка. Не забыть взять воду.",
            timestamp = yesterday.minusSeconds(3000),
            isImportant = false,
            isRead = false
        ),
        Note(
            id = 8,
            title = "Позвонить маме",
            text = "Спросить как дела и рассказать про успехи в обучении",
            timestamp = yesterday.minusSeconds(4000),
            isImportant = false,
            isRead = true
        ),
        Note(
            id = 9,
            title = "Книга на вечер",
            text = "Дочитать 'Чистый код' Роберта Мартина. Глава про именование переменных.",
            timestamp = twoDaysAgo,
            isImportant = true,
            isRead = false
        ),
        Note(
            id = 10,
            title = "Заметка из прошлого",
            text = "Этот текст должен быть скрыт в стеке, если их слишком много.",
            timestamp = twoDaysAgo.minusSeconds(5000),
            isImportant = false,
            isRead = true
        )
    )

    override fun getAllNotes(): List<Note> = notes.toList()

    override fun updateNote(note: Note) {
        val index = notes.indexOfFirst { it.id == note.id }
        if (index != -1) {
            notes[index] = note
        }
    }

    override fun addNote(note: Note) {
        notes.add(note)
    }

    override fun deleteNote(id: Long) {
        notes.removeIf { it.id == id }
    }
}
