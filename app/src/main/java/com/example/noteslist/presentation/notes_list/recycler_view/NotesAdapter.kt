package com.example.noteslist.presentation.notes_list.recycler_view

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.presentation.notes_list.recycler_view.delegates.NoteListItemDelegate

class NotesAdapter(
    private val delegates: List<NoteListItemDelegate>
) : ListAdapter<NoteListItem, RecyclerView.ViewHolder>(NoteDiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        val index = delegates.indexOfFirst { it.isForViewType(item) }
        if (index == -1)
            throw IllegalArgumentException("No delegate found for item at position $position")
        return index
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return delegates[viewType].onCreateViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        delegates[getItemViewType(position)].onBindViewHolder(holder, item)
    }

    class NoteDiffCallback : DiffUtil.ItemCallback<NoteListItem>() {
        override fun areItemsTheSame(oldItem: NoteListItem, newItem: NoteListItem): Boolean {
            return when {
                oldItem is NoteListItem.DateHeader && newItem is NoteListItem.DateHeader -> 
                    oldItem.title == newItem.title
                oldItem is NoteListItem.SingleNote && newItem is NoteListItem.SingleNote -> 
                    oldItem.note.id == newItem.note.id
                oldItem is NoteListItem.NoteStack && newItem is NoteListItem.NoteStack -> 
                    oldItem.notes.map { it.id } == newItem.notes.map { it.id }
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: NoteListItem, newItem: NoteListItem): Boolean {
            return oldItem == newItem
        }
    }
}
