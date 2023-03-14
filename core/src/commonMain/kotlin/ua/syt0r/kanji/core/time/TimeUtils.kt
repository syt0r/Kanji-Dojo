package ua.syt0r.kanji.core.time

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

interface TimeUtils {
    fun getCurrentDate(): LocalDate
    fun getCurrentTime(): LocalDateTime
    fun now(): Instant
}