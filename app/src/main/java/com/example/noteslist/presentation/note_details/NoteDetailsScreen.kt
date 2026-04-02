package com.example.noteslist.presentation.note_details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.noteslist.R
import kotlinx.coroutines.delay
import java.time.Instant

@Composable
fun NoteDetailsScreen(
    viewModel: NoteDetailsViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    NoteDetailsContent(
        state = state,
        onTitleChange = viewModel::onTitleChange,
        onTextChange = viewModel::onTextChange,
        onImportantToggle = viewModel::onImportantToggle,
        onReadToggle = viewModel::onReadToggle,
        onSave = viewModel::onSave,
        modifier = modifier
    )
}

@Composable
fun NoteDetailsContent(
    state: NoteDetailsScreenState,
    onTitleChange: (String) -> Unit,
    onTextChange: (String) -> Unit,
    onImportantToggle: (Boolean) -> Unit,
    onReadToggle: (Boolean) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NoteTitleInput(
            title = state.title,
            isError = state.titleError,
            onTitleChange = onTitleChange
        )

        NoteTextInput(
            text = state.text,
            onTextChange = onTextChange
        )

        ImportantToggle(
            isImportant = state.isImportant,
            onToggle = onImportantToggle
        )

        if (state.mode is NoteDetailsScreenMode.Edit) {
            ReadStatusSection(
                isRead = state.isRead,
                onReadToggle = onReadToggle
            )

            TimestampLabel(formattedDate = state.formattedDate)
        }

        Spacer(modifier = Modifier.weight(1f))

        SaveButton(
            mode = state.mode,
            onSave = onSave
        )
    }
}

@Composable
fun NoteTitleInput(
    title: String,
    isError: Boolean,
    onTitleChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text(stringResource(R.string.note_details_title_hint)) },
            singleLine = true,
            isError = isError,
            modifier = Modifier.fillMaxWidth()
        )
        if (isError) {
            Text(
                text = stringResource(R.string.note_details_error_empty),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun NoteTextInput(
    text: String,
    onTextChange: (String) -> Unit
) {
    OutlinedTextField(
        value = text,
        onValueChange = onTextChange,
        label = { Text(stringResource(R.string.note_details_text_hint)) },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp)
    )
}

@Composable
fun ImportantToggle(
    isImportant: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.note_details_important_label),
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = isImportant,
            onCheckedChange = onToggle
        )
    }
}

@Composable
fun ReadStatusSection(
    isRead: Boolean,
    onReadToggle: (Boolean) -> Unit
) {
    var showHint by remember { mutableStateOf(false) }

    val haptic = LocalHapticFeedback.current

    LaunchedEffect(showHint) {
        if (showHint) {
            delay(2000)
            showHint = false
        }
    }

    Column(
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .combinedClickable(
                    onClick = {
                        showHint = true
                    },
                    onLongClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onReadToggle(!isRead)
                        showHint = false
                    }
                )
        ) {
            Text(
                text = if (isRead)
                    stringResource(R.string.note_read)
                else
                    stringResource(R.string.note_not_read),
                style = MaterialTheme.typography.bodyMedium,
                color = if (isRead) Color(0xFF4CAF50) else Color.Gray
            )
        }

        AnimatedVisibility(
            visible = showHint,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = stringResource(R.string.read_toggle_hint),
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                fontSize = 10.sp,
            )
        }
    }
}

@Composable
fun TimestampLabel(formattedDate: String) {
    if (formattedDate.isNotEmpty()) {
        Text(
            text = stringResource(R.string.note_details_created_at_label) + " $formattedDate",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

@Composable
fun SaveButton(
    mode: NoteDetailsScreenMode,
    onSave: () -> Unit
) {
    val buttonText = if (mode is NoteDetailsScreenMode.Edit) {
        stringResource(R.string.note_details_save_edit)
    } else {
        stringResource(R.string.note_details_save_add)
    }

    Button(
        onClick = onSave,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = buttonText)
    }
}

@Preview(showBackground = true)
@Composable
fun NoteDetailsCreatePreview() {
    NoteDetailsContent(
        state = NoteDetailsScreenState(mode = NoteDetailsScreenMode.Create),
        onTitleChange = {},
        onTextChange = {},
        onImportantToggle = {},
        onReadToggle = {},
        onSave = {}
    )
}

@Preview(showBackground = true)
@Composable
fun NoteDetailsEditPreview() {
    NoteDetailsContent(
        state = NoteDetailsScreenState(
            title = "Тестовая заметка",
            text = "Текст тестовой заметки",
            isImportant = true,
            isRead = false,
            creationTimestamp = Instant.now(),
            formattedDate = "Сегодня",
            mode = NoteDetailsScreenMode.Edit(1L)
        ),
        onTitleChange = {},
        onTextChange = {},
        onImportantToggle = {},
        onReadToggle = {},
        onSave = {}
    )
}
