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
import android.text.TextUtils
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.withTranslation
import com.example.noteslist.R
import com.example.noteslist.domain.models.Note
import com.example.noteslist.presentation.notes_list.views.ViewPaddings

class NoteViewRenderer(private val context: Context) {

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD))
    }
    private val contentPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    private var bodyLayout: StaticLayout? = null

    private val importantIcon = AppCompatResources
        .getDrawable(context, R.drawable.baseline_star_24)
    private val readIcon = AppCompatResources
        .getDrawable(context, R.drawable.check_circle_24)

    private var fadeGradient: Shader? = null
    private var fadeGradientWidth: Float = -1f
    private var fadeGradientColor: Int = -1

    fun createBodyLayout(
        text: String,
        width: Int,
        textSize: Float,
        textColor: Int
    ): Int {
        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            this.textSize = textSize
            color = textColor
        }
        bodyLayout = StaticLayout.Builder
            .obtain(text, 0, text.length, paint, width)
            .setMaxLines(MAX_BODY_LINES)
            .setEllipsize(TextUtils.TruncateAt.END)
            .build()
        return bodyLayout?.height ?: 0
    }

    fun draw(
        canvas: Canvas,
        note: Note,
        formattedDate: String,
        config: NoteViewConfig,
        paddings: ViewPaddings,
        width: Float,
        height: Float
    ) {
        val colors = if (note.isRead) config.readColors else config.unreadColors

        val headerH = paddings.paddingTop + config.titleTextSize + paddings.paddingTop

        drawBackground(canvas, width, height, headerH, colors)
        drawHeader(canvas, note, config, colors, paddings)
        drawBody(canvas, colors, config, paddings, headerH, width)
        drawFooter(canvas, note, config, formattedDate, colors, width, height, paddings)
    }

    private fun drawBackground(canvas: Canvas, w: Float, h: Float, headerH: Float, colors: NoteColors) {
        bgPaint.shader = null
        bgPaint.color = colors.headerColor
        canvas.drawRect(0f, 0f, w, headerH, bgPaint)

        bgPaint.color = colors.bgColor
        canvas.drawRect(0f, headerH, w, h, bgPaint)
    }

    private fun drawHeader(canvas: Canvas, note: Note, config: NoteViewConfig, colors: NoteColors, paddings: ViewPaddings) {
        titlePaint.color = colors.titleColor
        titlePaint.textSize = config.titleTextSize

        var titleX = paddings.paddingLeft.toFloat()
        val iconSize = (config.titleTextSize * ICON_SIZE_RATIO).toInt()

        if (note.isImportant) {
            importantIcon?.let {
                val iconTop = paddings.paddingTop + (config.titleTextSize - iconSize) / 2f
                it.setBounds(
                    paddings.paddingLeft,
                    iconTop.toInt(),
                    paddings.paddingLeft + iconSize,
                    (iconTop + iconSize).toInt()
                )
                it.setTint(context.getColor(R.color.note_star_color))
                it.draw(canvas)
            }
            titleX += config.titleTextSize * TITLE_X_OFFSET_RATIO
        }

        val titleY = paddings.paddingTop + config.titleTextSize - (titlePaint.descent() / 2f)
        canvas.drawText(note.title, titleX, titleY, titlePaint)
    }

    private fun drawBody(canvas: Canvas, colors: NoteColors, config: NoteViewConfig, paddings: ViewPaddings, headerH: Float, w: Float) {
        contentPaint.color = colors.textColor
        contentPaint.textSize = config.bodyTextSize

        val bodyY = headerH + paddings.paddingTop

        canvas.withTranslation(paddings.paddingLeft.toFloat(), bodyY) {
            bodyLayout?.let {
                it.draw(this)
                if (it.lineCount >= MAX_BODY_LINES) {
                    val lastLine = it.lineCount - 1
                    drawFadeEffect(
                        this,
                        (w - paddings.paddingLeft - paddings.paddingRight).coerceAtLeast(0f),
                        it.getLineTop(lastLine).toFloat(),
                        it.getLineBottom(lastLine).toFloat(),
                        colors.bgColor
                    )
                }
            }
        }
    }

    private fun drawFooter(canvas: Canvas, note: Note, config: NoteViewConfig, formattedDate: String, colors: NoteColors, w: Float, h: Float, paddings: ViewPaddings) {
        contentPaint.textSize = config.dateTextSize

        val dateY = h - paddings.paddingBottom

        canvas.drawText(formattedDate, paddings.paddingLeft.toFloat(), dateY, contentPaint)

        if (note.isRead) {
            val iconSize = (config.titleTextSize * ICON_SIZE_RATIO).toInt()
            readIcon?.let {
                val x = w - paddings.paddingRight
                val iconCenterY = dateY - (config.dateTextSize / 2f)
                it.setBounds(
                    (x - iconSize).toInt(),
                    (iconCenterY - iconSize / 2f).toInt(),
                    x.toInt(),
                    (iconCenterY + iconSize / 2f).toInt()
                )
                it.setTint(context.getColor(R.color.note_checkmark_color))
                it.draw(canvas)
            }
        }
    }

    private fun drawFadeEffect(canvas: Canvas, width: Float, top: Float, bottom: Float, bgColor: Int) {
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
    }
}