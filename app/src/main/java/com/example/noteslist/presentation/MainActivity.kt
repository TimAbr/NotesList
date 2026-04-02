package com.example.noteslist.presentation

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.R
import com.example.noteslist.data.datasources.notes.local.InMemoryNotesDataSource
import com.example.noteslist.data.repositories.NotesRepositoryImpl
import com.example.noteslist.presentation.notes_list.NotesListViewModel
import com.example.noteslist.presentation.notes_list.recycler_view.NoteSpaceItemDecoration
import com.example.noteslist.presentation.notes_list.recycler_view.NotesAdapter
import com.example.noteslist.presentation.common.RelativeDateFormatter
import com.example.noteslist.presentation.notes_list.recycler_view.delegates.DateHeaderDelegate
import com.example.noteslist.presentation.notes_list.recycler_view.delegates.NoteDelegate
import com.example.noteslist.presentation.notes_list.recycler_view.delegates.NoteStackDelegate
import com.example.noteslist.presentation.notes_list.recycler_view.mapper.NoteListMapper
import com.example.noteslist.presentation.notes_list.recycler_view.mapper.NoteStackStateManager
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel: NotesListViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repo = NotesRepositoryImpl(
                    dataSource = InMemoryNotesDataSource()
                )
                val stateManager = NoteStackStateManager()
                val mapper = NoteListMapper(
                    dateFormatter = RelativeDateFormatter(
                        this@MainActivity.applicationContext
                    ),
                    stateProvider = stateManager
                )

                return NotesListViewModel(repo, mapper, stateManager) as T
            }
        }
    }
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

    private fun collectData(){
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