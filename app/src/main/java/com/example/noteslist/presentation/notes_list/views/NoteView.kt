package com.example.noteslist.presentation.notes_list.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.Typeface
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.ColorInt
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.withTranslation
import com.example.noteslist.R
import com.example.noteslist.domain.models.Note
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class NoteView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var data: Note = Note(0, "", "")
        set(value) {
            field = value
            formattedDate = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
                .withZone(ZoneId.systemDefault())
                .format(value.timestamp)
            requestLayout()
            invalidate()
        }

    private var formattedDate: String = ""

    private var cornerRadius: Float = 0f
    private var noteElevation: Float = 0f
    private var titleTextSize: Float = 0f
    private var bodyTextSize: Float = 0f
    private var dateTextSize: Float = 0f
    private var notePadding: Float = 0f

    @ColorInt
    private var unreadHeaderColor: Int = 0
    @ColorInt
    private var unreadBgColor: Int = 0
    @ColorInt
    private var unreadTitleColor: Int = 0
    @ColorInt
    private var unreadTextColor: Int = 0

    @ColorInt
    private var readHeaderColor: Int = 0
    @ColorInt
    private var readBgColor: Int = 0
    @ColorInt
    private var readTitleColor: Int = 0
    @ColorInt
    private var readTextColor: Int = 0

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    private val contentPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val importantIcon = AppCompatResources.getDrawable(context, R.drawable.baseline_star_24)
    private val readIcon = AppCompatResources.getDrawable(context, R.drawable.check_circle_24)


    init {
        readAttributes(attrs, defStyleAttr)
        setupOutline()
    }


    private fun readAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        parseAttributes(attrs, defStyleAttr)

        elevation = noteElevation
        setOnClickListener { toggleReadState() }
    }


    private fun parseAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        context.withStyledAttributes(attrs, R.styleable.NoteView, defStyleAttr, R.style.NoteStyle) {
            val isRead = getBoolean(R.styleable.NoteView_isRead, false)
            val isImportant = getBoolean(R.styleable.NoteView_isImportant, false)
            data = data.copy(isRead = isRead, isImportant = isImportant)

            cornerRadius = getDimension(R.styleable.NoteView_noteCornerRadius, 0f)
            noteElevation = getDimension(R.styleable.NoteView_noteElevation, 0f)

            titleTextSize = getDimension(R.styleable.NoteView_noteTitleTextSize, 0f)
            bodyTextSize = getDimension(R.styleable.NoteView_noteBodyTextSize, 0f)
            dateTextSize = getDimension(R.styleable.NoteView_noteDateTextSize, 0f)
            notePadding = getDimension(R.styleable.NoteView_notePadding, 0f)
            
            val readStyleId = getResourceId(R.styleable.NoteView_noteReadStyle, R.style.NoteStyle_Read)
            val unreadStyleId = getResourceId(R.styleable.NoteView_noteUnreadStyle, R.style.NoteStyle_NotRead)

            val unreadColors = getColorsFromTheme(unreadStyleId)
            unreadHeaderColor = unreadColors.headerColor
            unreadBgColor = unreadColors.bgColor
            unreadTitleColor = unreadColors.titleColor
            unreadTextColor = unreadColors.textColor

            val readColors = getColorsFromTheme(readStyleId)
            readHeaderColor = readColors.headerColor
            readBgColor = readColors.bgColor
            readTitleColor = readColors.titleColor
            readTextColor = readColors.textColor
        }
    }

    private fun getColorsFromTheme(styleId: Int): NoteColors {
        val attrs = context.obtainStyledAttributes(styleId, R.styleable.NoteView)
        val colors = NoteColors(
            headerColor = attrs.getColor(R.styleable.NoteView_noteHeaderColor, Color.TRANSPARENT),
            bgColor = attrs.getColor(R.styleable.NoteView_noteBackgroundColor, Color.TRANSPARENT),
            titleColor = attrs.getColor(R.styleable.NoteView_noteTitleColor, Color.BLACK),
            textColor = attrs.getColor(R.styleable.NoteView_noteTextColor, Color.DKGRAY)
        )
        attrs.recycle()
        return colors
    }

    private data class NoteColors(
        @ColorInt val headerColor: Int,
        @ColorInt val bgColor: Int,
        @ColorInt val titleColor: Int,
        @ColorInt val textColor: Int
    )

    private fun setupOutline() {
        clipToOutline = true
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, cornerRadius)
            }
        }
    }

    fun toggleReadState() {
        data = data.copy(isRead = !data.isRead)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val desiredWidth = widthSize

        titlePaint.textSize = titleTextSize
        contentPaint.textSize = bodyTextSize

        val headerHeight = titleTextSize + notePadding * 2f

        val textWidth = (desiredWidth - notePadding * 2).toInt().coerceAtLeast(0)
        val staticLayout = createStaticLayout(data.text, textWidth)

        val fontMetrics = contentPaint.fontMetricsInt
        val singleLineHeight = fontMetrics.bottom - fontMetrics.top

        val bodyHeight = staticLayout.height.coerceAtLeast(singleLineHeight)

        val dateHeight = dateTextSize + notePadding * 2f

        val desiredHeight = (headerHeight + bodyHeight + dateHeight).toInt()

        val measuredWidth = desiredWidth
        val measuredHeight = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> minOf(desiredHeight, heightSize)
            else -> desiredHeight
        }

        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val currentHeaderColor = if (data.isRead) readHeaderColor else unreadHeaderColor
        val currentBgColor = if (data.isRead) readBgColor else unreadBgColor
        val currentTitleColor = if (data.isRead) readTitleColor else unreadTitleColor
        val currentContentColor = if (data.isRead) readTextColor else unreadTextColor

        val w = width.toFloat()
        val h = height.toFloat()
        val padding = notePadding
        val headerH = titleTextSize + padding * 2f

        bgPaint.color = currentHeaderColor
        canvas.drawRect(0f, 0f, w, headerH, bgPaint)

        bgPaint.color = currentBgColor
        canvas.drawRect(0f, headerH, w, h, bgPaint)

        titlePaint.color = currentTitleColor
        titlePaint.textSize = titleTextSize

        var titleX = padding
        if (data.isImportant) {
            drawImportantIcon(canvas, padding, headerH / 2)
            titleX += titlePaint.textSize * TITLE_X_OFFSET_RATIO
        }

        val titleY = (headerH / 2) - ((titlePaint.descent() + titlePaint.ascent()) / 2)
        canvas.drawText(data.title, titleX, titleY, titlePaint)

        contentPaint.color = currentContentColor
        contentPaint.textSize = bodyTextSize

        val staticLayout = createStaticLayout(data.text, (w - padding * 2)
            .toInt()
            .coerceAtLeast(0))

        canvas.withTranslation(padding, headerH + padding) {
            staticLayout.draw(this)

            if (staticLayout.lineCount >= MAX_BODY_LINES) {
                val lastLine = staticLayout.lineCount - 1
                drawFadeEffect(
                    this,
                    (w - padding * 2).coerceAtLeast(0f),
                    staticLayout.getLineTop(lastLine).toFloat(),
                    staticLayout.getLineBottom(lastLine).toFloat(),
                    currentBgColor
                )
            }
        }

        contentPaint.textSize = dateTextSize
        canvas.drawText(formattedDate, padding, h - padding, contentPaint)
        if (data.isRead) drawReadIcon(
            canvas,
            w - padding * READ_ICON_X_PADDING_RATIO,
            h - padding * READ_ICON_Y_PADDING_RATIO
        )
    }

    private fun createStaticLayout(text: String, width: Int): StaticLayout {
        return StaticLayout.Builder
            .obtain(text, 0, text.length, contentPaint, width)
            .setMaxLines(MAX_BODY_LINES)
            .setEllipsize(TextUtils.TruncateAt.END)
            .build()
    }

    private fun drawFadeEffect(
        canvas: Canvas,
        width: Float,
        top: Float,
        bottom: Float,
        bgColor: Int
    ) {
        val fadeWidth = width * FADE_WIDTH_RATIO
        val gradient = LinearGradient(
            width - fadeWidth, 0f, width, 0f,
            intArrayOf(Color.TRANSPARENT, bgColor),
            null, Shader.TileMode.CLAMP
        )
        bgPaint.shader = gradient
        canvas.drawRect(width - fadeWidth, top, width, bottom, bgPaint)
        bgPaint.shader = null
    }

    private fun drawImportantIcon(canvas: Canvas, x: Float, y: Float) {
        importantIcon?.let { icon ->
            val size = (titlePaint.textSize * ICON_SIZE_RATIO).toInt()
            icon.setBounds(
                x.toInt(),
                (y - size / 2).toInt(),
                (x + size).toInt(),
                (y + size / 2).toInt()
            )
            icon.setTint(context.getColor(R.color.note_star_color))
            icon.draw(canvas)
        }
    }

    private fun drawReadIcon(canvas: Canvas, x: Float, y: Float) {
        readIcon?.let { icon ->
            val size = (titlePaint.textSize * ICON_SIZE_RATIO).toInt()
            val left = (x - size / 2).toInt()
            val top = (y - size / 2).toInt()
            icon.setBounds(left, top, left + size, top + size)
            icon.setTint(context.getColor(R.color.note_checkmark_color))
            icon.draw(canvas)
        }
    }


    companion object {
        private const val MAX_BODY_LINES = 2

        private const val TITLE_X_OFFSET_RATIO = 1.5f

        private const val FADE_WIDTH_RATIO = 0.3f
        private const val ICON_SIZE_RATIO = 1.2f

        private const val READ_ICON_X_PADDING_RATIO = 1.5f
        private const val READ_ICON_Y_PADDING_RATIO = 1.2f
    }
}