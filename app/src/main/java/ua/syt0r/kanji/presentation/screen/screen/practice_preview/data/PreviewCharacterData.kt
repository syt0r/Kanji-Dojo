package ua.syt0r.kanji.presentation.screen.screen.practice_preview.data

import java.time.LocalDateTime

data class PreviewCharacterData(
    val character: String,
    val frequency: Int,
    val lastReviewTime: LocalDateTime
)