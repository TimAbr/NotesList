package com.example.noteslist.presentation.notes_list.recycler_view.delegates

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.presentation.notes_list.recycler_view.NoteListItem

interface NoteListItemDelegate {
    fun isForViewType(item: NoteListItem): Boolean
    fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
    fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: NoteListItem)

    fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: NoteListItem, payloads: List<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, item)
        }
    }
}
