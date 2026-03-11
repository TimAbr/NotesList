package com.example.noteslist.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.R
import com.example.noteslist.data.repositories.NotesRepositoryImpl
import com.example.noteslist.domain.models.Note
import com.example.noteslist.presentation.notes_list.recycler_view.NoteListItem
import com.example.noteslist.presentation.notes_list.recycler_view.NoteListMapper
import com.example.noteslist.presentation.notes_list.recycler_view.NotesAdapter
import com.example.noteslist.presentation.notes_list.recycler_view.delegates.DateHeaderDelegate
import com.example.noteslist.presentation.notes_list.recycler_view.delegates.NoteDelegate
import com.example.noteslist.presentation.notes_list.recycler_view.NoteSpaceItemDecoration
import com.example.noteslist.presentation.notes_list.recycler_view.RelativeDateFormatter
import com.example.noteslist.presentation.notes_list.recycler_view.delegates.NoteStackDelegate
import java.time.LocalDate

class MainActivity : AppCompatActivity() {

    private val repository = NotesRepositoryImpl()
    private val mapper = NoteListMapper(RelativeDateFormatter(this))
    private val expandedDateStacks = mutableSetOf<LocalDate>()

    private var cachedNotes: List<Note> = emptyList()

    private fun onNoteClick(clickedNote: Note){
        val updatedNote = clickedNote.copy(isRead = !clickedNote.isRead)
        repository.updateNote(updatedNote)
        loadData()
    }

    private fun onStackClick(date: LocalDate){
        if (expandedDateStacks.contains(date)){
            expandedDateStacks.remove(date)
        } else {
            expandedDateStacks.add(date)
        }
        updateUI()
    }

    private fun updateUI(){
        val items = mapper.mapToAdapterItems(cachedNotes, expandedDateStacks)

        val currentDates = items.filterIsInstance<NoteListItem.NoteStack>().map { it.date }.toSet()
        expandedDateStacks.retainAll(currentDates)

        adapter.submitList(items)
    }

    private val adapter = NotesAdapter(
            listOf(
                DateHeaderDelegate(),
                NoteDelegate(::onNoteClick),
                NoteStackDelegate(::onNoteClick, ::onStackClick)
            )
        )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupRecyclerView()
        loadData()
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        val spacing = resources.getDimensionPixelSize(R.dimen.note_list_spacing)
        recyclerView.addItemDecoration(NoteSpaceItemDecoration(spacing))

        recyclerView.adapter = adapter
    }

    private fun loadData() {
        cachedNotes = repository.getAllNotes()
        updateUI()
    }
}