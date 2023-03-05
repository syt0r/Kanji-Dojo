package ua.syt0r.kanji.core.time

import java.time.LocalDate
import java.time.LocalDateTime

interface TimeUtils {
    fun getCurrentDay(): LocalDate
    fun getCurrentTime(): LocalDateTime
}