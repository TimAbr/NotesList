package com.example.noteslist.presentation.note_details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.noteslist.R
import com.example.noteslist.presentation.common.theme.NotesListTheme
import com.example.noteslist.presentation.note_details.components.ImportantToggle
import com.example.noteslist.presentation.note_details.components.NoteTextInput
import com.example.noteslist.presentation.note_details.components.NoteTitleInput
import com.example.noteslist.presentation.note_details.components.ReadStatusSection
import com.example.noteslist.presentation.note_details.components.SaveButton
import com.example.noteslist.presentation.note_details.components.TimestampLabel
import java.time.Instant

@Composable
fun NoteDetailsScreen(
    viewModel: NoteDetailsViewModel,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    NoteDetailsBase(
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
            error = state.titleError,
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
