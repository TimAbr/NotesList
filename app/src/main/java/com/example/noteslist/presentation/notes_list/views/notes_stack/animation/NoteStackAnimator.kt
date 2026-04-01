package com.example.noteslist.presentation.notes_list.views.notes_stack.animation

import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.PathInterpolator
import com.example.noteslist.presentation.notes_list.views.ViewPaddings
import com.example.noteslist.presentation.notes_list.views.note.NoteView
import com.example.noteslist.presentation.notes_list.views.notes_stack.NoteStackView
import com.example.noteslist.presentation.notes_list.views.notes_stack.NoteStackViewConfig
import com.example.noteslist.presentation.notes_list.views.notes_stack.NoteStackViewMeasurer

class NoteStackAnimator(
    private val view: NoteStackView,
    private val measurer: NoteStackViewMeasurer,
    private val config: NoteStackViewConfig,
    private val paddings: ViewPaddings
) {

    private val interpolator = PathInterpolator(
        INTERPOLATOR_X1,
        INTERPOLATOR_Y1,
        INTERPOLATOR_X2,
        INTERPOLATOR_Y2
    )
    private val sizeAnimator = NoteStackSizeAnimator(view, interpolator)
    private val itemAnimator = NoteStackItemAnimator(interpolator, ::calculateDuration)

    fun expand(noteViews: List<NoteView>, emptyView: View, collapseButton: View) {
        val n = noteViews.size
        val actualVisible = minOf(n, config.stackMaxVisible)
        val initialTops = noteViews.mapIndexed { i, note ->
            val reverseIndex = n - 1 - i
            if (reverseIndex < actualVisible) {
                note.top
            } else {
                paddings.paddingTop
            }
        }
        val initialLefts = noteViews.mapIndexed { i, note ->
            val reverseIndex = n - 1 - i
            if (reverseIndex < actualVisible) {
                note.left
            } else {
                paddings.paddingLeft
            }
        }
        
        val expandedWidth = (view.width - paddings.paddingLeft - paddings.paddingRight).coerceAtLeast(1)
        val maxOffset = (actualVisible - 1) * config.stackSpacing
        val collapsedWidth = (expandedWidth - maxOffset).coerceAtLeast(1)
        val collapsedScale = collapsedWidth.toFloat() / expandedWidth.toFloat()
        
        view.isExpanded = true

        val durationMs = calculateDuration(n) + (n - 1) * STAGGER_MULTIPLIER_MS
        
        val targetMeasure = measurer.measure(
            notes = noteViews,
            emptyView = emptyView,
            collapseButton = collapseButton,
            config = config,
            isExpanded = true,
            widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY),
            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            paddings = paddings
        )
        
        sizeAnimator.animate(targetMeasure.height, 0L, durationMs)
        
        collapseButton.visibility = View.INVISIBLE

        view.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                view.viewTreeObserver.removeOnPreDrawListener(this)
                itemAnimator.animateExpand(
                    notes = noteViews,
                    collapseButton = collapseButton,
                    initialTops = initialTops,
                    initialLefts = initialLefts,
                    initialScale = collapsedScale,
                    onEnd = {}
                )
                return true
            }
        })
        view.requestLayout()
    }

    fun collapse(noteViews: List<NoteView>, emptyView: View, collapseButton: View) {
        itemAnimator.animateCollapseButtonFadeOut(collapseButton) {
            performCollapseStateChange(noteViews, emptyView, collapseButton)
        }
    }

    private fun performCollapseStateChange(noteViews: List<NoteView>, emptyView: View, collapseButton: View) {
        val expandedTops = noteViews.map { it.top }

        val n = noteViews.size
        val actualVisible = minOf(n, config.stackMaxVisible)
        val targetTops = noteViews.mapIndexed { i, _ ->
            val reverseIndex = n - 1 - i
            if (reverseIndex < actualVisible) {
                val visualIndex = (actualVisible - 1) - reverseIndex
                val offset = visualIndex * config.stackSpacing
                paddings.paddingTop + offset
            } else {
                paddings.paddingTop
            }
        }
        val targetLefts = noteViews.mapIndexed { i, _ ->
            val reverseIndex = n - 1 - i
            if (reverseIndex < actualVisible) {
                val visualIndex = (actualVisible - 1) - reverseIndex
                val offset = visualIndex * config.stackSpacing
                paddings.paddingLeft + offset
            } else {
                paddings.paddingLeft
            }
        }

        val expandedWidth = (view.width - paddings.paddingLeft - paddings.paddingRight).coerceAtLeast(1)
        val maxOffset = (actualVisible - 1) * config.stackSpacing
        val collapsedWidth = (expandedWidth - maxOffset).coerceAtLeast(1)
        val collapsedScale = collapsedWidth.toFloat() / expandedWidth.toFloat()

        view.isExpanded = false

        val durationMs = calculateDuration(n) + (n - 1) * STAGGER_MULTIPLIER_MS
        
        val targetMeasure = measurer.measure(
            notes = noteViews,
            emptyView = emptyView,
            collapseButton = collapseButton,
            config = config,
            isExpanded = false,
            widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY),
            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            paddings = paddings
        )
        
        sizeAnimator.animate(targetMeasure.height, 0L, durationMs)
        
        view.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                view.viewTreeObserver.removeOnPreDrawListener(this)
                itemAnimator.animateCollapse(
                    notes = noteViews,
                    expandedTops = expandedTops,
                    targetTops = targetTops,
                    targetLefts = targetLefts,
                    targetScale = collapsedScale,
                    onEnd = { 
                        view.requestLayout()
                    }
                )
                return true
            }
        })
        view.requestLayout()
    }

    private fun calculateDuration(noteCount: Int): Long {
        return minOf(MAX_ANIMATION_DURATION_MS, BASE_ANIMATION_DURATION_MS + noteCount * ITEM_DURATION_INCREMENT_MS)
    }

    companion object {
        private const val INTERPOLATOR_X1 = 0.4f
        private const val INTERPOLATOR_Y1 = 0.1f
        private const val INTERPOLATOR_X2 = 0.2f
        private const val INTERPOLATOR_Y2 = 1.0f

        private const val STAGGER_MULTIPLIER_MS = 20L
        
        private const val BASE_ANIMATION_DURATION_MS = 200L
        private const val ITEM_DURATION_INCREMENT_MS = 40L
        private const val MAX_ANIMATION_DURATION_MS = 800L
    }
}
