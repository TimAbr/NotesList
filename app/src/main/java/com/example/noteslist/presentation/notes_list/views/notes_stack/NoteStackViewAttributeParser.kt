package com.example.noteslist.presentation.notes_list.views.notes_stack

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import androidx.core.content.res.use
import com.example.noteslist.R

class NoteStackViewAttributeParser {

    fun parse(
        context: Context,
        attrs: AttributeSet?
    ): Pair<NoteStackViewConfig, Boolean> {
        var isExpanded = false

        val typedValue = TypedValue()
        context.theme.resolveAttribute(
            com.google.android.material.R.attr.colorOnSurface,
            typedValue,
            true
        )
        val defaultContrastColor = typedValue.data

        return context.obtainStyledAttributes(
            attrs,
            R.styleable.NoteStackView
        ).use { array ->
            isExpanded = array.getBoolean(
                R.styleable.NoteStackView_isExpanded,
                isExpanded
            )

            NoteStackViewConfig(
                stackSpacing = array.getDimensionPixelSize(
                    R.styleable.NoteStackView_stackSpacing,
                    SPACING_DEFAULT
                ),
                stackMaxVisible = array.getInt(
                    R.styleable.NoteStackView_stackMaxVisible,
                    MAX_VISIBLE_DEFAULT
                ),
                stackExpandedSpacing = array.getDimensionPixelSize(
                    R.styleable.NoteStackView_stackExpandedSpacing,
                    EXPANDED_GAP_DEFAULT
                ),
                collapseButtonColor = array.getColor(
                    R.styleable.NoteStackView_collapseButtonColor,
                    defaultContrastColor
                )
            )
        } to isExpanded
    }

    companion object {
        private const val SPACING_DEFAULT = 16
        private const val MAX_VISIBLE_DEFAULT = 3
        private const val EXPANDED_GAP_DEFAULT = 32
    }
}
