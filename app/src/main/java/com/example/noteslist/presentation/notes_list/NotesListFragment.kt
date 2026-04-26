package com.example.noteslist.presentation.notes_list

import android.os.Bundle
import android.view.LayoutInflater
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
import com.example.noteslist.databinding.FragmentNotesListBinding
import com.example.noteslist.presentation.common.navigation.AppNavigator
import com.example.noteslist.presentation.note_details.NoteDetailsScreenMode
import com.example.noteslist.presentation.notes_list.recycler_view.NoteSpaceItemDecoration
import com.example.noteslist.presentation.notes_list.recycler_view.NotesAdapter
import com.example.noteslist.presentation.notes_list.recycler_view.delegates.DateHeaderDelegate
import com.example.noteslist.presentation.notes_list.recycler_view.delegates.NoteDelegate
import com.example.noteslist.presentation.notes_list.recycler_view.delegates.NoteStackDelegate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class NotesListFragment : Fragment() {

    private val viewModel: NotesListViewModel by activityViewModels()

    @Inject
    lateinit var navigator: AppNavigator

    private var adapter: NotesAdapter? = null

    private var _binding: FragmentNotesListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesListBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupAddNoteButton()
        setupSearchBar()
        applyWindowInsets()
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
                            binding.recyclerView.layoutManager?.onSaveInstanceState()
                        )
                    }

                    is NotesListViewModel.ViewEffect.ScrollToTop -> {
                        binding.recyclerView.post {
                            binding.recyclerView.scrollToPosition(0)
                        }
                    }
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            val barTop = resources.getDimensionPixelSize(R.dimen.search_bar_top_margin)
            val barHeight = resources.getDimensionPixelSize(R.dimen.search_bar_height)
            val barBottom = resources.getDimensionPixelSize(R.dimen.search_bar_bottom_margin)
            val fabMargin = resources.getDimensionPixelSize(R.dimen.fab_margin)

            val totalTopOffset = insets.top + barTop + barHeight + barBottom

            with(binding) {
                topBar.searchPanelBackground.updateLayoutParams {
                    height = totalTopOffset
                }

                topBar.searchRow.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    topMargin = insets.top + barTop
                }

                recyclerView.updatePadding(top = totalTopOffset)
                shimmerViewContainer.updatePadding(top = totalTopOffset)

                btnAddNote.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    leftMargin = insets.left
                    rightMargin = insets.right
                    bottomMargin = insets.bottom + fabMargin
                }
            }

            WindowInsetsCompat.CONSUMED
        }
    }

    private fun setupSearchBar() {
        with(binding.topBar.searchBar) {
            searchEditText.addTextChangedListener { text ->
                val query = text?.toString().orEmpty()
                viewModel.onSearchQueryChange(query)
                btnClearSearch.visibility = if (query.isEmpty()) View.GONE else View.VISIBLE
            }

            btnClearSearch.setOnClickListener {
                searchEditText.setText("")
            }
        }

        binding.topBar.btnSettings.setOnClickListener {
            navigator.navigateToSettings()
        }
    }

    private fun setupRecyclerView() {
        adapter = NotesAdapter(
            delegates = listOf(
                DateHeaderDelegate(),
                NoteDelegate(onNoteClick = ::onNoteClick),
                NoteStackDelegate(
                    onNoteClick = ::onNoteClick,
                    onStackClick = viewModel::onStackClick
                )
            )
        ).apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@NotesListFragment.adapter

            val spacing = resources.getDimensionPixelSize(R.dimen.note_list_spacing)
            addItemDecoration(NoteSpaceItemDecoration(spacing))

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val fab = binding.btnAddNote
                    if (dy > 0 && fab.isShown) {
                        fab.hide()
                    } else if (dy < 0 && !fab.isShown) {
                        fab.show()
                    }
                }
            })
        }
    }

    private fun setupAddNoteButton() {
        binding.btnAddNote.setOnClickListener {
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
        viewModel.state
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach(::handleUIState)
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleUIState(state: NotesListViewModel.NotesListUIState) {
        with(binding) {
            when (state) {
                is NotesListViewModel.NotesListUIState.Loading -> {
                    shimmerViewContainer.visibility = View.VISIBLE
                    shimmerViewContainer.startShimmer()
                    recyclerView.visibility = View.GONE
                    emptyStateContainer.visibility = View.GONE
                }

                is NotesListViewModel.NotesListUIState.Empty -> {
                    shimmerViewContainer.stopShimmer()
                    shimmerViewContainer.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    emptyStateContainer.visibility = View.VISIBLE
                }

                is NotesListViewModel.NotesListUIState.Content -> {
                    shimmerViewContainer.stopShimmer()
                    shimmerViewContainer.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    emptyStateContainer.visibility = View.GONE

                    adapter?.submitList(state.items) {
                        restoreScrollPosition(state.items)
                    }
                }
            }
        }
    }

    private fun restoreScrollPosition(items: List<Any>) {
        val savedScrollState = viewModel.scrollState
        if (savedScrollState != null && items.isNotEmpty()) {
            binding.recyclerView.post {
                binding.recyclerView.layoutManager?.onRestoreInstanceState(savedScrollState)
                viewModel.scrollState = null
            }
        }
    }

    override fun onDestroyView() {
        if (_binding != null) {
            viewModel.scrollState = binding.recyclerView.layoutManager?.onSaveInstanceState()
        }
        super.onDestroyView()
        _binding = null
        adapter = null
    }
}
