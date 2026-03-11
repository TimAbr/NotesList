package com.example.noteslist.presentation

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.noteslist.R
import com.example.noteslist.presentation.notes_list.views.note.NoteView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val container = findViewById<ViewGroup>(R.id.notesContainer)
        setupListeners(container)
    }

    private fun setupListeners(view: View) {
        when (view) {
            is NoteView -> {
                view.setOnClickListener {
                    view.data = view.data.copy(isRead = !view.data.isRead)
                }
            }
            is ViewGroup -> {
                for (i in 0 until view.childCount) {
                    setupListeners(view.getChildAt(i))
                }
            }
        }
    }
}