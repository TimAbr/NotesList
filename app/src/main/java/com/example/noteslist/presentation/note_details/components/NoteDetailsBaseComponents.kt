package com.example.noteslist.presentation.note_details.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.noteslist.R
import com.example.noteslist.presentation.note_details.NoteDetailsScreenMode

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
            if (onSave()) {

                onBack()
            }
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = buttonText)
    }
}