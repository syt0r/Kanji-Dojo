package ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.data

import ua.syt0r.kanji.core.user_data.model.CharacterReviewOutcome
import kotlin.time.Duration

data class ReadingPracticeSummaryItem(
    val character: String,
    val repeats: Int,
    val reviewDuration: Duration,
    val outcome: CharacterReviewOutcome
)