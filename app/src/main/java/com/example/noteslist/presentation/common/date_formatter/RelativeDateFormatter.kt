package com.example.noteslist.presentation.common.date_formatter

import android.content.Context
import com.example.noteslist.R
import com.example.noteslist.presentation.di.DatePattern
import com.example.noteslist.presentation.di.RelativeDate
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import it.czerwinski.android.hilt.annotations.BoundTo
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@BoundTo(supertype = NoteDateFormatter::class, component = SingletonComponent::class)
@RelativeDate
class RelativeDateFormatter @Inject constructor(
    @ApplicationContext private val context: Context,
    @DatePattern private val dateFormat: String
) : NoteDateFormatter {

    private val dateFormatter = DateTimeFormatter.ofPattern(dateFormat)

    override fun format(instant: Instant): String {
        val targetDate = instant.atZone(SYSTEM_ZONE).toLocalDate()
        val today = LocalDate.now(SYSTEM_ZONE)

        return when (
            ChronoUnit.DAYS.between(today, targetDate)
        ) {
            0L -> context.getString(R.string.date_today)
            -1L -> context.getString(R.string.date_yesterday)
            1L -> context.getString(R.string.date_tomorrow)
            else -> targetDate.format(dateFormatter)
        }
    }

    override fun parse(dateString: String): Instant {
        return try {
            LocalDate.parse(dateString, dateFormatter)
                .atStartOfDay(SYSTEM_ZONE)
                .toInstant()
        } catch (e: Exception) {
            throw IllegalArgumentException(
                "Invalid date format: $dateString. Expected: $dateFormat",
                e
            )
        }
    }

    companion object {
        private val SYSTEM_ZONE = ZoneId.systemDefault()
    }
}
