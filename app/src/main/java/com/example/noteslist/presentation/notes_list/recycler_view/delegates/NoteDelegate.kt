package com.example.noteslist.presentation.notes_list.recycler_view.delegates

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.presentation.notes_list.recycler_view.NoteListItem
import com.example.noteslist.presentation.notes_list.views.note.NoteView

class NoteDelegate : NoteListItemDelegate {
    override fun isForViewType(item: NoteListItem) = item is NoteListItem.SingleNote

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val noteView = NoteView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        return NoteViewHolder(noteView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: NoteListItem) {
        (holder as NoteViewHolder).bind(item as NoteListItem.SingleNote)
    }

    class NoteViewHolder(
        private val noteView: NoteView
    ) : RecyclerView.ViewHolder(noteView) {
        fun bind(item: NoteListItem.SingleNote) {
            noteView.data = item.note
        }
    }
}
