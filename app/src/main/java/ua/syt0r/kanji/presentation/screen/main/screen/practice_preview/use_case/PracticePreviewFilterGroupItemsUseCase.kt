package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.use_case

import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.PracticePreviewScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.CharacterReviewState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.FilterOption
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticeGroupItem
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticeType
import javax.inject.Inject

class PracticePreviewFilterGroupItemsUseCase @Inject constructor() :
    PracticePreviewScreenContract.FilterGroupItemsUseCase {

    override fun filter(
        items: List<PracticeGroupItem>,
        practiceType: PracticeType,
        filterOption: FilterOption
    ): List<PracticeGroupItem> {
        return items.filter {
            val reviewState = when (practiceType) {
                PracticeType.Writing -> it.writingSummary.state
                PracticeType.Reading -> it.readingSummary.state
            }
            when (filterOption) {
                FilterOption.All -> true
                FilterOption.ReviewOnly -> reviewState == CharacterReviewState.NeedReview
                FilterOption.NewOnly -> reviewState == CharacterReviewState.NeverReviewed
            }
        }
    }

}
