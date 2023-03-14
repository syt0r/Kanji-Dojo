package ua.syt0r.kanji.core.time

import kotlinx.datetime.*

object DefaultTimeUtils : TimeUtils {

    override fun getCurrentDate(): LocalDate {
        return getCurrentTime().date
    }

    override fun getCurrentTime(): LocalDateTime {
        return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    }

    override fun now(): Instant {
        return Clock.System.now()
    }

}