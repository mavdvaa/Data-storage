package com.example.datastorage

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.datastorage.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: ContactViewModel by viewModels()
    private val CONTACTS_PERMISSION_REQUEST = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (checkSelfPermission(android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.READ_CONTACTS), CONTACTS_PERMISSION_REQUEST)
        } else {
            setupUI()
        }
    }

    private fun setupUI() {
        val adapter = ContactAdapter { contact ->
            showContactOptions(contact)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        viewModel.contacts.observe(this) { list ->
            adapter.submitList(list.toList())
        }

        viewModel.loadContacts()
    }

    // Выбор действия: Добавить или Посмотреть заметки
    private fun showContactOptions(contact: Contact) {
        val options = arrayOf("Добавить заметку", "Посмотреть заметки этого контакта")
        AlertDialog.Builder(this)
            .setTitle(contact.name)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showAddNoteDialog(contact)
                    1 -> showNotesListDialog(contact)
                }
            }.show()
    }

    // 1. СОЗДАНИЕ заметки
    private fun showAddNoteDialog(contact: Contact) {
        val editText = EditText(this)
        AlertDialog.Builder(this)
            .setTitle("Новая заметка")
            .setView(editText)
            .setPositiveButton("Сохранить") { _, _ ->
                val text = editText.text.toString()
                if (text.isNotBlank()) {
                    viewModel.addNote(Note(contactId = contact.id, contactName = contact.name, text = text))
                }
            }.setNegativeButton("Отмена", null).show()
    }

    // 2. ПРОСМОТР списка заметок (из Room)
    private fun showNotesListDialog(contact: Contact) {
        // Подписываемся на Flow из Room и превращаем в LiveData на время работы диалога
        viewModel.getNotes(contact.id).asLiveData().observe(this) { notes ->
            if (notes.isEmpty()) {
                Toast.makeText(this, "Заметок пока нет", Toast.LENGTH_SHORT).show()
                return@observe
            }

            val noteTexts = notes.map { it.text }.toTypedArray()

            AlertDialog.Builder(this)
                .setTitle("Заметки: ${contact.name}")
                .setItems(noteTexts) { _, which ->
                    showEditDeleteDialog(notes[which])
                }
                .setPositiveButton("Закрыть", null)
                .show()
        }
    }

    // 3. РЕДАКТИРОВАНИЕ и УДАЛЕНИЕ
    private fun showEditDeleteDialog(note: Note) {
        val options = arrayOf("Редактировать", "Удалить")
        AlertDialog.Builder(this)
            .setTitle("Выберите действие")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> { // Редактирование
                        val editText = EditText(this)
                        editText.setText(note.text)
                        AlertDialog.Builder(this)
                            .setTitle("Редактирование")
                            .setView(editText)
                            .setPositiveButton("ОК") { _, _ ->
                                viewModel.updateNote(note.copy(text = editText.text.toString()))
                            }.show()
                    }
                    1 -> viewModel.deleteNote(note) // Удаление
                }
            }.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CONTACTS_PERMISSION_REQUEST && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setupUI()
        }
    }
}
