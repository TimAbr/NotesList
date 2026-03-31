package com.example.noteslist.presentation.notes_list.views.note

import android.view.View.MeasureSpec
import com.example.noteslist.domain.models.Note
import com.example.noteslist.presentation.notes_list.views.ViewPaddings
import kotlin.math.roundToInt

class NoteViewMeasurer(private val renderer: NoteViewRenderer) {

    fun measure(
        note: Note,
        config: NoteViewConfig,
        paddings: ViewPaddings,
        widthMeasureSpec: Int
    ): NoteMeasurementResult {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        val horizontalPadding = paddings.paddingLeft + paddings.paddingRight
        val textWidth = (widthSize - horizontalPadding).coerceAtLeast(0)

        val colors = if (note.isRead) config.readColors else config.unreadColors
        val bodyHeight = renderer.createBodyLayout(
            note.text,
            textWidth,
            config.bodyTextSize,
            colors.textColor
        )

        val headerAreaHeight = config.titleTextSize.roundToInt() + (paddings.paddingTop * 2)

        val internalGaps = paddings.paddingTop * 2 + paddings.paddingBottom

        val footerAreaHeight = config.dateTextSize.roundToInt() + paddings.paddingBottom

        val totalHeight = (headerAreaHeight + internalGaps + bodyHeight + footerAreaHeight)

        return NoteMeasurementResult(
            measuredWidth = widthSize,
            measuredHeight = totalHeight
        )
    }
}
