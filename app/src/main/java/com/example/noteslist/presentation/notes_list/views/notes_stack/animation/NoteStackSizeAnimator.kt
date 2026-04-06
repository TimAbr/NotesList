package com.example.noteslist.presentation.notes_list.views.notes_stack.animation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import android.view.animation.Interpolator

class NoteStackSizeAnimator(private val view: View, private val interpolator: Interpolator) {
    private var boundsAnimator: ValueAnimator? = null

    fun animate(targetHeight: Int, startDelayMs: Long, durationMs: Long, onUpdate: ((Int) -> Unit)? = null) {
        boundsAnimator?.cancel()
        val startHeight = view.height
        
        val lp = view.layoutParams
        if (lp != null) {
            lp.height = startHeight
            view.layoutParams = lp
        }
        
        boundsAnimator = ValueAnimator.ofInt(startHeight, targetHeight).apply {
            startDelay = startDelayMs
            duration = durationMs
            this.interpolator = this@NoteStackSizeAnimator.interpolator
            addUpdateListener { anim ->
                val h = anim.animatedValue as Int
                val currentLp = view.layoutParams
                if (currentLp != null) {
                    currentLp.height = h
                    view.layoutParams = currentLp
                }
                onUpdate?.invoke(h)
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    val currentLp = view.layoutParams
                    if (currentLp != null) {
                        currentLp.height = ViewGroup.LayoutParams.WRAP_CONTENT
                        view.layoutParams = currentLp
                    }
                }
            })
            start()
        }
    }
}
