package com.example.noteslist.presentation.notes_list.views.notes_stack.animation

class NoteStackAnimationTiming(noteCount: Int) {
    val staggerDelay: Long
    val itemDuration: Long
    val totalStackDuration: Long
    val buttonStartDelay: Long

    init {
        val n = noteCount.coerceAtLeast(1)
        val targetTotalWindow = (BASE_DURATION_MS + n * STEP_DURATION_MS)
            .coerceAtMost(MAX_DURATION_MS)
        
        staggerDelay = calculateStagger(n, targetTotalWindow)
        val lastItemStartDelay = (n - 1) * staggerDelay
        
        itemDuration = (targetTotalWindow - lastItemStartDelay).coerceAtLeast(BASE_DURATION_MS)
        totalStackDuration = lastItemStartDelay + itemDuration
        buttonStartDelay = totalStackDuration + BUTTON_APPEAR_PAUSE_MS
    }

    private fun calculateStagger(n: Int, targetTotalWindow: Long): Long {
        if (n <= 1) return 0L
        val maxStaggerBudget = targetTotalWindow - BASE_DURATION_MS
        val potentialTotalStagger = (n - 1) * STAGGER_STEP_MS
        
        return if (potentialTotalStagger <= maxStaggerBudget) {
            STAGGER_STEP_MS
        } else {
            (MAX_DURATION_MS - BASE_DURATION_MS) / n / 2
        }
    }

    companion object {
        const val BASE_DURATION_MS = 200L
        const val STEP_DURATION_MS = 40L
        const val MAX_DURATION_MS = 800L
        const val STAGGER_STEP_MS = 20L
        const val BUTTON_APPEAR_PAUSE_MS = 100L
        const val BUTTON_ANIMATION_DURATION_MS = 200L
    }
}
