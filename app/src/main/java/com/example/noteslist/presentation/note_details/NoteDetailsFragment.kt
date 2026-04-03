package com.example.noteslist.presentation.note_details

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.noteslist.R
import com.example.noteslist.presentation.MainActivity

class NoteDetailsFragment : Fragment(R.layout.fragment_note_details) {

    private val viewModel: NoteDetailsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBackCallback()

        view.findViewById<ComposeView>(R.id.composeView).apply {
            setContent {
                NoteDetailsScreen(
                    viewModel = viewModel,
                    onBack = {
                        handleBack()
                    }
                )
            }
        }
    }

    private fun setupBackCallback() {
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(
                viewLifecycleOwner,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        handleBack()
                    }
                }
            )
    }

    private fun handleBack() {
        val navigator = (activity as? MainActivity)?.navigator
        navigator?.handleBackPress(viewModel.isDirty())
    }
}