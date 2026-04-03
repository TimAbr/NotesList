package com.example.noteslist.presentation

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.noteslist.R
import com.example.noteslist.presentation.common.navigation.AppNavigator

class MainActivity : AppCompatActivity() {

    lateinit var navigator: AppNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigator = AppNavigator(this)
        navigator.setup()

        setupBackNavigation()
    }

    private fun setupBackNavigation() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigator.handleBackPress()
            }
        })
    }
}