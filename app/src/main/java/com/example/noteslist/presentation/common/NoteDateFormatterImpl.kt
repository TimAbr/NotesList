package com.example.noteslist.presentation.common

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class NoteDateFormatterImpl(
    private val dateFormat: String = DEFAULT_DATE_FORMAT
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
        private const val DEFAULT_DATE_FORMAT = "dd.MM.yyyy HH:mm"
        private val SYSTEM_ZONE = ZoneId.systemDefault()
    }
}
