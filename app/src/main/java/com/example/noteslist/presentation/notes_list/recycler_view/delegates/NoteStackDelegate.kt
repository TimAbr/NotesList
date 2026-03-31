package com.example.noteslist.presentation.notes_list.recycler_view.delegates

import android.view.ViewGroup
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.domain.models.Note
import com.example.noteslist.presentation.notes_list.recycler_view.NoteListItem
import com.example.noteslist.presentation.notes_list.recycler_view.mapper.NoteStackKey
import com.example.noteslist.presentation.notes_list.views.note.NoteView
import com.example.noteslist.presentation.notes_list.views.notes_stack.NoteStackView

class NoteStackDelegate(
    private val onNoteClick: (Note)-> Unit,
    private val onStackClick: (NoteStackKey)->Unit
) : NoteListItemDelegate {
    override fun isForViewType(item: NoteListItem) = item is NoteListItem.NoteStack

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val stackView = NoteStackView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        return StackViewHolder(stackView, onNoteClick, onStackClick)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: NoteListItem) {
        (holder as StackViewHolder).bind(item as NoteListItem.NoteStack)
    }

    class StackViewHolder(
        private val stackView: NoteStackView,
        private val onNoteClick: (Note)-> Unit,
        private val onStackClick: (NoteStackKey)->Unit
    ) : RecyclerView.ViewHolder(stackView) {
        fun bind(item: NoteListItem.NoteStack) {
            stackView.notes = item.notes
            stackView.children.forEach { child ->
                if (child is NoteView) {
                    child.setOnClickListener {
                        onNoteClick(child.data)
                    }
                }
            }
            stackView.isExpanded = item.isExpanded
            stackView.setOnClickListener {
                onStackClick(item.key)
            }
            stackView.setCollapseButtonOnClickListener {
                onStackClick(item.key)
            }
        }
    }
}
