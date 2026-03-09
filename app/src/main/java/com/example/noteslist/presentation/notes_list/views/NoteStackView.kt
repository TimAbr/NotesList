package com.example.noteslist.presentation.notes_list.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import com.example.noteslist.R
import com.example.noteslist.domain.models.Note

class NoteStackView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private var stackSpacing = SPACING_DEFAULT
    private var stackMaxVisible = MAX_VISIBLE_DEFAULT
    private var stackExpandedSpacing = EXPANDED_GAP_DEFAULT
    var isExpanded = false
        set(value) {
            if (field != value) {
                field = value
                requestLayout()
            }
        }

    private var sortedNotes: List<NoteView> = emptyList()
    private var areChildrenChanged = true

    private val collapseButton: TextView = TextView(context).apply {
        val label = context.getString(R.string.note_stack_collapse_label)
        text = String.format(COLLAPSE_TEMPLATE, label)
        setPadding(
            PADDING_HORIZONTAL,
            PADDING_VERTICAL,
            PADDING_HORIZONTAL,
            PADDING_VERTICAL
        )
        visibility = GONE
        setOnClickListener {
            isExpanded = false
        }
    }

    private val emptyView: TextView = TextView(context).apply {
        text = context.getString(R.string.note_stack_empty_placeholder)
        gravity = android.view.Gravity.CENTER
        visibility = GONE
    }

    init {
        context.obtainStyledAttributes(attrs, R.styleable.NoteStackView).apply {
            stackSpacing = getDimensionPixelSize(
                R.styleable.NoteStackView_stackSpacing,
                SPACING_DEFAULT
            )
            stackMaxVisible = getInt(
                R.styleable.NoteStackView_stackMaxVisible,
                MAX_VISIBLE_DEFAULT
            )
            isExpanded = getBoolean(
                R.styleable.NoteStackView_isExpanded,
                false
            )
            stackExpandedSpacing = getDimensionPixelSize(
                R.styleable.NoteStackView_stackExpandedSpacing,
                EXPANDED_GAP_DEFAULT
            )
            recycle()
        }
        addView(collapseButton)
        addView(emptyView)

        setOnClickListener {
            val visibleNotes = ensureSortedNotes().size
            if (!isExpanded && visibleNotes > 1) {
                isExpanded = true
            }
        }
    }

    override fun onViewAdded(child: View?) {
        super.onViewAdded(child)
        areChildrenChanged = true
    }

    override fun onViewRemoved(child: View?) {
        super.onViewRemoved(child)
        areChildrenChanged = true
    }

    override fun onInterceptTouchEvent(ev: android.view.MotionEvent?): Boolean {
        return (!isExpanded && ensureSortedNotes().size!=1) || super.onInterceptTouchEvent(ev)
    }

    override fun addView(child: View?, index: Int, params: LayoutParams?) {
        if (child != collapseButton && child != emptyView && child !is NoteView) {
            throw IllegalArgumentException("NoteStackView can only contain NoteView children")
        }
        super.addView(child, index, params)
    }

    private fun ensureSortedNotes(): List<NoteView> {
        if (areChildrenChanged) {
            sortedNotes = children.filterIsInstance<NoteView>()
                .toList()
                .sortedBy { it.data.timestamp }
            areChildrenChanged = false
        }
        return sortedNotes
    }

    fun setNoteViews(noteViews: List<NoteView>) {
        val button = collapseButton
        val empty = emptyView
        removeAllViews()
        addView(button)
        addView(empty)

        noteViews.forEach { addView(it) }
        
        areChildrenChanged = true
        requestLayout()
        invalidate()
    }

    fun setNotes(notes: List<Note>) {
        if (childCount < 2 || getChildAt(0) != collapseButton || getChildAt(1) != emptyView) {
            removeView(collapseButton)
            removeView(emptyView)
            addView(collapseButton, 0)
            addView(emptyView, 1)
        }

        val targetCount = notes.size
        val currentNoteCount = (childCount - 2).coerceAtLeast(0)

        if (currentNoteCount > targetCount) {
            removeViews(targetCount + 2, currentNoteCount - targetCount)
        }

        notes.forEachIndexed { index, note ->
            val childIndex = index + 2
            val noteView = if (childIndex < childCount) {
                getChildAt(childIndex) as NoteView
            } else {
                NoteView(context).apply {
                    layoutParams = LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT
                    )
                    this@NoteStackView.addView(this)
                }
            }
            noteView.data = note
        }

        areChildrenChanged = true
        requestLayout()
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val noteViews = ensureSortedNotes()
        if (noteViews.isEmpty()) {
            emptyView.visibility = View.VISIBLE
            measureChild(
                emptyView,
                widthMeasureSpec,
                heightMeasureSpec
            )
            val h = emptyView.measuredHeight + paddingTop + paddingBottom
            val w = emptyView.measuredWidth + paddingLeft + paddingRight
            setMeasuredDimension(
                resolveSize(w, widthMeasureSpec),
                resolveSize(h, heightMeasureSpec)
            )
            return
        }

        emptyView.visibility = View.GONE
        val parentWidth = MeasureSpec.getSize(widthMeasureSpec)
        val availableWidth = (parentWidth - paddingLeft - paddingRight)
            .coerceAtLeast(0)

        if (isExpanded && noteViews.size > 1) {
            measureExpanded(noteViews, widthMeasureSpec, heightMeasureSpec, availableWidth)
        } else {
            measureCollapsed(noteViews, widthMeasureSpec, heightMeasureSpec, availableWidth)
        }
    }

    private fun measureExpanded(
        notes: List<NoteView>,
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
        availableWidth: Int
    ) {
        val childWidthSpec = MeasureSpec.makeMeasureSpec(
            availableWidth,
            MeasureSpec.AT_MOST
        )
        val childHeightSpec = getChildMeasureSpec(
            heightMeasureSpec,
            paddingTop + paddingBottom,
            LayoutParams.WRAP_CONTENT
        )

        notes.forEach {
            it.measure(childWidthSpec, childHeightSpec)
        }
        measureChild(
            collapseButton,
            widthMeasureSpec,
            heightMeasureSpec
        )

        val maxChildWidth = if (notes.isEmpty()) 0 else notes.maxOf { it.measuredWidth }
        val desiredWidth = maxChildWidth + paddingLeft + paddingRight
        val desiredHeight = notes.sumOf { it.measuredHeight + stackExpandedSpacing } +
                collapseButton.measuredHeight + paddingTop + paddingBottom

        setMeasuredDimension(
            resolveSize(desiredWidth, widthMeasureSpec),
            resolveSize(desiredHeight, heightMeasureSpec)
        )
    }

    private fun measureCollapsed(
        notes: List<NoteView>,
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
        availableWidth: Int
    ) {
        val visibleCount = minOf(notes.size, stackMaxVisible)
        val maxOffset = (visibleCount - 1) * stackSpacing
        val availableWidthInStack = (availableWidth - maxOffset).coerceAtLeast(0)

        val childWidthSpec = MeasureSpec.makeMeasureSpec(
            availableWidthInStack,
            MeasureSpec.AT_MOST
        )
        val childHeightSpec = getChildMeasureSpec(
            heightMeasureSpec,
            paddingTop + paddingBottom,
            LayoutParams.WRAP_CONTENT
        )

        notes.forEach {
            it.measure(childWidthSpec, childHeightSpec)
        }
        measureChild(
            collapseButton,
            widthMeasureSpec,
            heightMeasureSpec
        )

        var maxWWithOffset = 0
        var maxHeight = 0
        val startIndex = notes.size - visibleCount
        for (i in startIndex..<notes.size) {
            val note = notes[i]
            val visualIndex = i - startIndex
            val offset = visualIndex * stackSpacing
            maxWWithOffset = maxOf(maxWWithOffset, note.measuredWidth + offset)
            maxHeight = maxOf(maxHeight, note.measuredHeight + offset)
        }

        val desiredWidth = maxWWithOffset + paddingLeft + paddingRight
        val desiredHeight = maxHeight + paddingTop + paddingBottom

        setMeasuredDimension(
            resolveSize(desiredWidth, widthMeasureSpec),
            resolveSize(desiredHeight, heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val noteViews = ensureSortedNotes()
        if (noteViews.isEmpty()) {
            val left = paddingLeft
            val top = paddingTop
            emptyView.layout(
                left,
                top,
                left + emptyView.measuredWidth,
                top + emptyView.measuredHeight
            )
        }

        if (isExpanded && noteViews.size > 1) {
            layoutExpanded(noteViews)
        } else {
            layoutCollapsed(noteViews)
        }
    }

    private fun layoutCollapsed(notes: List<NoteView>) {
        collapseButton.visibility = View.GONE
        val actualVisible = minOf(notes.size, stackMaxVisible)

        for (i in notes.indices) {
            val note = notes[i]
            val reverseIndex = notes.size - 1 - i

            if (reverseIndex < actualVisible) {
                note.visibility = View.VISIBLE

                val visualIndex = (actualVisible - 1) - reverseIndex
                val offset = visualIndex * stackSpacing

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

    private fun layoutExpanded(notes: List<NoteView>) {
        var currentTop = paddingTop

        notes.reversed().forEach { note ->
            note.visibility = View.VISIBLE
            note.translationZ = 0f
            note.layout(
                paddingLeft,
                currentTop,
                paddingLeft + note.measuredWidth,
                currentTop + note.measuredHeight
            )
            currentTop += note.measuredHeight + stackExpandedSpacing
        }

        collapseButton.visibility = View.VISIBLE
        collapseButton.layout(
            paddingLeft,
            currentTop,
            paddingLeft + collapseButton.measuredWidth,
            currentTop + collapseButton.measuredHeight
        )
    }

    companion object {
        private const val PADDING_HORIZONTAL = 20
        private const val PADDING_VERTICAL = 20
        private const val EXPANDED_GAP_DEFAULT = 30
        private const val SPACING_DEFAULT = 20
        private const val MAX_VISIBLE_DEFAULT = 3
        private const val COLLAPSE_TEMPLATE = "<< %s"
    }
}