package com.yamanorlon.grog.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object DateTimeUtils {
    private val instantFormatter = DateTimeFormatter.ISO_INSTANT
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun formatInstant(instant: Instant): String = instantFormatter.format(instant)

    fun formatDate(date: LocalDate): String = dateFormatter.format(date)

    fun parseInstant(value: String): Instant = Instant.parse(value)

    fun parseDate(value: String): LocalDate = LocalDate.parse(value)

    fun now(): Instant = Instant.now()

    fun nowUtcOffset() = java.time.OffsetDateTime.now(ZoneOffset.UTC)
}
