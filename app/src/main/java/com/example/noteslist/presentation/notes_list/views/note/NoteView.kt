package com.example.noteslist.presentation.notes_list.views.note

import android.content.Context
import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Paint
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import com.example.noteslist.domain.models.Note
import com.example.noteslist.presentation.notes_list.NoteDateFormatter
import com.example.noteslist.presentation.notes_list.NoteDateFormatterImpl

class NoteView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var config: NoteViewConfig
    private val renderer = NoteRenderer(context)

    private var formattedDate: String = ""
    private var bodyLayout: StaticLayout? = null

    private val dateFormatter: NoteDateFormatter =
        NoteDateFormatterImpl()
    private val attributeParser =
        NoteViewAttributeParser(dateFormatter)

    private lateinit var _data: Note
    var data: Note
        get() = _data
        set(value) {
            if (_data == value) return
            _data = value
            formattedDate = dateFormatter
                .format(value.timestamp)
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
        setOnClickListener { data = data.copy(isRead = !data.isRead) }
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
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val padding = config.notePadding
        val textWidth = (widthSize - padding * 2).toInt().coerceAtLeast(0)

        bodyLayout = createBodyLayout(data.text, textWidth)

        val headerHeight = config.titleTextSize + padding * 2f
        val bodyHeight = (bodyLayout?.height ?: 0)
            .coerceAtLeast(config.bodyTextSize.toInt())
        val dateHeight = config.dateTextSize + padding * 2f

        val desiredHeight = (headerHeight + bodyHeight + dateHeight + padding * 2).toInt()
        setMeasuredDimension(
            widthSize,
            resolveSize(desiredHeight, heightMeasureSpec)
        )
    }

    private fun createBodyLayout(text: String, width: Int): StaticLayout {
        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG)
            .apply { textSize = config.bodyTextSize }
        return StaticLayout.Builder
            .obtain(text, 0, text.length, paint, width)
            .setMaxLines(2)
            .setEllipsize(TextUtils.TruncateAt.END)
            .build()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        renderer.draw(
            canvas = canvas,
            note = data,
            config = config,
            formattedDate = formattedDate,
            bodyLayout = bodyLayout,
            width = width.toFloat(),
            height = height.toFloat()
        )
    }
}