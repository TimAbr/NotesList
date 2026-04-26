package com.example.noteslist.presentation.notes_list.recycler_view.mapper

import com.example.noteslist.domain.models.AppSettings
import com.example.noteslist.domain.models.Note
import com.example.noteslist.presentation.common.date_formatter.NoteDateFormatter
import com.example.noteslist.presentation.di.RelativeDate
import com.example.noteslist.presentation.notes_list.recycler_view.NoteListItem
import java.time.ZoneId
import javax.inject.Inject

class NoteListMapper @Inject constructor(
    @RelativeDate private val dateFormatter: NoteDateFormatter,
    private val stateProvider: NoteStackStateProvider
) {
    fun mapToAdapterItems(
        notes: List<Note>,
        settings: AppSettings
    ): List<NoteListItem> {
        val result = mutableListOf<NoteListItem>()

        val groupedByDate = notes.groupBy {
            it.timestamp.atZone(SYSTEM_ZONE).toLocalDate()
        }.toSortedMap(compareByDescending { it })

        groupedByDate.forEach { (date, notesInDate) ->
            result.add(
                NoteListItem.DateHeader(
                    dateFormatter.format(
                            date.atStartOfDay(
                                SYSTEM_ZONE
                            ).toInstant()
                        )
                )
            )

            val (importantNotes, regularNotes) = notesInDate.partition { it.isImportant }

            importantNotes
                .sortedByDescending { it.timestamp }
                .forEach {
                    result.add(NoteListItem.SingleNote(it))
                }

            if (regularNotes.isNotEmpty()) {
                result.add(
                    NoteListItem.NoteStack(
                        regularNotes,
                        stateProvider.isExpanded(
                            NoteStackKey(date)
                        ),
                        NoteStackKey(date),
                        stackSpacing = settings.stackSpacing,
                        stackMaxVisible = settings.stackMaxVisible
                    )
                )
            }
        }

        return result
    }

    companion object{
        private val SYSTEM_ZONE = ZoneId.systemDefault()
    }
}
