package com.example.noteslist.presentation.notes_list.views.note

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.Typeface
import android.text.StaticLayout
import android.text.TextPaint
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.withTranslation
import com.example.noteslist.R
import com.example.noteslist.domain.models.Note

class NoteRenderer(private val context: Context) {

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD))
    }
    private val contentPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    private val importantIcon = AppCompatResources
        .getDrawable(context, R.drawable.baseline_star_24)
    private val readIcon = AppCompatResources
        .getDrawable(context, R.drawable.check_circle_24)

    private var fadeGradient: Shader? = null
    private var fadeGradientWidth: Float = -1f
    private var fadeGradientColor: Int = -1

    fun draw(
        canvas: Canvas,
        note: Note,
        formattedDate: String,
        config: NoteViewConfig,
        bodyLayout: StaticLayout?,
        width: Float,
        height: Float
    ) {
        val colors = if (note.isRead) config.readColors else config.unreadColors
        val padding = config.notePadding
        val headerH = config.titleTextSize + padding * 2f

        drawBackground(canvas, width, height, headerH, colors)

        drawHeader(canvas, note, config, colors, padding, headerH)

        drawBody(canvas, bodyLayout, colors, config, padding, headerH, width)

        drawFooter(canvas, note,  config, formattedDate, colors, width, height, padding)
    }

    private fun drawBackground(
        canvas: Canvas,
        w: Float,
        h: Float,
        headerH:
        Float,
        colors: NoteColors
    ) {
        bgPaint.shader = null
        bgPaint.color = colors.headerColor
        canvas.drawRect(0f, 0f, w, headerH, bgPaint)

        bgPaint.color = colors.bgColor
        canvas.drawRect(0f, headerH, w, h, bgPaint)
    }

    private fun drawHeader(
        canvas: Canvas,
        note: Note,
        config: NoteViewConfig,
        colors: NoteColors,
        padding: Float,
        headerH: Float
    ) {
        titlePaint.color = colors.titleColor
        titlePaint.textSize = config.titleTextSize

        var titleX = padding
        if (note.isImportant) {
            val size = (titlePaint.textSize * ICON_SIZE_RATIO).toInt()
            importantIcon?.let {
                it.setBounds(
                    padding.toInt(),
                    (headerH / 2 - size / 2).toInt(),
                    (padding + size).toInt(),
                    (headerH / 2 + size / 2).toInt()
                )
                it.setTint(context.getColor(R.color.note_star_color))
                it.draw(canvas)
            }
            titleX += titlePaint.textSize * TITLE_X_OFFSET_RATIO
        }

        val titleY = (headerH / 2) - ((titlePaint.descent() + titlePaint.ascent()) / 2)
        canvas.drawText(note.title, titleX, titleY, titlePaint)
    }

    private fun drawBody(
        canvas: Canvas,
        layout: StaticLayout?,
        colors: NoteColors,
        config: NoteViewConfig,
        padding: Float,
        headerH: Float,
        w: Float
    ) {
        contentPaint.color = colors.textColor
        contentPaint.textSize = config.bodyTextSize

        canvas.withTranslation(padding, headerH + padding) {
            layout?.let {
                it.draw(this)
                if (it.lineCount >= MAX_BODY_LINES) {
                    val lastLine = it.lineCount - 1
                    drawFadeEffect(
                        this,
                        (w - padding * 2).coerceAtLeast(0f),
                        it.getLineTop(lastLine).toFloat(),
                        it.getLineBottom(lastLine).toFloat(),
                        colors.bgColor
                    )
                }
            }
        }
    }

    private fun drawFooter(
        canvas: Canvas,
        note: Note,
        config: NoteViewConfig,
        formattedDate: String,
        colors: NoteColors,
        w: Float,
        h: Float,
        padding: Float
    ) {
        contentPaint.textSize = config.dateTextSize
        canvas.drawText(formattedDate, padding, h - padding, contentPaint)

        if (note.isRead) {
            val size = (config.titleTextSize * ICON_SIZE_RATIO).toInt()
            val x = w - padding * READ_ICON_X_PADDING_RATIO
            val y = h - padding * READ_ICON_Y_PADDING_RATIO
            readIcon?.let {
                val left = (x - size / 2).toInt()
                val top = (y - size / 2).toInt()
                it.setBounds(left, top, left + size, top + size)
                it.setTint(context.getColor(R.color.note_checkmark_color))
                it.draw(canvas)
            }
        }
    }

    private fun drawFadeEffect(
        canvas: Canvas,
        width: Float,
        top: Float,
        bottom: Float,
        bgColor: Int
    ) {
        val fadeWidth = width * FADE_WIDTH_RATIO
        if (fadeGradient == null || width != fadeGradientWidth || bgColor != fadeGradientColor) {
            fadeGradientWidth = width
            fadeGradientColor = bgColor
            fadeGradient = LinearGradient(
                width - fadeWidth, 0f, width, 0f,
                intArrayOf(Color.TRANSPARENT, bgColor),
                null, Shader.TileMode.CLAMP
            )
        }
        bgPaint.shader = fadeGradient
        canvas.drawRect(width - fadeWidth, top, width, bottom, bgPaint)
        bgPaint.shader = null
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