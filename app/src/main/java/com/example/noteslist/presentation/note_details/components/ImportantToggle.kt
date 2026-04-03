package com.example.noteslist.presentation.note_details.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.noteslist.R
import com.example.noteslist.presentation.common.theme.NoteReadText
import com.example.noteslist.presentation.common.theme.NoteStarColor

@Composable
fun ImportantToggle(
    isImportant: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val tint by animateColorAsState(
        targetValue = if (isImportant) NoteStarColor else NoteReadText,
        label = "StarTint"
    )

    val scale by animateFloatAsState(
        targetValue = if (isImportant) 1.2f else 1.0f,
        animationSpec = if (isImportant) {
            keyframes {
                durationMillis = 300
                1.0f at 0
                1.5f at 100 using FastOutSlowInEasing
                1.2f at 300
            }
        } else {
            tween(durationMillis = 300)
        },
        label = "StarSinglePulse"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.note_details_important_label),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge
        )

        IconButton(
            onClick = { onToggle(!isImportant) }
        ) {
            Icon(
                imageVector = if (isImportant) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.scale(scale)
            )
        }
    }
}