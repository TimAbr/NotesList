package com.example.noteslist.presentation.notes_list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.R
import com.example.noteslist.data.datasources.notes.local.InMemoryNotesDataSource
import com.example.noteslist.data.repositories.NotesRepositoryImpl
import com.example.noteslist.presentation.MainActivity
import com.example.noteslist.presentation.common.date_formatter.RelativeDateFormatter
import com.example.noteslist.presentation.note_details.NoteDetailsScreenMode
import com.example.noteslist.presentation.notes_list.recycler_view.NoteSpaceItemDecoration
import com.example.noteslist.presentation.notes_list.recycler_view.NotesAdapter
import com.example.noteslist.presentation.notes_list.recycler_view.delegates.DateHeaderDelegate
import com.example.noteslist.presentation.notes_list.recycler_view.delegates.NoteDelegate
import com.example.noteslist.presentation.notes_list.recycler_view.delegates.NoteStackDelegate
import com.example.noteslist.presentation.notes_list.recycler_view.mapper.NoteListMapper
import com.example.noteslist.presentation.notes_list.recycler_view.mapper.NoteStackStateManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import kotlin.getValue


class NotesListFragment : Fragment(R.layout.fragment_notes_list) {

    private val viewModel: NotesListViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repo = NotesRepositoryImpl(
                    dataSource = InMemoryNotesDataSource()
                )
                val stateManager = NoteStackStateManager()
                val mapper = NoteListMapper(
                    dateFormatter = RelativeDateFormatter(
                        requireContext().applicationContext
                    ),
                    stateProvider = stateManager
                )

                return NotesListViewModel(repo, mapper, stateManager) as T
            }
        }
    }

    private var adapter: NotesAdapter? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView(view)
        setupAddNoteButton(view)
        collectData()
    }

    private fun setupRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        adapter = NotesAdapter(
            listOf(
                DateHeaderDelegate(),
                NoteDelegate { onNoteClick(it) },
                NoteStackDelegate(
                    onNoteClick = { onNoteClick(it) },
                    onStackClick = { viewModel.onStackClick(it) }
                )
            )
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val spacing = resources.getDimensionPixelSize(R.dimen.note_list_spacing)
        recyclerView.addItemDecoration(NoteSpaceItemDecoration(spacing))
        recyclerView.adapter = adapter

        val fab = view.findViewById<FloatingActionButton>(R.id.btnAddNote)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 && fab.isShown) {
                    fab.hide()
                } else if (dy < 0 && !fab.isShown) {
                    fab.show()
                }
            }
        })
    }

    private fun setupAddNoteButton(view: View) {
        view.findViewById<FloatingActionButton>(R.id.btnAddNote).setOnClickListener {
            onAddNoteClick()
        }
    }

    private fun onNoteClick(id: Long) {
        val navigator = (activity as? MainActivity)?.navigator
        navigator?.navigateToNoteDetails(NoteDetailsScreenMode.Edit(id))
    }

    private fun onAddNoteClick() {
        val navigator = (activity as? MainActivity)?.navigator
        navigator?.navigateToNoteDetails(NoteDetailsScreenMode.Create)
    }

    private fun collectData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.items.collect {
                    adapter?.submitList(it)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }

}