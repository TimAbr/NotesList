package com.example.noteslist.presentation.notes_list.recycler_view.delegates

import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.R
import com.example.noteslist.domain.models.Note
import com.example.noteslist.presentation.notes_list.recycler_view.NoteListItem
import com.example.noteslist.presentation.notes_list.views.note.NoteView

class NoteDelegate(
    private val onNoteClick: (Long)-> Unit
) : NoteListItemDelegate {
    override fun isForViewType(item: NoteListItem) = item is NoteListItem.SingleNote

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val styledContext = ContextThemeWrapper(
            parent.context,
            R.style.NoteStyle
        )
        val noteView = NoteView(styledContext).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        noteView.setOnClickListener {
            onNoteClick(noteView.data.id)
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
