package com.example.noteslist.data.datasources.notes.local

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.noteslist.data.models.NoteDbo
import javax.inject.Provider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class AppDatabaseDemoCallback @Inject constructor(
    private val databaseProvider: Provider<AppDatabase>
): RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        CoroutineScope(Dispatchers.IO).launch {
            populateDatabase(databaseProvider.get())
        }
    }

    private suspend fun populateDatabase(database: AppDatabase) {
        val dao = database.noteDao()
        val now = Instant.now()
        val yesterday = now.minus(1, ChronoUnit.DAYS)

        dao.insertNote(
            NoteDbo(
                title = "Важная заметка",
                text = "Эта заметка помечена как важная. Внутри своей даты такие записи всегда отображаются отдельно и не объединяются в стек с другими.",
                timestamp = now,
                isImportant = true,
                isRead = false
            )
        )

        dao.insertNote(
            NoteDbo(
                title = "Первая заметка в стопке",
                text = "Это обычная заметка. Все неважные записи за одну дату группируются в стек. В свернутом состоянии виден заголовок только этой заметки.",
                timestamp = now.minusSeconds(10),
                isImportant = false,
                isRead = true
            )
        )

        dao.insertNote(
            NoteDbo(
                title = "Заметка внутри стека",
                text = "Данная запись находится внутри сегодняшнего стека. Она скрыта под первой заметкой и становится доступной только после развертывания стопки.",
                timestamp = now.minusSeconds(60),
                isImportant = false,
                isRead = true
            )
        )

        dao.insertNote(
            NoteDbo(
                title = "Важная вчерашняя заметка",
                text = "Пометка «важная» выводит запись из вчерашнего стека. Она располагается отдельно внутри своей даты, сохраняя акцент на значимости.",
                timestamp = yesterday.plusSeconds(100),
                isImportant = true,
                isRead = true
            )
        )

        dao.insertNote(
            NoteDbo(
                title = "Одиночная обычная заметка",
                text = "Если за конкретную дату создана только одна обычная заметка, она отображается как стандартная одиночная запись. Стек сформируется только при появлении второй обычной заметки в этот же день.",
                timestamp = yesterday,
                isImportant = false,
                isRead = true
            )
        )
    }
}