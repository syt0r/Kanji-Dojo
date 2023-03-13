package ua.syt0r.kanji.core.time

import java.time.LocalDate
import java.time.LocalDateTime

object DefaultLegacyTimeUtils : LegacyTimeUtils {
    override fun getCurrentDay(): LocalDate {
        return LocalDate.now()
    }

    override fun getCurrentTime(): LocalDateTime {
        return LocalDateTime.now()
    }
}