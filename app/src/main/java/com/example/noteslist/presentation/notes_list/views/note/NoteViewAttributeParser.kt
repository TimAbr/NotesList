package com.example.noteslist.presentation.notes_list.views.note

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.util.AttributeSet
import androidx.core.content.res.use
import com.example.noteslist.R
import com.example.noteslist.domain.models.Note
import com.example.noteslist.presentation.common.NoteDateFormatter
import java.time.Instant

class NoteViewAttributeParser(private val noteDateFormatter: NoteDateFormatter) {

    fun parse(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ): Pair<NoteViewConfig, Note> = context.obtainStyledAttributes(
        attrs,
        R.styleable.NoteView,
        defStyleAttr,
        R.style.NoteStyle
    ).use { array ->
        array.run {
            val note = getNoteFromAttrs()

            val unreadStyleId = getResourceId(
                R.styleable.NoteView_noteUnreadStyle,
                R.style.NoteStyle_NotRead
            )
            val readStyleId = getResourceId(
                R.styleable.NoteView_noteReadStyle,
                R.style.NoteStyle_Read
            )

            var unreadColors = getColorsFromTheme(
                context,
                unreadStyleId
            )
            var readColors = getColorsFromTheme(
                context,
                readStyleId
            )

            if (!note.isRead) {
                unreadColors = modifyThemeWithAttributes(unreadColors)
            } else {
                readColors = modifyThemeWithAttributes(readColors)
            }

            val config = getConfigFromAttrs(
                unreadColors,
                readColors
            )

            config to note
        }
    }

    private fun getColorsFromTheme(context: Context, styleId: Int): NoteColors =
        context.obtainStyledAttributes(styleId, R.styleable.NoteView).use { array ->
            array.run {
                NoteColors(
                    headerColor = getColor(
                        R.styleable.NoteView_noteHeaderColor,
                        DEFAULT_COLOR_BG
                    ),
                    bgColor = getColor(
                        R.styleable.NoteView_noteBackgroundColor,
                        DEFAULT_COLOR_BG
                    ),
                    titleColor = getColor(
                        R.styleable.NoteView_noteTitleColor,
                        DEFAULT_COLOR_TITLE
                    ),
                    textColor = getColor(
                        R.styleable.NoteView_noteTextColor,
                        DEFAULT_COLOR_TEXT
                    )
                )
            }
        }

    private fun TypedArray.getNoteFromAttrs(): Note {
        return Note(
            id = DEFAULT_ID,
            title = getString(
                R.styleable.NoteView_noteTitle
            ) ?: DEFAULT_STRING,
            text = getString(
                R.styleable.NoteView_noteBody
            ) ?: DEFAULT_STRING,
            isImportant = getBoolean(
                R.styleable.NoteView_isImportant,
                DEFAULT_BOOLEAN
            ),
            isRead = getBoolean(
                R.styleable.NoteView_isRead,
                DEFAULT_BOOLEAN
            ),
            timestamp = getString(R.styleable.NoteView_noteDate)?.let {
                try {
                    noteDateFormatter.parse(it)
                } catch (e: Exception) {
                    null
                }
            } ?: DEFAULT_TIME
        )
    }

    private fun TypedArray.modifyThemeWithAttributes(colors: NoteColors): NoteColors {
        return NoteColors(
            headerColor = getColor(
                R.styleable.NoteView_noteHeaderColor,
                colors.headerColor
            ),
            bgColor = getColor(
                R.styleable.NoteView_noteBackgroundColor,
                colors.bgColor
            ),
            titleColor = getColor(
                R.styleable.NoteView_noteTitleColor,
                colors.titleColor
            ),
            textColor = getColor(
                R.styleable.NoteView_noteTextColor,
                colors.textColor
            )
        )
    }

    private fun TypedArray.getConfigFromAttrs(
        unreadColors: NoteColors,
        readColors: NoteColors
    ): NoteViewConfig {
        return NoteViewConfig(
            cornerRadius = getDimension(
                R.styleable.NoteView_noteCornerRadius,
                DEFAULT_DIMEN
            ),
            noteElevation = getDimension(
                R.styleable.NoteView_noteElevation,
                DEFAULT_DIMEN
            ),
            titleTextSize = getDimension(
                R.styleable.NoteView_noteTitleTextSize,
                DEFAULT_DIMEN
            ),
            bodyTextSize = getDimension(
                R.styleable.NoteView_noteBodyTextSize,
                DEFAULT_DIMEN
            ),
            dateTextSize = getDimension(
                R.styleable.NoteView_noteDateTextSize,
                DEFAULT_DIMEN
            ),
            unreadColors = unreadColors,
            readColors = readColors
        )
    }

    companion object {
        private val DEFAULT_TIME = Instant.now()
        private const val DEFAULT_ID = 0L
        private const val DEFAULT_STRING = ""
        private const val DEFAULT_BOOLEAN = false
        private const val DEFAULT_DIMEN = 0f

        private const val DEFAULT_COLOR_BG = Color.TRANSPARENT
        private const val DEFAULT_COLOR_TITLE = Color.BLACK
        private const val DEFAULT_COLOR_TEXT = Color.DKGRAY
    }
}