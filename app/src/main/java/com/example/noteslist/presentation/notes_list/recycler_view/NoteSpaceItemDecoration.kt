package com.example.noteslist.presentation.notes_list.recycler_view

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class NoteSpaceItemDecoration(private val spacingPx: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position == RecyclerView.NO_POSITION) return

        val adapter = parent.adapter as? NotesAdapter
        val item = adapter?.currentList?.getOrNull(position)

        outRect.bottom = spacingPx

        if (item is NoteListItem.DateHeader && position > 0) {
            outRect.top = spacingPx * 2
        }
    }
}
