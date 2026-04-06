package com.example.noteslist.presentation.note_details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.noteslist.R
import com.example.noteslist.presentation.common.theme.NotesListTheme
import kotlinx.coroutines.delay
import java.time.Instant

@Composable
fun NoteDetailsScreen(
    viewModel: NoteDetailsViewModel,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    NoteDetailsContent(
        state = state,
        onTitleChange = viewModel::onTitleChange,
        onTextChange = viewModel::onTextChange,
        onImportantToggle = viewModel::onImportantToggle,
        onReadToggle = viewModel::onReadToggle,
        onSave = viewModel::onSave,
        onBack = onBack,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailsBase(
    state: NoteDetailsScreenState,
    onTitleChange: (String) -> Unit,
    onTextChange: (String) -> Unit,
    onImportantToggle: (Boolean) -> Unit,
    onReadToggle: (Boolean) -> Unit,
    onSave: () -> Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
){
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(
                            if (state.mode is NoteDetailsScreenMode.Edit)
                                R.string.edit_note
                            else
                                R.string.add_note
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back_note_details),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        NoteDetailsContent(
            state = state,
            onTitleChange = onTitleChange,
            onTextChange = onTextChange,
            onImportantToggle = onImportantToggle,
            onReadToggle = onReadToggle,
            onSave = onSave,
            modifier = modifier.padding(paddingValues),
            onBack = onBack
        )
    }
}

@Composable
fun NoteDetailsContent(
    state: NoteDetailsScreenState,
    onTitleChange: (String) -> Unit,
    onTextChange: (String) -> Unit,
    onImportantToggle: (Boolean) -> Unit,
    onReadToggle: (Boolean) -> Unit,
    onSave: () -> Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
    ) {
        NoteTitleInput(
            title = state.title,
            isError = state.titleError,
            onTitleChange = onTitleChange
        )

        Spacer(Modifier.height(16.dp))

        NoteTextInput(
            text = state.text,
            onTextChange = onTextChange
        )

        Spacer(Modifier.height(8.dp))

        ImportantToggle(
            isImportant = state.isImportant,
            onToggle = onImportantToggle
        )

        Spacer(Modifier.height(8.dp))

        if (state.mode is NoteDetailsScreenMode.Edit) {
            ReadStatusSection(
                isRead = state.isRead,
                onReadToggle = onReadToggle
            )

            Spacer(Modifier.height(20.dp))

            TimestampLabel(formattedDate = state.formattedDate)
        }

        Spacer(modifier = Modifier.weight(1f))

        SaveButton(
            mode = state.mode,
            onSave = onSave,
            onBack = onBack
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

@OptIn(ExperimentalMaterial3Api::class)
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
                color = if (isRead) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
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
                color = MaterialTheme.colorScheme.secondary,
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
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun SaveButton(
    mode: NoteDetailsScreenMode,
    onSave: () -> Boolean,
    onBack: () -> Unit
) {
    val buttonText = if (mode is NoteDetailsScreenMode.Edit) {
        stringResource(R.string.note_details_save_edit)
    } else {
        stringResource(R.string.note_details_save_add)
    }

    Button(
        onClick = {
            if (onSave()){
                onBack()
            }
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = buttonText)
    }
}

@Preview(showBackground = true)
@Composable
fun NoteDetailsCreatePreview() {
    NotesListTheme {
        NoteDetailsBase(
            state = NoteDetailsScreenState(mode = NoteDetailsScreenMode.Create),
            onTitleChange = {},
            onTextChange = {},
            onImportantToggle = {},
            onReadToggle = {},
            onSave = {true},
            onBack = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NoteDetailsEditPreview() {
    NotesListTheme {
        NoteDetailsBase(
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
            onSave = {true},
            onBack = {},
        )
    }

}
