package com.example.noteslist.presentation.notes_list.views.notes_stack

import android.view.View
import android.view.ViewGroup
import com.example.noteslist.presentation.notes_list.views.ViewPaddings
import com.example.noteslist.presentation.notes_list.views.note.NoteView

class NoteStackViewLayoutManager {

    fun layout(
        notes: List<NoteView>,
        config: NoteStackViewConfig,
        collapseButton: View,
        emptyView: View,
        isExpanded: Boolean,
        paddings: ViewPaddings
    ) {
        if (notes.isEmpty()) {
            layoutEmpty(
                paddings,
                emptyView
            )
            return
        }

        if (isExpanded && notes.size > 1) {
            layoutExpanded(
                notes,
                collapseButton,
                paddings,
                config
            )
        } else {
            layoutCollapsed(
                notes,
                config,
                paddings
            )
        }
    }

    private fun layoutEmpty(
        paddings: ViewPaddings,
        emptyView: View
    ) {

        val left = paddings.paddingLeft
        val top = paddings.paddingTop
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
        paddings: ViewPaddings
    ) {
        val actualVisible = minOf(notes.size, config.stackMaxVisible)

        for (i in notes.indices) {
            val note = notes[i]
            val reverseIndex = notes.size - 1 - i

            if (reverseIndex < actualVisible) {
                note.visibility = View.VISIBLE

                val visualIndex = (actualVisible - 1) - reverseIndex
                val offset = visualIndex * config.stackSpacing

                val left = paddings.paddingLeft + offset
                val top = paddings.paddingTop + offset

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
        paddings: ViewPaddings,
        config: NoteStackViewConfig
    ) {
        var currentTop = paddings.paddingTop

        notes.forEach { note ->
            note.visibility = View.VISIBLE
            note.translationZ = 0f
            note.layout(
                paddings.paddingLeft,
                currentTop,
                paddings.paddingLeft + note.measuredWidth,
                currentTop + note.measuredHeight
            )
            currentTop += note.measuredHeight + config.stackExpandedSpacing
        }


        collapseButton.visibility = View.VISIBLE
        collapseButton.layout(
            paddings.paddingLeft,
            currentTop,
            paddings.paddingLeft + collapseButton.measuredWidth,
            currentTop + collapseButton.measuredHeight
        )
    }
}

