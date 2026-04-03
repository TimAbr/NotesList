package com.example.noteslist.presentation.common

import com.example.noteslist.presentation.di.DateTimePattern
import dagger.hilt.components.SingletonComponent
import it.czerwinski.android.hilt.annotations.BoundTo
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.inject.Inject

@BoundTo(supertype = NoteDateFormatter::class, component = SingletonComponent::class)
class NoteDateFormatterImpl @Inject constructor(
    @DateTimePattern private val dateFormat: String
) : NoteDateFormatter {
    private val dateFormatter: DateTimeFormatter

    init {
        try {
            dateFormatter = DateTimeFormatter.ofPattern(dateFormat)
                .withZone(SYSTEM_ZONE)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid date format pattern: $dateFormat", e)
        }
    }

    override fun format(instant: Instant): String {
        return dateFormatter.format(LocalDateTime.ofInstant(instant, SYSTEM_ZONE))
    }

    override fun parse(dateString: String): Instant {
        try {
            return LocalDateTime.parse(dateString, dateFormatter)
                .atZone(SYSTEM_ZONE)
                .toInstant()
        } catch (e: DateTimeParseException) {
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
