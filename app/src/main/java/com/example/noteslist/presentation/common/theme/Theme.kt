package com.example.noteslist.presentation.common.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = NoteUnreadHeader,
    onPrimary = White,
    surface = NoteUnreadBackground,
    onSurface = NoteUnreadTitle,
    onSurfaceVariant = NoteUnreadText,
    secondary = NoteReadText,
    surfaceVariant = NoteReadAllBg,
    tertiary = NoteStarColor,
    outline = DateHeaderStroke
)

@Composable
fun NotesListTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content,
        typography = Typography
    )
}
