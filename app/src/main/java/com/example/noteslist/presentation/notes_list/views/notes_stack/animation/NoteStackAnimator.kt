package com.example.noteslist.presentation.notes_list.views.notes_stack.animation

import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.PathInterpolator
import com.example.noteslist.presentation.notes_list.views.ViewPaddings
import com.example.noteslist.presentation.notes_list.views.note.NoteView
import com.example.noteslist.presentation.notes_list.views.notes_stack.NoteStackView
import com.example.noteslist.presentation.notes_list.views.notes_stack.NoteStackViewConfig
import com.example.noteslist.presentation.notes_list.views.notes_stack.NoteStackViewLayoutManager
import com.example.noteslist.presentation.notes_list.views.notes_stack.NoteStackViewMeasurer

class NoteStackAnimator(
    private val view: NoteStackView,
    private val measurer: NoteStackViewMeasurer,
    private val layoutManager: NoteStackViewLayoutManager,
    private val config: NoteStackViewConfig,
    private val paddings: ViewPaddings
) {

    private val interpolator = PathInterpolator(X1, Y1, X2, Y2)
    private val sizeAnimator = NoteStackSizeAnimator(view, interpolator)
    private val itemAnimator = NoteStackItemAnimator(interpolator, layoutManager)

    fun expand(noteViews: List<NoteView>, emptyView: View, collapseButton: View) {
        if (noteViews.isEmpty()) return

        val timing = NoteStackAnimationTiming(noteViews.size)
        view.isExpanded = true

        animateContainerSize(noteViews, emptyView, collapseButton, true, timing)

        collapseButton.visibility = View.INVISIBLE

        view.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                view.viewTreeObserver.removeOnPreDrawListener(this)
                itemAnimator.animateExpand(
                    notes = noteViews,
                    collapseButton = collapseButton,
                    config = config,
                    paddings = paddings,
                    stackWidth = view.width,
                    timing = timing
                )
                return true
            }
        })
        view.requestLayout()
    }

    fun collapse(noteViews: List<NoteView>, emptyView: View, collapseButton: View) {
        if (noteViews.isEmpty()) return

        val timing = NoteStackAnimationTiming(noteViews.size)
        performCollapse(noteViews, emptyView, collapseButton, timing)
    }

    private fun performCollapse(
        noteViews: List<NoteView>,
        emptyView: View,
        collapseButton: View,
        timing: NoteStackAnimationTiming
    ) {
        val expandedTops = noteViews.map { it.top }

        val targetHeight = measureTargetHeight(noteViews, emptyView, collapseButton, false)

        val initialButtonTop = collapseButton.top
        val buttonHeight = collapseButton.measuredHeight

        sizeAnimator.animate(
            targetHeight = targetHeight,
            startDelayMs = 0L,
            durationMs = timing.totalStackDuration,
            onUpdate = { h ->
                collapseButton.translationY = (h - (initialButtonTop + buttonHeight)).toFloat()
            }
        )

        itemAnimator.animateCollapseButtonFadeOut(
            button = collapseButton,
            z = noteViews.size + 1f,
            duration = NoteStackAnimationTiming.BUTTON_ANIMATION_DURATION_MS
        )

        itemAnimator.animateCollapse(
            notes = noteViews,
            expandedTops = expandedTops,
            config = config,
            paddings = paddings,
            stackWidth = view.width,
            timing = timing,
            onEnd = {
                view.isExpanded = false
                noteViews.forEach {
                    it.translationY = 0f
                    it.translationX = 0f
                    it.scaleX = 1f
                    it.translationZ = 0f
                }
                view.requestLayout()
            })
    }


    private fun animateContainerSize(
        noteViews: List<NoteView>,
        emptyView: View,
        collapseButton: View,
        isExpanded: Boolean,
        timing: NoteStackAnimationTiming
    ) {
        val targetHeight = measureTargetHeight(noteViews, emptyView, collapseButton, isExpanded)
        sizeAnimator.animate(targetHeight, 0L, timing.totalStackDuration)
    }

    private fun measureTargetHeight(
        noteViews: List<NoteView>,
        emptyView: View,
        collapseButton: View,
        isExpanded: Boolean
    ): Int {
        return measurer.measure(
            notes = noteViews,
            emptyView = emptyView,
            collapseButton = collapseButton,
            config = config,
            isExpanded = isExpanded,
            widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                view.width, View.MeasureSpec.EXACTLY
            ),
            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            paddings = paddings
        ).height
    }

    companion object {
        private const val X1 = 0.4f
        private const val Y1 = 0.1f
        private const val X2 = 0.2f
        private const val Y2 = 1.0f
    }
}
