package com.example.noteslist.presentation.note_details

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.noteslist.R
import com.example.noteslist.presentation.common.navigation.AppNavigator
import com.example.noteslist.presentation.common.theme.NotesListTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NoteDetailsFragment : Fragment(R.layout.fragment_note_details) {

    private val viewModel: NoteDetailsViewModel by viewModels()

    @Inject
    lateinit var navigator: AppNavigator

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBackCallback()

        view.findViewById<ComposeView>(R.id.composeView).apply {
            setContent {
                NotesListTheme {
                    NoteDetailsScreen(
                        viewModel = viewModel,
                        onBack = {
                            handleBack()
                        }
                    )
                }
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
        navigator.handleBackPress(viewModel.isDirty())
    }
}
