package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.use_case

import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.PracticePreviewScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.CharacterReviewState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.FilterConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticePreviewItem
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticeType

class PracticePreviewFilterItemsUseCase :
    PracticePreviewScreenContract.FilterItemsUseCase {

    override fun filter(
        items: List<PracticePreviewItem>,
        practiceType: PracticeType,
        filterConfiguration: FilterConfiguration
    ): List<PracticePreviewItem> {
        return items.filter {
            val reviewState = when (practiceType) {
                PracticeType.Writing -> it.writingSummary.state
                PracticeType.Reading -> it.readingSummary.state
            }
            when (reviewState) {
                CharacterReviewState.New -> filterConfiguration.showNew
                CharacterReviewState.Due -> filterConfiguration.showDue
                CharacterReviewState.Done -> filterConfiguration.showDone
            }
        }
    }

}
