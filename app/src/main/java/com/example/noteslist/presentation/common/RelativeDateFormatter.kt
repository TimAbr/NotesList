package com.example.noteslist.presentation.common

import android.content.Context
import com.example.noteslist.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class RelativeDateFormatter(
    private val context: Context,
    private val dateFormat: String = DEFAULT_DATE_FORMAT
) : NoteDateFormatter {

    private val dateFormatter = DateTimeFormatter.ofPattern(dateFormat)

    override fun format(instant: Instant): String {
        val targetDate = instant.atZone(SYSTEM_ZONE).toLocalDate()
        val today = LocalDate.now(SYSTEM_ZONE)

        return when (ChronoUnit.DAYS.between(today, targetDate)) {
            0L -> context.getString(R.string.date_today)
            -1L -> context.getString(R.string.date_yesterday)
            1L -> context.getString(R.string.date_tomorrow)
            else -> targetDate.format(dateFormatter)
        }
    }

    override fun parse(dateString: String): Instant {
        return try{
            LocalDate.parse(dateString, dateFormatter)
            .atStartOfDay(SYSTEM_ZONE)
            .toInstant()
        } catch (e: Exception){
            throw IllegalArgumentException(
                "Invalid date format: $dateString. Expected: $dateFormat",
                e
            )
        }
    }

    companion object {
        private const val DEFAULT_DATE_FORMAT = "dd.MM.yyyy"
        private val SYSTEM_ZONE = ZoneId.systemDefault()
    }
}