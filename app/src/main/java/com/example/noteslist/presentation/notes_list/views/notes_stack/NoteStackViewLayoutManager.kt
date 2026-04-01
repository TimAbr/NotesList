package com.example.noteslist.presentation.notes_list.views.notes_stack

import android.view.View
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

    fun calculateCollapsedTop(
        reverseIndex: Int,
        actualVisible: Int,
        spacing: Int,
        paddingTop: Int
    ): Int {
        return if (reverseIndex < actualVisible) {
            val visualIndex = (actualVisible - 1) - reverseIndex
            paddingTop + (visualIndex * spacing)
        } else {
            paddingTop
        }
    }

    fun calculateCollapsedLeft(
        reverseIndex: Int,
        actualVisible: Int,
        spacing: Int,
        paddingLeft: Int
    ): Int {
        return if (reverseIndex < actualVisible) {
            val visualIndex = (actualVisible - 1) - reverseIndex
            paddingLeft + (visualIndex * spacing)
        } else {
            paddingLeft
        }
    }

    fun calculateNoteWidth(
        containerWidth: Int,
        paddings: ViewPaddings,
        isExpanded: Boolean,
        config: NoteStackViewConfig,
        noteCount: Int
    ): Int {
        val horizontalPadding = paddings.paddingLeft + paddings.paddingRight
        val availableWidth = (containerWidth - horizontalPadding).coerceAtLeast(0)

        return if (isExpanded || noteCount <= 1) {
            availableWidth
        } else {
            val visibleCount = minOf(noteCount, config.stackMaxVisible)
            val maxOffset = (visibleCount - 1) * config.stackSpacing
            (availableWidth - maxOffset).coerceAtLeast(0)
        }
    }

    fun calculateCollapsedScale(
        stackWidth: Int,
        paddings: ViewPaddings,
        config: NoteStackViewConfig,
        noteCount: Int
    ): Float {
        val expandedWidth = calculateNoteWidth(
            stackWidth,
            paddings,
            true,
            config,
            noteCount
        ).toFloat()
        val collapsedWidth = calculateNoteWidth(
            stackWidth,
            paddings,
            false,
            config,
            noteCount
        ).toFloat()
        return if (expandedWidth > 0) collapsedWidth / expandedWidth else 1.0f
    }

    private fun layoutEmpty(paddings: ViewPaddings, emptyView: View) {
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
        val n = notes.size
        val actualVisible = minOf(n, config.stackMaxVisible)

        for (i in notes.indices) {
            val note = notes[i]
            val reverseIndex = n - 1 - i

            if (reverseIndex < actualVisible) {
                note.visibility = View.VISIBLE
                val left = calculateCollapsedLeft(
                    reverseIndex,
                    actualVisible,
                    config.stackSpacing,
                    paddings.paddingLeft
                )
                val top = calculateCollapsedTop(
                    reverseIndex,
                    actualVisible,
                    config.stackSpacing,
                    paddings.paddingTop
                )

                note.layout(
                    left,
                    top,
                    left + note.measuredWidth,
                    top + note.measuredHeight
                )
                note.translationZ = (n - reverseIndex).toFloat()
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
