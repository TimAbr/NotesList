package com.example.noteslist.presentation.notes_list.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.graphics.Typeface
import android.text.*
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

    private var isImportant = false
    private var isRead = false
    private var titleText: String = ""
    private var bodyText: String = ""
    private var dateText: String = ""

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
    private val commonPath = Path()
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
            isRead = getBoolean(R.styleable.NoteView_isRead, false)
            isImportant = getBoolean(R.styleable.NoteView_isImportant, false)

            cornerRadius = getDimension(R.styleable.NoteView_noteCornerRadius, 0f)
            noteElevation = getDimension(R.styleable.NoteView_noteElevation, 0f)

            unreadHeaderColor = getColor(R.styleable.NoteView_noteHeaderColor, Color.TRANSPARENT)
            unreadBgColor = getColor(R.styleable.NoteView_noteBackgroundColor, Color.TRANSPARENT)
            unreadTitleColor = getColor(R.styleable.NoteView_noteTitleColor, Color.BLACK)
            unreadTextColor = getColor(R.styleable.NoteView_noteTextColor, Color.DKGRAY)

            titleTextSize = getDimension(R.styleable.NoteView_noteTitleTextSize, 0f)
            bodyTextSize = getDimension(R.styleable.NoteView_noteBodyTextSize, 0f)
            dateTextSize = getDimension(R.styleable.NoteView_noteDateTextSize, 0f)
            notePadding = getDimension(R.styleable.NoteView_notePadding, 0f)

            val readColor = context.getColor(R.color.note_read_all_bg)
            val readText = context.getColor(R.color.note_read_text)

            readHeaderColor = readColor
            readBgColor = readColor
            readTitleColor = readText
            readTextColor = readText
        }
    }

    private fun setupOutline() {
        clipToOutline = true
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, cornerRadius)
            }
        }
    }

    fun toggleReadState() {
        isRead = !isRead
        invalidate()
    }

    fun setNoteData(note: Note) {
        titleText = note.title
        bodyText = note.text
        isImportant = note.isImportant
        isRead = note.isRead

        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
            .withZone(ZoneId.systemDefault())
        dateText = formatter.format(note.timestamp)

        invalidate()
        requestLayout()
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
        val staticLayout = createStaticLayout(bodyText, textWidth)

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

        val currentHeaderColor = if (isRead) readHeaderColor else unreadHeaderColor
        val currentBgColor = if (isRead) readBgColor else unreadBgColor
        val currentTitleColor = if (isRead) readTitleColor else unreadTitleColor
        val currentContentColor = if (isRead) readTextColor else unreadTextColor

        val w = width.toFloat()
        val h = height.toFloat()
        val padding = notePadding
        val headerH = titleTextSize + padding * 2f

        commonPath.reset()
        commonPath.addRoundRect(
            0f,
            0f,
            w,
            h,
            cornerRadius,
            cornerRadius,
            Path.Direction.CW
        )
        canvas.clipPath(commonPath)

        bgPaint.color = currentHeaderColor
        canvas.drawRect(0f, 0f, w, headerH, bgPaint)

        bgPaint.color = currentBgColor
        canvas.drawRect(0f, headerH, w, h, bgPaint)

        titlePaint.color = currentTitleColor
        titlePaint.textSize = titleTextSize

        var titleX = padding
        if (isImportant) {
            drawImportantIcon(canvas, padding, headerH / 2)
            titleX += titlePaint.textSize * TITLE_X_OFFSET_RATIO
        }

        val titleY = (headerH / 2) - ((titlePaint.descent() + titlePaint.ascent()) / 2)
        canvas.drawText(titleText, titleX, titleY, titlePaint)

        contentPaint.color = currentContentColor
        contentPaint.textSize = bodyTextSize

        val staticLayout = createStaticLayout(bodyText, (w - padding * 2)
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
        canvas.drawText(dateText, padding, h - padding, contentPaint)
        if (isRead) drawReadIcon(
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