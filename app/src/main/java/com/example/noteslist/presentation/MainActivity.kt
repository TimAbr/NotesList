package com.example.noteslist.presentation

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.noteslist.R
import com.example.noteslist.presentation.notes_list.views.note.NoteView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        val container = findViewById<LinearLayout>(
            R.id.notesContainer
        )
        
        for (i in 0 until container.childCount) {
            val child = container.getChildAt(i)
            if (child is NoteView) {
                child.setOnClickListener {
                    child.data = child.data.copy(
                        isRead = !child.data.isRead
                    )
                }
            }
        }
    }
}