package com.example.noteslist.presentation

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.R
import com.example.noteslist.presentation.notes_list.NotesListViewModel
import com.example.noteslist.presentation.notes_list.recycler_view.NoteSpaceItemDecoration
import com.example.noteslist.presentation.notes_list.recycler_view.NotesAdapter
import com.example.noteslist.presentation.notes_list.recycler_view.delegates.DateHeaderDelegate
import com.example.noteslist.presentation.notes_list.recycler_view.delegates.NoteDelegate
import com.example.noteslist.presentation.notes_list.recycler_view.delegates.NoteStackDelegate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: NotesListViewModel by viewModels()

    private val adapter by lazy {
        NotesAdapter(
            listOf(
                DateHeaderDelegate(),
                NoteDelegate { viewModel.onNoteClick(it) },
                NoteStackDelegate(
                    onNoteClick = { viewModel.onNoteClick(it) },
                    onStackClick = { viewModel.onStackClick(it) }
                )
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupRecyclerView()
        collectData()
    }

    private fun collectData() {
        lifecycleScope.launch {
            viewModel.items.collect {
                adapter.submitList(it)
            }
        }
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val spacing = resources.getDimensionPixelSize(R.dimen.note_list_spacing)
        recyclerView.addItemDecoration(NoteSpaceItemDecoration(spacing))

        recyclerView.adapter = adapter
    }
}
