package com.example.noteslist.domain.usecases

import com.example.noteslist.domain.models.Note
import com.example.noteslist.domain.models.search.SearchMatcher
import javax.inject.Inject

class SearchNotesUseCase @Inject constructor(
    private val matcher: SearchMatcher
) {
    operator fun invoke(query: String, notes: List<Note>): List<Note> {
        if (query.isBlank()) return notes

        return notes.asSequence()
            .map { it to matcher.match(query, it.title) }
            .filter { it.second >= SearchMatcher.MIN_SIMILARITY_SCORE }
            .sortedByDescending { it.second }
            .map { it.first }
            .toList()
    }
}
