package com.example.noteslist.presentation.common.navigation

import android.content.Context
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import androidx.transition.TransitionManager
import com.example.noteslist.NavGraphDirections
import com.example.noteslist.R
import com.example.noteslist.presentation.MainActivity
import com.example.noteslist.presentation.note_details.NoteDetailsScreenMode
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class AppNavigator @Inject constructor(
    @ActivityContext private val context: Context
) {
    private val activity = context as MainActivity
    private val navController: NavController by lazy {
        val navHostFragment = activity.supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navHostFragment.navController
    }

    private val isMultiPane: Boolean
        get() = activity.resources.getBoolean(R.bool.is_multi_pane)

    fun setup() {
        activity.window.decorView.post {
            syncRotationState()
            
            if (isMultiPane && isDestination(R.id.notesListFragment)) {
                navigateToRoot()
            }

            if (!isMultiPane && isDestination(R.id.emptyFragment)) {
                navigateToRoot()
            }
        }
    }

    fun navigateToNoteDetails(mode: NoteDetailsScreenMode) {
        if (isMultiPane) {
            animateGuideline(0.5f)
        }

        val action = NavGraphDirections.actionGlobalNoteDetailsFragment(mode)
        navController.navigate(
            action,
            navOptions {
                popUpTo(R.id.noteDetailsFragment) { inclusive = true }
                launchSingleTop = true
            }
        )
    }

    fun handleBackPress(isDataChanged: Boolean = false) {
        when {
            isDetailsOpen() -> handleDetailsBack(isDataChanged)
            isAtRoot() -> showExitConfirmationDialog()
            else -> if (!navController.popBackStack()) showExitConfirmationDialog()
        }
    }

    fun closeDetails() {
        if (isMultiPane) {
            animateGuideline(1.0f)
        }
        navigateToRoot()
    }

    fun syncRotationState() {
        if (!isMultiPane) return
        setGuidelinePercent(if (isDetailsOpen()) 0.5f else 1.0f)
    }

    private fun handleDetailsBack(isDataChanged: Boolean) {
        if (isDataChanged) {
            showDataLossDialog()
        } else {
            closeDetails()
        }
    }

    private fun navigateToRoot() {
        val rootId = if (isMultiPane) R.id.emptyFragment else R.id.notesListFragment
        if (!navController.popBackStack(rootId, false)) {
            navController.navigate(rootId, null, navOptions {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            })
        }
    }

    private fun isDetailsOpen(): Boolean = isDestination(R.id.noteDetailsFragment)

    private fun isAtRoot(): Boolean {
        val currentDest = navController.currentDestination?.id
        return currentDest == null || 
               (isMultiPane && currentDest == R.id.emptyFragment) || 
               (!isMultiPane && currentDest == R.id.notesListFragment)
    }

    private fun isDestination(id: Int): Boolean = navController.currentDestination?.id == id

    private fun animateGuideline(percent: Float) {
        val root = activity.findViewById<ViewGroup>(R.id.main_root) ?: return
        TransitionManager.beginDelayedTransition(root)
        setGuidelinePercent(percent)
    }

    private fun setGuidelinePercent(percent: Float) {
        val guideline = activity.findViewById<Guideline>(R.id.guideline) ?: return
        val params = guideline.layoutParams as ConstraintLayout.LayoutParams
        params.guidePercent = percent
        guideline.layoutParams = params
    }

    private fun showDataLossDialog() {
        AlertDialog.Builder(activity)
            .setTitle(R.string.lose_data_dialog_title)
            .setMessage(R.string.lose_data_dialog_message)
            .setPositiveButton(R.string.dialog_yes) { _, _ -> closeDetails() }
            .setNegativeButton(R.string.dialog_no, null)
            .show()
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(activity)
            .setTitle(R.string.exit_dialog_title)
            .setMessage(R.string.exit_dialog_message)
            .setPositiveButton(R.string.dialog_yes) { _, _ -> activity.finish() }
            .setNegativeButton(R.string.dialog_no, null)
            .show()
    }
}
