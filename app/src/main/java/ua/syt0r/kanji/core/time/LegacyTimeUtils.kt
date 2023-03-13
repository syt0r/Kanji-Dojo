package ua.syt0r.kanji.core.time

import java.time.LocalDate
import java.time.LocalDateTime

interface LegacyTimeUtils {
    fun getCurrentDay(): LocalDate
    fun getCurrentTime(): LocalDateTime
}