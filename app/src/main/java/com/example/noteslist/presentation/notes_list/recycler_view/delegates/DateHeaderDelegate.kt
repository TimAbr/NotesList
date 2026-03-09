package com.example.noteslist.presentation.notes_list.recycler_view.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.R
import com.example.noteslist.presentation.notes_list.recycler_view.NoteListItem
import java.time.format.DateTimeFormatter

class DateHeaderDelegate : NoteListItemDelegate {
    override fun isForViewType(item: NoteListItem) = item is NoteListItem.DateHeader
    
    override fun onCreateViewHolder(parent: ViewGroup) = DateViewHolder(
        LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.item_date_header,
                parent,
                false
            )
    )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: NoteListItem) {
        (holder as DateViewHolder).bind(item as NoteListItem.DateHeader)
    }

    class DateViewHolder(
        containerView: android.view.View
    ) : RecyclerView.ViewHolder(containerView) {
        private val textView: TextView = containerView.findViewById(R.id.dateHeaderText)
        
        fun bind(item: NoteListItem.DateHeader) {
            textView.text = item.title
        }
    }
}
