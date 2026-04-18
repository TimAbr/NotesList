package com.example.noteslist.presentation.notes_list.views.notes_stack.animation

import android.view.View
import android.view.animation.Interpolator
import com.example.noteslist.presentation.notes_list.views.ViewPaddings
import com.example.noteslist.presentation.notes_list.views.note.NoteView
import com.example.noteslist.presentation.notes_list.views.notes_stack.NoteStackViewConfig
import com.example.noteslist.presentation.notes_list.views.notes_stack.NoteStackViewLayoutManager

class NoteStackItemAnimator(
    private val interpolator: Interpolator,
    private val layoutManager: NoteStackViewLayoutManager
) {

    fun animateExpand(
        notes: List<NoteView>,
        collapseButton: View,
        config: NoteStackViewConfig,
        paddings: ViewPaddings,
        stackWidth: Int,
        timing: NoteStackAnimationTiming
    ) {
        val n = notes.size
        val actualVisible = minOf(n, config.stackMaxVisible)
        val collapsedScale = layoutManager.calculateCollapsedScale(stackWidth, paddings, config, n)

        notes.forEachIndexed { i, note ->
            val reverseIndex = (n - 1) - i
            val delay = reverseIndex * timing.staggerDelay
            
            val initialTop = layoutManager.calculateCollapsedTop(reverseIndex, actualVisible, config.stackSpacing, paddings.paddingTop)
            val initialLeft = layoutManager.calculateCollapsedLeft(reverseIndex, actualVisible, config.stackSpacing, paddings.paddingLeft)
            
            setupExpandStart(note, initialTop, initialLeft, collapsedScale, n - reverseIndex)
            animateNoteToExpand(note, delay, timing.itemDuration)
        }

        animateCollapseButtonIn(collapseButton, timing.buttonStartDelay)
    }

    fun animateCollapse(
        notes: List<NoteView>,
        expandedTops: List<Int>,
        config: NoteStackViewConfig,
        paddings: ViewPaddings,
        stackWidth: Int,
        timing: NoteStackAnimationTiming,
        onEnd: () -> Unit
    ) {
        val n = notes.size
        val actualVisible = minOf(n, config.stackMaxVisible)
        val targetScale = layoutManager.calculateCollapsedScale(stackWidth, paddings, config, n)

        notes.forEachIndexed { i, note ->
            val reverseIndex = (n - 1) - i
            val delay = reverseIndex * timing.staggerDelay
            val isHiddenInStack = reverseIndex >= actualVisible
            
            val targetTop = layoutManager.calculateCollapsedTop(reverseIndex, actualVisible, config.stackSpacing, paddings.paddingTop)
            val targetLeft = layoutManager.calculateCollapsedLeft(reverseIndex, actualVisible, config.stackSpacing, paddings.paddingLeft)

            setupCollapseStart(note, expandedTops[i])
            animateNoteToCollapse(
                note,
                targetTop,
                targetLeft,
                targetScale,
                delay,
                timing.itemDuration,
                n - reverseIndex,
                isHiddenInStack,
                i == 0,
                onEnd
            )
        }
    }

    fun animateCollapseButtonFadeOut(button: View, z: Float, duration: Long) {
        button.translationZ = z
        button.animate()
            .alpha(0f)
            .scaleX(0.7f)
            .scaleY(0.7f)
            .setDuration(duration)
            .setStartDelay(0L)
            .setInterpolator(interpolator)
            .withEndAction {
                button.visibility = View.GONE
                button.translationY = 0f
                button.translationZ = 0f
            }
            .start()
    }

    private fun setupExpandStart(note: NoteView, initialTop: Int, initialLeft: Int, scale: Float, z: Int) {
        note.visibility = View.VISIBLE
        note.translationY = (initialTop - note.top).toFloat()
        note.translationX = (initialLeft - note.left).toFloat()
        note.scaleX = scale
        note.pivotX = 0f
        note.translationZ = z.toFloat()
        note.alpha = 1f
    }

    private fun animateNoteToExpand(note: NoteView, delay: Long, duration: Long) {
        note.animate()
            .translationY(0f)
            .translationX(0f)
            .scaleX(1.0f)
            .alpha(1f)
            .translationZ(0f)
            .setStartDelay(delay)
            .setDuration(duration)
            .setInterpolator(interpolator)
            .start()
    }

    private fun setupCollapseStart(note: NoteView, expandedTop: Int) {
        note.visibility = View.VISIBLE
        note.translationY = (expandedTop - note.top).toFloat()
        note.translationX = 0f
        note.scaleX = 1f
        note.pivotX = 0f
        note.alpha = 1f
    }

    private fun animateNoteToCollapse(
        note: NoteView,
        targetTop: Int,
        targetLeft: Int,
        targetScale: Float,
        delay: Long,
        duration: Long,
        z: Int,
        isHidden: Boolean,
        isLast: Boolean,
        onEnd: () -> Unit
    ) {
        note.translationZ = z.toFloat()
        note.animate()
            .translationY((targetTop - note.top).toFloat())
            .translationX((targetLeft - note.left).toFloat())
            .scaleX(targetScale)
            .alpha(1f)
            .setStartDelay(delay)
            .setDuration(duration)
            .setInterpolator(interpolator)
            .withEndAction {
                if (isHidden) note.visibility = View.GONE
                if (isLast) onEnd()
            }
            .start()
    }

    private fun animateCollapseButtonIn(button: View, delay: Long) {
        button.alpha = 0f
        button.scaleX = 0.7f
        button.scaleY = 0.7f
        button.translationY = 0f
        button.visibility = View.VISIBLE
        button.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setStartDelay(delay)
            .setDuration(NoteStackAnimationTiming.BUTTON_ANIMATION_DURATION_MS)
            .setInterpolator(interpolator)
            .start()
    }
}
