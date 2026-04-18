package com.example.noteslist.presentation.notes_list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.R
import com.example.noteslist.presentation.common.navigation.AppNavigator
import com.example.noteslist.presentation.note_details.NoteDetailsScreenMode
import com.example.noteslist.presentation.notes_list.recycler_view.NoteSpaceItemDecoration
import com.example.noteslist.presentation.notes_list.recycler_view.NotesAdapter
import com.example.noteslist.presentation.notes_list.recycler_view.delegates.DateHeaderDelegate
import com.example.noteslist.presentation.notes_list.recycler_view.delegates.NoteDelegate
import com.example.noteslist.presentation.notes_list.recycler_view.delegates.NoteStackDelegate
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotesListFragment : Fragment(R.layout.fragment_notes_list) {

    private val viewModel: NotesListViewModel by activityViewModels()

    @Inject
    lateinit var navigator: AppNavigator

    private var adapter: NotesAdapter? = null

    private var _recyclerView: RecyclerView? = null
    private val recyclerView: RecyclerView
        get() = _recyclerView!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView(view)
        setupAddNoteButton(view)
        collectData()
    }

    private fun setupRecyclerView(view: View) {
        _recyclerView = view.findViewById(R.id.recyclerView)

        val layoutManager = LinearLayoutManager(requireContext())

        adapter = NotesAdapter(
            listOf(
                DateHeaderDelegate(),
                NoteDelegate { onNoteClick(it) },
                NoteStackDelegate(
                    onNoteClick = { onNoteClick(it) },
                    onStackClick = { viewModel.onStackClick(it) }
                )
            )
        ).apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

        recyclerView.layoutManager = layoutManager

        val spacing = resources.getDimensionPixelSize(R.dimen.note_list_spacing)
        recyclerView.addItemDecoration(NoteSpaceItemDecoration(spacing))
        recyclerView.adapter = adapter

        val btnAddNote = view.findViewById<FloatingActionButton>(R.id.btnAddNote)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 && btnAddNote.isShown) {
                    btnAddNote.hide()
                } else if (dy < 0 && !btnAddNote.isShown) {
                    btnAddNote.show()
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
        navigator.navigateToNoteDetails(NoteDetailsScreenMode.Edit(id))
    }

    private fun onAddNoteClick() {
        navigator.navigateToNoteDetails(NoteDetailsScreenMode.Create)
    }

    private fun collectData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.items.collect {items ->
                    adapter?.submitList(items){
                        val state = viewModel.scrollState
                        if (state != null && items.isNotEmpty()) {

                            recyclerView.post {
                                recyclerView.layoutManager?.onRestoreInstanceState(state)
                                viewModel.scrollState = null
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {

        viewModel.scrollState = recyclerView.layoutManager?.onSaveInstanceState()

        super.onDestroyView()

        adapter = null
        _recyclerView = null
    }

}
