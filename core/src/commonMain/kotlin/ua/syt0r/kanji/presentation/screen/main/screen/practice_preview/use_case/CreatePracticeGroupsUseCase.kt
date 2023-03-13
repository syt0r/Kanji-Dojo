package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.use_case

import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.PracticePreviewScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.*

class CreatePracticeGroupsUseCase :
    PracticePreviewScreenContract.CreatePracticeGroupsUseCase {

    companion object {
        private const val CharactersInGroup = 6
    }

    override fun create(items: List<PracticeGroupItem>, type: PracticeType): List<PracticeGroup> {
        return items.chunked(CharactersInGroup)
            .mapIndexed { index, groupItems ->
                val itemReviewStates = when (type) {
                    PracticeType.Writing -> groupItems.map { it.writingSummary.state }
                    PracticeType.Reading -> groupItems.map { it.readingSummary.state }
                }

                val groupReviewState = when {
                    itemReviewStates.all { it == CharacterReviewState.RecentlyReviewed } -> CharacterReviewState.RecentlyReviewed
                    itemReviewStates.any { it == CharacterReviewState.NeedReview } -> CharacterReviewState.NeedReview
                    else -> CharacterReviewState.NeverReviewed
                }

                val summary = when (type) {
                    PracticeType.Writing -> PracticeSummary(
                        firstReviewDate = groupItems
                            .mapNotNull { it.writingSummary.firstReviewDate }
                            .minOrNull(),
                        lastReviewDate = groupItems
                            .mapNotNull { it.writingSummary.lastReviewDate }
                            .maxOrNull(),
                        state = groupReviewState
                    )
                    PracticeType.Reading -> PracticeSummary(
                        firstReviewDate = groupItems
                            .mapNotNull { it.readingSummary.firstReviewDate }
                            .minOrNull(),
                        lastReviewDate = groupItems
                            .mapNotNull { it.readingSummary.lastReviewDate }
                            .maxOrNull(),
                        state = groupReviewState
                    )
                }

                PracticeGroup(
                    index = index + 1,
                    items = groupItems,
                    summary = summary,
                    reviewState = groupReviewState
                )
            }
    }

}
