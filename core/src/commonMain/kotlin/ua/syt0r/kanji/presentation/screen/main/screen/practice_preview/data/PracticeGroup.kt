package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data

import kotlinx.datetime.LocalDateTime

data class PracticeGroup(
    val index: Int,
    val items: List<PracticePreviewItem>,
    val summary: PracticeGroupSummary,
    val reviewState: CharacterReviewState
) {

    companion object {

        fun random(
            index: Int,
            needReviewOnly: Boolean = false
        ): PracticeGroup {
            val items = (1..6).map {
                if (needReviewOnly) PracticePreviewItem.random(CharacterReviewState.NeedReview)
                else PracticePreviewItem.random()
            }
            return PracticeGroup(
                index = index,
                items = items,
                summary = items.random().writingSummary.run {
                    PracticeGroupSummary(firstReviewDate, lastReviewDate, state)
                },
                reviewState = items.random().writingSummary.state
            )
        }

    }

}

data class PracticeGroupSummary(
    val firstReviewDate: LocalDateTime?,
    val lastReviewDate: LocalDateTime?,
    val state: CharacterReviewState
)
