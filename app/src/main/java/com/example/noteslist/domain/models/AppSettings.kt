package com.example.noteslist.domain.models

data class AppSettings(
    val stackSpacing: Int,
    val stackMaxVisible: Int,
) {
    companion object {
        const val DEFAULT_STACK_SPACING = 16
        const val DEFAULT_STACK_MAX_VISIBLE = 3
        
        fun default() = AppSettings(
            stackSpacing = DEFAULT_STACK_SPACING,
            stackMaxVisible = DEFAULT_STACK_MAX_VISIBLE,
        )
    }
}
