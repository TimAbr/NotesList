package com.example.noteslist.presentation.notes_list.views.note

import android.content.Context
import android.graphics.Canvas
import android.graphics.Outline
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import com.example.noteslist.domain.models.Note
import com.example.noteslist.presentation.common.NoteDateFormatter
import com.example.noteslist.presentation.notes_list.views.ViewPaddings
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NoteView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    @Inject
    lateinit var dateFormatter: NoteDateFormatter

    private var config: NoteViewConfig
    private val paddings = ViewPaddings(
        paddingLeft = paddingLeft,
        paddingTop = paddingTop,
        paddingRight = paddingRight,
        paddingBottom = paddingBottom
    )

    private val renderer = NoteViewRenderer(context)
    private val measurer = NoteViewMeasurer(renderer)

    private var formattedDate: String = ""

    private val attributeParser =
        NoteViewAttributeParser(dateFormatter)

    private lateinit var _data: Note
    var data: Note
        get() = _data
        set(value) {
            _data = value
            formattedDate = dateFormatter.format(value.timestamp)
            requestLayout()
            invalidate()
        }

    init {
        val (parsedConfig, initialNote) =
            attributeParser.parse(context, attrs, defStyleAttr)
        config = parsedConfig
        data = initialNote

        elevation = config.noteElevation
        setupOutline()
    }

    private fun setupOutline() {
        clipToOutline = true
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(
                    0,
                    0,
                    view.width,
                    view.height,
                    config.cornerRadius
                )
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val result = measurer.measure(
            note = data,
            config = config,
            paddings = paddings,
            widthMeasureSpec = widthMeasureSpec
        )

        setMeasuredDimension(
            result.measuredWidth,
            resolveSize(result.measuredHeight, heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        renderer.draw(
            canvas = canvas,
            note = data,
            config = config,
            formattedDate = formattedDate,
            width = width.toFloat(),
            height = height.toFloat(),
            paddings = paddings
        )
    }
}
