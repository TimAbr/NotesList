package com.example.noteslist.presentation.notes_list.views.notes_stack

import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.view.ViewGroup.getChildMeasureSpec
import com.example.noteslist.presentation.notes_list.views.ViewPaddings
import com.example.noteslist.presentation.notes_list.views.note.NoteView

class NoteStackViewMeasurer {

    data class MeasuredSize(val width: Int, val height: Int)

    fun measure(
        notes: List<NoteView>,
        emptyView: View,
        collapseButton: View,
        config: NoteStackViewConfig,
        isExpanded: Boolean,
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
        paddings: ViewPaddings
    ): MeasuredSize {
        val paddingLeft = paddings.paddingLeft
        val paddingRight = paddings.paddingRight
        val paddingTop = paddings.paddingTop
        val paddingBottom = paddings.paddingBottom

        if (notes.isEmpty()) {
            measureChildSafe(
                emptyView,
                widthMeasureSpec,
                heightMeasureSpec,
                paddingLeft + paddingRight,
                paddingTop + paddingBottom
            )

            return MeasuredSize(
                width = emptyView.measuredWidth + paddingLeft + paddingRight,
                height = emptyView.measuredHeight + paddingTop + paddingBottom
            )
        }

        val parentWidth = MeasureSpec.getSize(widthMeasureSpec)
        val availableWidth = (parentWidth - paddingLeft - paddingRight)
            .coerceAtLeast(0)

        return if (isExpanded && notes.size > 1) {
            measureExpanded(
                notes,
                collapseButton,
                config,
                widthMeasureSpec,
                heightMeasureSpec,
                availableWidth,
                paddings
            )
        } else {
            measureCollapsed(
                notes,
                config,
                heightMeasureSpec,
                availableWidth,
                paddings
            )
        }
    }

    private fun measureExpanded(
        notes: List<NoteView>,
        collapseButton: View,
        config: NoteStackViewConfig,
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
        availableWidth: Int,
        paddings: ViewPaddings
    ): MeasuredSize {
        val childWidthSpec = MeasureSpec.makeMeasureSpec(
            availableWidth,
            MeasureSpec.AT_MOST
        )
        val childHeightSpec = ViewGroup.getChildMeasureSpec(
            heightMeasureSpec,
            paddings.paddingTop + paddings.paddingBottom,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        var maxChildWidth = 0
        var totalHeight = 0

        for (i in notes.indices) {
            val note = notes[i]
            note.measure(childWidthSpec, childHeightSpec)
            maxChildWidth = maxOf(maxChildWidth, note.measuredWidth)
            totalHeight += note.measuredHeight

            if (i < notes.size - 1) {
                totalHeight += config.stackExpandedSpacing
            }
        }

        var buttonHeight = 0
        if (collapseButton.visibility != View.GONE) {
            measureChildSafe(
                collapseButton,
                widthMeasureSpec,
                heightMeasureSpec,
                paddings.paddingLeft + paddings.paddingRight,
                paddings.paddingTop + paddings.paddingBottom
            )
            buttonHeight = collapseButton.measuredHeight
            if (notes.isNotEmpty()) {
                totalHeight += config.stackExpandedSpacing
            }
        }

        val desiredWidth = maxChildWidth + paddings.paddingLeft + paddings.paddingRight
        val desiredHeight = totalHeight + buttonHeight +
                paddings.paddingTop + paddings.paddingBottom

        return MeasuredSize(desiredWidth, desiredHeight)
    }

    private fun measureCollapsed(
        notes: List<NoteView>,
        config: NoteStackViewConfig,
        heightMeasureSpec: Int,
        availableWidth: Int,
        paddings: ViewPaddings
    ): MeasuredSize {
        val visibleCount = minOf(notes.size, config.stackMaxVisible)
        val maxOffset = (visibleCount - 1) * config.stackSpacing
        val availableWidthInStack = (availableWidth - maxOffset)
            .coerceAtLeast(0)

        val childWidthSpec = MeasureSpec.makeMeasureSpec(
            availableWidthInStack,
            MeasureSpec.AT_MOST
        )
        val childHeightSpec = getChildMeasureSpec(
            heightMeasureSpec,
            paddings.paddingTop + paddings.paddingBottom,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        var maxWidthWithOffset = 0
        var maxHeightWithOffset = 0
        val startIndex = (notes.size - visibleCount).coerceAtLeast(0)

        for (i in startIndex..<notes.size) {
            notes[i].measure(childWidthSpec, childHeightSpec)
        }

        for (i in startIndex..<notes.size) {
            val note = notes[i]
            val visualIndex = i - startIndex
            val offset = visualIndex * config.stackSpacing

            maxWidthWithOffset = maxOf(maxWidthWithOffset, note.measuredWidth + offset)
            maxHeightWithOffset = maxOf(maxHeightWithOffset, note.measuredHeight + offset)
        }

        return MeasuredSize(
            width = maxWidthWithOffset + paddings.paddingLeft + paddings.paddingRight,
            height = maxHeightWithOffset + paddings.paddingTop + paddings.paddingBottom
        )
    }

    private fun measureChildSafe(
        child: View,
        parentWidthSpec: Int,
        parentHeightSpec: Int,
        widthPadding: Int,
        heightPadding: Int
    ) {
        val lp = child.layoutParams
        val childWidthSpec = getChildMeasureSpec(
            parentWidthSpec,
            widthPadding,
            lp.width
        )
        val childHeightSpec = getChildMeasureSpec(
            parentHeightSpec,
            heightPadding,
            lp.height
        )
        child.measure(
            childWidthSpec,
            childHeightSpec
        )
    }
}