package com.example.datastorage

import androidx.room.Entity
import androidx.room.PrimaryKey

// data/Note.kt
@Entity(tableName = "notes")
data class `Note`(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val contactId: String, // ID контакта из Content Provider
    val contactName: String,
    val text: String
)