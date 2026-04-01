package com.example.noteslist.presentation.notes_list.views.notes_stack.animation

import android.view.View
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import com.example.noteslist.presentation.notes_list.views.note.NoteView

class NoteStackItemAnimator(
    private val interpolator: Interpolator,
    private val durationCalculator: (Int) -> Long
) {
    fun animateCollapseButtonFadeOut(button: View, onEnd: () -> Unit) {
        button.visibility = View.GONE
        onEnd()
    }

    fun animateExpand(
        notes: List<NoteView>,
        collapseButton: View,
        initialTops: List<Int>,
        initialLefts: List<Int>,
        initialScale: Float,
        onEnd: () -> Unit
    ) {
        val n = notes.size
        val elementDuration = durationCalculator(n)
        var maxEndTime = 0L

        notes.forEachIndexed { i, note ->
            val reverseIndex = n - 1 - i
            val delay = reverseIndex * STAGGER_STEP_MS
            
            val currentTop = note.top
            val startTop = initialTops[i]
            val currentLeft = note.left
            val startLeft = initialLefts[i]

            val startZ = (n - reverseIndex).toFloat()
            note.translationZ = startZ
            note.translationY = (startTop - currentTop).toFloat()
            note.translationX = (startLeft - currentLeft).toFloat()
            
            note.pivotX = 0f
            note.scaleX = initialScale
            
            if (note.alpha != 1f) {
                note.alpha = 1f
            }
            
            note.animate()
                .translationY(0f)
                .translationX(0f)
                .scaleX(1.0f)
                .translationZ(0f)
                .setStartDelay(delay)
                .setDuration(elementDuration)
                .setInterpolator(interpolator)
                .start()
                
            val endTime = delay + elementDuration
            if (endTime > maxEndTime) {
                maxEndTime = endTime
            }
        }

        val buttonDelay = maxEndTime + BUTTON_APPEAR_DELAY_MS
        
        collapseButton.alpha = BUTTON_START_ALPHA
        collapseButton.scaleX = BUTTON_START_SCALE
        collapseButton.scaleY = BUTTON_START_SCALE
        collapseButton.visibility = View.VISIBLE
        
        collapseButton.animate()
            .alpha(BUTTON_END_ALPHA)
            .scaleX(BUTTON_END_SCALE)
            .scaleY(BUTTON_END_SCALE)
            .setStartDelay(buttonDelay)
            .setDuration(BUTTON_FADE_DURATION_MS)
            .setInterpolator(interpolator)
            .withEndAction {
                onEnd()
            }
            .start()
    }

    fun animateCollapse(
        notes: List<NoteView>,
        expandedTops: List<Int>,
        targetTops: List<Int>,
        targetLefts: List<Int>,
        targetScale: Float,
        onEnd: () -> Unit
    ) {
        val n = notes.size
        val elementDuration = durationCalculator(n)
        
        var maxEndTime = 0L

        notes.forEachIndexed { i, note ->
            val reverseIndex = n - 1 - i
            val delay = reverseIndex * STAGGER_STEP_MS
            
            val startExpandedTop = expandedTops[i]
            val endCollapsedTop = targetTops[i]
            val currentCollapsedTop = note.top
            val currentCollapsedLeft = note.left
            val endCollapsedLeft = targetLefts[i]
            
            note.visibility = View.VISIBLE
            
            val endZ = (n - reverseIndex).toFloat()
            note.translationZ = endZ
            note.translationY = (startExpandedTop - currentCollapsedTop).toFloat()
            note.translationX = 0f
            
            note.pivotX = 0f
            note.scaleX = 1f

            note.animate()
                .translationY((endCollapsedTop - currentCollapsedTop).toFloat())
                .translationX((endCollapsedLeft - currentCollapsedLeft).toFloat())
                .scaleX(targetScale)
                .setStartDelay(delay)
                .setDuration(elementDuration)
                .setInterpolator(interpolator)
                .withEndAction {
                    if (currentCollapsedTop != endCollapsedTop) {
                        note.visibility = View.GONE
                    }
                    if (i == 0) {
                        onEnd()
                    }
                }
                .start()
                
            val endTime = delay + elementDuration
            if (endTime > maxEndTime) {
                maxEndTime = endTime
            }
        }
    }

    companion object {
        private const val STAGGER_STEP_MS = 20L
        
        private const val BUTTON_APPEAR_DELAY_MS = 100L
        private const val BUTTON_FADE_DURATION_MS = 200L
        
        private const val BUTTON_START_ALPHA = 0f
        private const val BUTTON_END_ALPHA = 1f
        
        private const val BUTTON_START_SCALE = 0.7f
        private const val BUTTON_END_SCALE = 1.0f
    }
}
