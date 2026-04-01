package com.example.noteslist.presentation.notes_list.recycler_view.delegates

import android.view.ViewGroup
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.domain.models.Note
import com.example.noteslist.presentation.notes_list.recycler_view.NoteListItem
import com.example.noteslist.presentation.notes_list.recycler_view.NoteStackPayload
import com.example.noteslist.presentation.notes_list.recycler_view.mapper.NoteStackKey
import com.example.noteslist.presentation.notes_list.views.note.NoteView
import com.example.noteslist.presentation.notes_list.views.notes_stack.NoteStackView
import com.example.noteslist.presentation.notes_list.views.notes_stack.animation.StackAnimationType

class NoteStackDelegate(
    private val onNoteClick: (Long)-> Unit,
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
        val holder = StackViewHolder(stackView, onNoteClick)
        stackView.setOnClickListener {
            (stackView.tag as? NoteStackKey)?.let(onStackClick)
        }
        stackView.setCollapseButtonOnClickListener {
            (stackView.tag as? NoteStackKey)?.let(onStackClick)
        }
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: NoteListItem) {
        (holder as StackViewHolder).bind(
            item = item as NoteListItem.NoteStack,
            animation = null,
            notesChanged = true
        )
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        item: NoteListItem,
        payloads: List<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, item)
        } else {
            val payload = payloads.firstOrNull() as? NoteStackPayload
            (holder as StackViewHolder).bind(
                item = item as NoteListItem.NoteStack, 
                animation = payload?.animationType,
                notesChanged = payload?.notesChanged ?: true
            )
        }
    }

    class StackViewHolder(
        private val stackView: NoteStackView,
        private val onNoteClick: (Long)-> Unit
    ) : RecyclerView.ViewHolder(stackView) {

        fun bind(
            item: NoteListItem.NoteStack, 
            animation: StackAnimationType? = null,
            notesChanged: Boolean = true
        ) {
            stackView.tag = item.key
            
            if (notesChanged) {
                stackView.notes = item.notes
                stackView.children.forEach { child ->
                    if (child is NoteView) {
                        child.setOnClickListener {
                            onNoteClick(child.data.id)
                        }
                    }
                }
            }
            
            if (animation == StackAnimationType.EXPAND) {
                stackView.expand()
            } else if (animation == StackAnimationType.COLLAPSE) {
                stackView.collapse()
            } else {
                stackView.isExpanded = item.isExpanded
            }
        }
    }
}
