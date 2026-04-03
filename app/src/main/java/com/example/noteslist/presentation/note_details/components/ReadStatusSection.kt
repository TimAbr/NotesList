package com.example.noteslist.presentation.note_details.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.example.noteslist.R
import kotlinx.coroutines.delay

@Composable
fun ReadStatusSection(
    isRead: Boolean,
    onReadToggle: (Boolean) -> Unit
) {
    var showHint by remember { mutableStateOf(false) }
    val visibleState = remember { MutableTransitionState(false) }


    visibleState.targetState = showHint

    val haptic = LocalHapticFeedback.current

    LaunchedEffect(showHint) {
        if (showHint) {
            delay(2000)
            showHint = false
        }
    }

    Box(contentAlignment = Alignment.Companion.TopStart) {
        Box(
            modifier = Modifier.Companion
                .clip(CircleShape)
                .combinedClickable(
                    onClick = { showHint = true },
                    onLongClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.Companion.LongPress)
                        onReadToggle(!isRead)
                        showHint = false
                    }
                )
        ) {
            Text(
                text = stringResource(if (isRead) R.string.note_read else R.string.note_not_read),
                style = MaterialTheme.typography.bodyMedium,
                color = if (isRead)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.secondary
            )
        }

        if (visibleState.targetState || !visibleState.isIdle) {
            Popup(
                offset = IntOffset(x = 0, y = 70),
                onDismissRequest = { showHint = false }
            ) {
                AnimatedVisibility(
                    visibleState = visibleState,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(4.dp),
                        tonalElevation = 4.dp
                    ) {
                        Text(
                            text = stringResource(R.string.read_toggle_hint),
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.Companion.padding(
                                horizontal = 8.dp,
                                vertical = 4.dp
                            ),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}