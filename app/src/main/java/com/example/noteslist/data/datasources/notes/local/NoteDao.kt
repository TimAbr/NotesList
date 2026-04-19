package com.example.noteslist.data.datasources.notes.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.noteslist.data.models.NoteDbo
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes")
    fun getAllNotesFlow(): Flow<List<NoteDbo>>

    @Query("SELECT * FROM notes")
    suspend fun getAllNotes(): List<NoteDbo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteDbo): Long

    @Update
    suspend fun updateNote(note: NoteDbo)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNoteById(id: Long)
}
