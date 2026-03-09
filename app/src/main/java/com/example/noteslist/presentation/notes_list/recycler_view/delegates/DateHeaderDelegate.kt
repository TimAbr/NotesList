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
        LayoutInflater.from(parent.context).inflate(R.layout.item_date_header, parent, false) as TextView
    )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: NoteListItem) {
        (holder as DateViewHolder).bind(item as NoteListItem.DateHeader)
    }

    class DateViewHolder(private val textView: TextView) : RecyclerView.ViewHolder(textView) {
        private val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        fun bind(item: NoteListItem.DateHeader) {
            textView.text = String.format("[%s]", item.date.format(formatter))
        }
    }
}
