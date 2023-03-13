package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data

import kotlinx.datetime.LocalDateTime
import org.jetbrains.annotations.TestOnly
import ua.syt0r.kanji.presentation.common.ui.kanji.PreviewKanji
import kotlin.random.Random

data class PracticeGroupItem(
    val character: String,
    val positionInPractice: Int,
    val frequency: Int?,
    val writingSummary: PracticeSummary,
    val readingSummary: PracticeSummary,
) {

    companion object {

        @TestOnly
        fun random(
            reviewState: CharacterReviewState = CharacterReviewState.values().random()
        ) = PracticeGroupItem(
            character = PreviewKanji.randomKanji(),
            positionInPractice = Random.nextInt(),
            frequency = Random.nextInt(),
            writingSummary = PracticeSummary(
                LocalDateTime(2020, 1, 1, 1, 1),
                LocalDateTime(2020, 1, 1, 1, 1),
                reviewState
            ),
            readingSummary = PracticeSummary(
                LocalDateTime(2020, 1, 1, 1, 1),
                LocalDateTime(2020, 1, 1, 1, 1),
                reviewState
            )
        )

    }

}

data class PracticeSummary(
    val firstReviewDate: LocalDateTime?,
    val lastReviewDate: LocalDateTime?,
    val state: CharacterReviewState
)
