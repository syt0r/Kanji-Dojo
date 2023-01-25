package ua.syt0r.kanji.core.time

import java.time.LocalDate

object DefaultTimeUtils : TimeUtils {
    override fun getCurrentDay(): LocalDate {
        return LocalDate.now()
    }
}