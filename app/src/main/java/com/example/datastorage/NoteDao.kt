package com.example.datastorage

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// data/NoteDao.kt
@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE contactId = :contactId")
    fun getNotesForContact(contactId: String): Flow<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note)

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)

    // Метод для задания: удалить заметки, если контакта больше нет в телефоне
    @Query("DELETE FROM notes WHERE contactId NOT IN (:activeIds)")
    suspend fun deleteNotesForDeletedContacts(activeIds: List<String>)
}
