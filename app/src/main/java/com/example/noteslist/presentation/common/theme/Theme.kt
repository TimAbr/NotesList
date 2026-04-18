package com.example.noteslist.presentation.common.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.example.noteslist.R

@Composable
fun NotesListTheme(
    content: @Composable () -> Unit
) {

    val colorScheme = lightColorScheme(
        primary = colorResource(R.color.theme_primary),
        onPrimary = colorResource(R.color.theme_on_primary),
        primaryContainer = colorResource(R.color.theme_primary_container),
        onPrimaryContainer = colorResource(R.color.theme_on_primary_container),

        secondary = colorResource(R.color.theme_secondary),
        onSecondary = colorResource(R.color.theme_on_secondary),
        secondaryContainer = colorResource(R.color.theme_secondary_container),
        onSecondaryContainer = colorResource(R.color.theme_on_secondary_container),

        background = colorResource(R.color.theme_background),
        onBackground = colorResource(R.color.theme_on_background),

        surface = colorResource(R.color.theme_surface),
        onSurface = colorResource(R.color.theme_on_surface),

        surfaceVariant = colorResource(R.color.theme_surface_variant),
        onSurfaceVariant = colorResource(R.color.theme_on_surface_variant),

        outline = colorResource(R.color.theme_outline),
        error = colorResource(R.color.theme_error),
        onError = colorResource(R.color.theme_on_error)
    )

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}