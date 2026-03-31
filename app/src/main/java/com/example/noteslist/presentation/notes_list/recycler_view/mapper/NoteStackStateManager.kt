package com.example.noteslist.presentation.notes_list.recycler_view.mapper

import java.time.LocalDate

class NoteStackStateManager : NoteStackStateProvider {
    private val expandedKeys = mutableSetOf<NoteStackKey>()

    override fun isExpanded(key: NoteStackKey): Boolean = expandedKeys.contains(key)

    fun toggle(key: NoteStackKey) {
        if (expandedKeys.contains(key)) {
            expandedKeys.remove(key)
        } else {
            expandedKeys.add(key)
        }
    }

    fun keepOnly(activeDates: Set<LocalDate>) {
        val activeKeys = activeDates.map { NoteStackKey(it) }.toSet()
        expandedKeys.retainAll(activeKeys)
    }
}