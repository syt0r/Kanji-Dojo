package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data

import kotlinx.datetime.LocalDateTime
import org.jetbrains.annotations.TestOnly
import ua.syt0r.kanji.presentation.common.ui.kanji.PreviewKanji
import kotlin.random.Random

data class PracticePreviewItem(
    val character: String,
    val positionInPractice: Int,
    val frequency: Int?,
    val writingSummary: PracticeItemSummary,
    val readingSummary: PracticeItemSummary,
) {

    companion object {

        @TestOnly
        fun random(
            reviewState: CharacterReviewState = CharacterReviewState.values().random()
        ) = PracticePreviewItem(
            character = PreviewKanji.randomKanji(),
            positionInPractice = Random.nextInt(),
            frequency = Random.nextInt(),
            writingSummary = PracticeItemSummary(
                LocalDateTime(2020, 1, 1, 1, 1),
                LocalDateTime(2020, 1, 1, 1, 1),
                LocalDateTime(2020, 1, 2, 1, 1),
                2,
                1,
                reviewState
            ),
            readingSummary = PracticeItemSummary(
                LocalDateTime(2020, 1, 1, 1, 1),
                LocalDateTime(2020, 1, 1, 1, 1),
                LocalDateTime(2020, 1, 2, 1, 1),
                2,
                1,
                reviewState
            )
        )

    }

}

data class PracticeItemSummary(
    val firstReviewDate: LocalDateTime?,
    val lastReviewDate: LocalDateTime?,
    val expectedReviewDate: LocalDateTime?,
    val lapses: Int,
    val repeats: Int,
    val state: CharacterReviewState
)
