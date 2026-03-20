package com.example.datastorage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(
    private val onClick: (Contact) -> Unit
) : ListAdapter<Contact, ContactAdapter.ContactViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = getItem(position)
        android.util.Log.d("MyLog", "Отрисовка контакта: ${contact.name}")
        holder.bind(contact)
    }

    class ContactViewHolder(
        itemView: View,
        private val onClick: (Contact) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val textName: TextView = itemView.findViewById(R.id.textName)
        private val textPhone: TextView = itemView.findViewById(R.id.textPhone)
        private var currentContact: Contact? = null

        init {
            itemView.setOnClickListener {
                currentContact?.let { onClick(it) }
            }
        }

        fun bind(contact: Contact) {
            currentContact = contact
            textName.text = contact.name
            textPhone.text = contact.phone
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem == newItem
        }
    }
}
