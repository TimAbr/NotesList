package com.example.noteslist.presentation.common.navigation

import android.content.Context
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.transition.TransitionManager
import com.example.noteslist.R
import com.example.noteslist.presentation.MainActivity
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class NavigationDisplayController @Inject constructor(
    @ActivityContext private val context: Context
) {
    private val activity = context as MainActivity
    private val isMultiPane: Boolean
        get() = activity.resources.getBoolean(R.bool.is_multi_pane)

    fun showDetails() {
        if (isMultiPane) animateTo(OPEN_PERCENT)
    }

    fun hideDetails() {
        if (isMultiPane) animateTo(CLOSED_PERCENT)
    }

    fun syncState(isDetailsOpen: Boolean) {
        if (!isMultiPane) return
        setPercent(if (isDetailsOpen) OPEN_PERCENT else CLOSED_PERCENT)
    }

    private fun animateTo(percent: Float) {
        val root = activity.findViewById<ViewGroup>(R.id.main_root) ?: return
        TransitionManager.beginDelayedTransition(root)
        setPercent(percent)
    }

    private fun setPercent(percent: Float) {
        val guideline = activity.findViewById<Guideline>(R.id.guideline) ?: return
        val params = guideline.layoutParams as ConstraintLayout.LayoutParams
        params.guidePercent = percent
        guideline.layoutParams = params
    }

    companion object {
        private const val OPEN_PERCENT = 0.5f
        private const val CLOSED_PERCENT = 1.0f
    }
}
