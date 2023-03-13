package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data

data class PracticeGroup(
    val index: Int,
    val items: List<PracticeGroupItem>,
    val summary: PracticeSummary,
    val reviewState: CharacterReviewState
) {

    companion object {

        fun random(
            index: Int,
            needReviewOnly: Boolean = false
        ): PracticeGroup {
            val items = (1..6).map {
                if (needReviewOnly) PracticeGroupItem.random(CharacterReviewState.NeedReview)
                else PracticeGroupItem.random()
            }
            return PracticeGroup(
                index = index,
                items = items,
                summary = items.random().writingSummary,
                reviewState = items.random().writingSummary.state
            )
        }

    }

}