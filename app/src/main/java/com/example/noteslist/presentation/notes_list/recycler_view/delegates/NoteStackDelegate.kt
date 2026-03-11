package com.example.noteslist.presentation.notes_list.recycler_view.delegates

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.presentation.notes_list.recycler_view.NoteListItem
import com.example.noteslist.presentation.notes_list.views.notes_stack.NoteStackView

class NoteStackDelegate : NoteListItemDelegate {
    override fun isForViewType(item: NoteListItem) = item is NoteListItem.NoteStack

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val stackView = NoteStackView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        return StackViewHolder(stackView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: NoteListItem) {
        (holder as StackViewHolder).bind(item as NoteListItem.NoteStack)
    }

    class StackViewHolder(
        private val stackView: NoteStackView
    ) : RecyclerView.ViewHolder(stackView) {
        fun bind(item: NoteListItem.NoteStack) {
            stackView.notes = item.notes
        }
    }
}
