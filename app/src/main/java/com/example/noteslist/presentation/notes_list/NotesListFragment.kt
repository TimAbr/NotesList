package com.example.noteslist.presentation.notes_list

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
        setupSearchBar(view)
        applyWindowInsets(view)
        collectData()
        observeViewEffects()
    }

    private fun observeViewEffects() {
        viewModel.viewEffect
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { effect ->
                when (effect) {
                    is NotesListViewModel.ViewEffect.SaveScroll -> {
                        viewModel.onScrollStateCaptured(
                            recyclerView.layoutManager?.onSaveInstanceState()
                        )
                    }

                    is NotesListViewModel.ViewEffect.ScrollToTop -> {
                        recyclerView.post {
                            recyclerView.scrollToPosition(0)
                        }
                    }
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun applyWindowInsets(view: View) {
        val searchPanelBackground = view.findViewById<View>(R.id.searchPanelBackground)
        val searchBarContainer = view.findViewById<View>(R.id.searchBarContainer)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val btnAddNote = view.findViewById<FloatingActionButton>(R.id.btnAddNote)

        ViewCompat.setOnApplyWindowInsetsListener(view) { _, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            val searchBarTopMargin = resources.getDimensionPixelSize(R.dimen.search_bar_top_margin)
            val searchBarHeight = resources.getDimensionPixelSize(R.dimen.search_bar_height)
            val searchBarBottomMargin =
                resources.getDimensionPixelSize(R.dimen.search_bar_bottom_margin)
            val fabMargin = resources.getDimensionPixelSize(R.dimen.fab_margin)

            searchPanelBackground.updateLayoutParams {
                height = insets.top + searchBarHeight + searchBarTopMargin + searchBarBottomMargin
            }
            searchBarContainer.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.top + searchBarTopMargin
            }

            recyclerView.updatePadding(
                top = insets.top
                        + searchBarTopMargin
                        + searchBarHeight
                        + searchBarBottomMargin
            )

            btnAddNote.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = insets.left
                bottomMargin = insets.bottom + fabMargin
                rightMargin = insets.right
            }

            WindowInsetsCompat.CONSUMED
        }
    }


    private fun setupSearchBar(view: View) {
        val searchEditText = view.findViewById<android.widget.EditText>(R.id.searchEditText)
        val btnClearSearch = view.findViewById<View>(R.id.btnClearSearch)

        searchEditText.addTextChangedListener { text ->
            val query = text?.toString() ?: ""
            viewModel.onSearchQueryChange(query)
            btnClearSearch.visibility = if (query.isEmpty()) View.GONE else View.VISIBLE
        }

        btnClearSearch.setOnClickListener {
            searchEditText.setText("")
        }
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
        viewModel.items
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { items ->
                adapter?.submitList(items) {
                    val state = viewModel.scrollState
                    if (state != null && items.isNotEmpty()) {
                        recyclerView.post {
                            recyclerView.layoutManager?.onRestoreInstanceState(state)
                            viewModel.scrollState = null
                        }
                    }
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onDestroyView() {

        viewModel.scrollState = recyclerView.layoutManager?.onSaveInstanceState()

        super.onDestroyView()

        adapter = null
        _recyclerView = null
    }

}
