package com.example.noteslist.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.R
import com.example.noteslist.data.repositories.NotesRepositoryImpl
import com.example.noteslist.presentation.notes_list.recycler_view.NoteListMapper
import com.example.noteslist.presentation.notes_list.recycler_view.NotesAdapter
import com.example.noteslist.presentation.notes_list.recycler_view.delegates.DateHeaderDelegate
import com.example.noteslist.presentation.notes_list.recycler_view.delegates.NoteDelegate
import com.example.noteslist.presentation.notes_list.recycler_view.delegates.NoteStackDelegate

class MainActivity : AppCompatActivity() {

    private val repository = NotesRepositoryImpl()
    private val mapper = NoteListMapper()
    
    private val adapter by lazy {
        NotesAdapter(
            listOf(
                DateHeaderDelegate(),
                NoteDelegate(),
                NoteStackDelegate()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupRecyclerView()
        loadData()
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun loadData() {
        val notes = repository.getAllNotes()
        val items = mapper.mapToAdapterItems(notes)
        adapter.submitList(items)
    }
}