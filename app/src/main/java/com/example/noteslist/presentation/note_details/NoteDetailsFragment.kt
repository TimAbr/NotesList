package com.example.noteslist.presentation.note_details

import android.os.Bundle
import android.view.View
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.noteslist.R

class NoteDetailsFragment : Fragment(R.layout.fragment_note_details) {

    private val viewModel: NoteDetailsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ComposeView>(R.id.composeView).apply {
            setContent {
                NoteDetailsScreen(
                    viewModel = viewModel,
                    onBack = {
                        findNavController().popBackStack()
                    }
                )
            }
        }
    }
}