package com.example.noteslist.presentation.notes_list.views.notes_stack

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.view.children
import com.example.noteslist.R
import com.example.noteslist.domain.models.Note
import com.example.noteslist.presentation.notes_list.views.ViewPaddings
import com.example.noteslist.presentation.notes_list.views.note.NoteView
import com.example.noteslist.presentation.notes_list.views.notes_stack.animation.NoteStackAnimator

class NoteStackView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private val emptyView: View = createEmptyView()
    private val collapseButton: View

    private val attributeParser = NoteStackViewAttributeParser()
    private val measurer = NoteStackViewMeasurer()
    private val layoutManager = NoteStackViewLayoutManager()

    private val config: NoteStackViewConfig
    private val paddings = ViewPaddings(
        paddingLeft = paddingLeft,
        paddingTop = paddingTop,
        paddingBottom = paddingBottom,
        paddingRight = paddingRight
    )

    private val animator: NoteStackAnimator
    private var _isExpanded = false
    var isExpanded
        get() = _isExpanded
        set(value) {
            if (_isExpanded != value) {
                _isExpanded = value
                ensureSortedNotes()
                updateInternalViews()
                requestLayout()
                invalidate()
            }
        }

    private var _notes: List<Note> = emptyList()
    var notes: List<Note>
        get() =_notes
        set(value) {
            _notes = value

            val noteViews = ensureSortedNotes()
            val targetCount = notes.size
            val currentNoteCount = noteViews.size

            if (currentNoteCount > targetCount) {
                for (i in currentNoteCount - 1 downTo targetCount) {
                    removeView(noteViews[i])
                }
            }
            val styledContext = ContextThemeWrapper(context, R.style.NoteStyle)

            value.forEachIndexed { index, note ->
                val noteView = if (index < noteViews.size) {
                    noteViews[index]
                } else {
                    NoteView(styledContext).apply {
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
            
            updateInternalViews()
            requestLayout()
            invalidate()
        }

    private var sortedNotes: List<NoteView> = emptyList()
    private var areChildrenChanged = true

    init {

        val (configAttr, isExpandedAttr) = attributeParser.parse(context, attrs)
        _isExpanded = isExpandedAttr
        this.config = configAttr
        
        clipChildren = false
        clipToPadding = false

        collapseButton = createCollapseButton(config.collapseButtonColor)

        addView(emptyView)
        addView(collapseButton)

        animator = NoteStackAnimator(this, measurer, layoutManager, config, paddings)

        ensureSortedNotes()
        updateInternalViews()
    }

    fun expand() {
        if (_isExpanded) return
        val noteViews = ensureSortedNotes()
        if (noteViews.size <= 1) return

        animator.expand(noteViews, emptyView, collapseButton)
    }

    fun collapse() {
        if (!_isExpanded) return
        val noteViews = ensureSortedNotes()
        if (noteViews.isEmpty()) return

        animator.collapse(noteViews, emptyView, collapseButton)
    }

    private fun createEmptyView() = LayoutInflater
        .from(context)
        .inflate(R.layout.view_note_stack_empty, this, false)
        .apply {
            visibility = GONE
        }

    fun setCollapseButtonOnClickListener(
        onCollapse:() -> Unit
    ){
        collapseButton.setOnClickListener {
            onCollapse()
        }
    }

    private fun createCollapseButton(@ColorInt buttonColor: Int) = TextView(context).apply {
        val label = context.getString(R.string.note_stack_collapse_label)
        text = String.format(COLLAPSE_TEMPLATE, label)
        setTextColor(buttonColor)
        setPadding(
            PADDING_HORIZONTAL,
            PADDING_VERTICAL,
            PADDING_HORIZONTAL,
            PADDING_VERTICAL
        )
        visibility = if (_isExpanded) VISIBLE else GONE
    }

    override fun onViewAdded(child: View?) {
        super.onViewAdded(child)
        areChildrenChanged = true
    }

    override fun onViewRemoved(child: View?) {
        super.onViewRemoved(child)
        areChildrenChanged = true
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return (!_isExpanded && ensureSortedNotes().size != 1) || super.onInterceptTouchEvent(ev)
    }

    override fun addView(child: View?, index: Int, params: LayoutParams?) {
        if (child != null && child !is NoteView && child !is TextView) {
            throw IllegalArgumentException(
                "NoteStackView can only contain NoteView or TextView children"
            )
        }
        super.addView(child, index, params)
    }

    private fun ensureSortedNotes(): List<NoteView> {
        if (areChildrenChanged) {
            sortedNotes = children.filterIsInstance<NoteView>()
                .toList()
                .sortedBy { it.data.timestamp }
            _notes = sortedNotes.map{it.data}
            areChildrenChanged = false
        }
        return sortedNotes
    }

    fun setNoteViews(noteViews: List<NoteView>) {
        sortedNotes.forEach {removeView(it) }
        noteViews.forEach { addView(it) }
        
        areChildrenChanged = true
        requestLayout()
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val noteViews = ensureSortedNotes()

        val result = measurer.measure(
            notes = noteViews,
            emptyView = emptyView,
            collapseButton = collapseButton,
            config = config,
            isExpanded = isExpanded,
            widthMeasureSpec = widthMeasureSpec,
            heightMeasureSpec = heightMeasureSpec,
            paddings = paddings
        )

        setMeasuredDimension(
            resolveSize(result.width, widthMeasureSpec),
            resolveSize(result.height, heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val noteViews = ensureSortedNotes()
        layoutManager.layout(
            noteViews,
            config,
            collapseButton,
            emptyView,
            isExpanded,
            paddings
        )
    }

    private fun updateInternalViews() {
        emptyView.visibility = if (notes.isEmpty())
            VISIBLE
        else
            GONE

        if (_isExpanded && notes.size > 1) {
            collapseButton.visibility = VISIBLE
        } else if (notes.size <= 1) {
            collapseButton.visibility = GONE
        }
    }

    companion object {
        private const val PADDING_HORIZONTAL = 20
        private const val PADDING_VERTICAL = 20
        private const val COLLAPSE_TEMPLATE = "<< %s"
    }
}
