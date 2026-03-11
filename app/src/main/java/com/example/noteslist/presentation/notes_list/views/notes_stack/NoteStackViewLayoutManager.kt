package com.example.noteslist.presentation.notes_list.views.notes_stack

import android.view.View
import android.view.ViewGroup
import com.example.noteslist.presentation.notes_list.views.note.NoteView

class NoteStackViewLayoutManager {

    fun layout(
        notes: List<NoteView>,
        config: NoteStackViewConfig,
        collapseButton: View,
        emptyView: View,
        isExpanded: Boolean,
        paddingLeft: Int,
        paddingTop: Int,
        viewGroup: ViewGroup
    ) {
        if (notes.isEmpty()) {
            layoutEmpty(viewGroup, paddingLeft, paddingTop, emptyView)
            return
        }

        if (isExpanded && notes.size > 1) {
            layoutExpanded(
                notes,
                collapseButton,
                paddingLeft,
                paddingTop,
                config
            )
        } else {
            layoutCollapsed(
                notes,
                config,
                paddingLeft,
                paddingTop,
                viewGroup
            )
        }
    }

    private fun layoutEmpty(
        viewGroup: ViewGroup,
        paddingLeft: Int,
        paddingTop: Int,
        emptyView: View
    ) {

        val left = paddingLeft
        val top = paddingTop
        emptyView.layout(
            left,
            top,
            left + emptyView.measuredWidth,
            top + emptyView.measuredHeight
        )

    }

    private fun layoutCollapsed(
        notes: List<NoteView>,
        config: NoteStackViewConfig,
        paddingLeft: Int,
        paddingTop: Int,
        viewGroup: ViewGroup
    ) {
        val actualVisible = minOf(notes.size, config.stackMaxVisible)

        for (i in notes.indices) {
            val note = notes[i]
            val reverseIndex = notes.size - 1 - i

            if (reverseIndex < actualVisible) {
                note.visibility = View.VISIBLE

                val visualIndex = (actualVisible - 1) - reverseIndex
                val offset = visualIndex * config.stackSpacing

                val left = paddingLeft + offset
                val top = paddingTop + offset

                note.layout(
                    left,
                    top,
                    left + note.measuredWidth,
                    top + note.measuredHeight
                )

                note.translationZ = (notes.size - reverseIndex).toFloat()
            } else {
                note.visibility = View.GONE
            }
        }
    }

    private fun layoutExpanded(
        notes: List<NoteView>,
        collapseButton: View,
        paddingLeft: Int,
        paddingTop: Int,
        config: NoteStackViewConfig
    ) {
        var currentTop = paddingTop

        notes.forEach { note ->
            note.visibility = View.VISIBLE
            note.translationZ = 0f
            note.layout(
                paddingLeft,
                currentTop,
                paddingLeft + note.measuredWidth,
                currentTop + note.measuredHeight
            )
            currentTop += note.measuredHeight + config.stackExpandedSpacing
        }


        collapseButton.visibility = View.VISIBLE
        collapseButton.layout(
            paddingLeft,
            currentTop,
            paddingLeft + collapseButton.measuredWidth,
            currentTop + collapseButton.measuredHeight
        )
    }
}

