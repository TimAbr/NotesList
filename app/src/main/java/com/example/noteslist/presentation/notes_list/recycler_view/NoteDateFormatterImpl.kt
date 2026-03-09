package com.example.noteslist.presentation.notes_list.recycler_view

import android.content.Context
import com.example.noteslist.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class NoteDateFormatterImpl(private val context: Context) : NoteDateFormatter {

    private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    override fun format(date: LocalDate): String {
        val today = LocalDate.now()
        return when (ChronoUnit.DAYS.between(today, date)) {
            0L -> context.getString(R.string.date_today)
            -1L -> context.getString(R.string.date_yesterday)
            1L -> context.getString(R.string.date_tomorrow)
            else -> date.format(dateFormatter)
        }
    }
}
