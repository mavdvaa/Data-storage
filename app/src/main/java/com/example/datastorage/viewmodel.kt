package com.example.datastorage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ContactViewModel(application: Application) : AndroidViewModel(application) {
    // Используйте lazy инициализацию, чтобы избежать проблем при старте
    private val db by lazy { AppDatabase.getDatabase(application) }
    private val contactRepo by lazy { ContactRepository(application) }

    private val _contacts = MutableLiveData<List<Contact>>()
    val contacts: LiveData<List<Contact>> = _contacts

    fun loadContacts() {
        android.util.Log.d("MyLog", "Метод loadContacts в ViewModel запущен")
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val list = contactRepo.getContacts()
                android.util.Log.d("MyLog", "Контактов считано: ${list.size}")
                _contacts.postValue(list)
            } catch (e: Exception) {
                android.util.Log.e("MyLog", "Ошибка загрузки: ${e.message}")
            }
        }
    }

    fun getNotes(contactId: String): Flow<List<Note>> {
        return db.noteDao().getNotesForContact(contactId)
    }

    fun addNote(note: Note) = viewModelScope.launch {
        db.noteDao().insert(note)
    }

    fun updateNote(note: Note) = viewModelScope.launch {
        db.noteDao().update(note)
    }

    fun deleteNote(note: Note) = viewModelScope.launch {
        db.noteDao().delete(note)
    }
}