package ua.syt0r.kanji.presentation.screen.screen.practice_preview.use_case

import ua.syt0r.kanji.presentation.screen.screen.practice_preview.PracticePreviewScreenContract
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.data.PreviewCharacterData
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.data.SortConfiguration
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.data.SortOption
import javax.inject.Inject

class PracticePreviewSortListUseCase @Inject constructor() :
    PracticePreviewScreenContract.SortListUseCase {

    override fun sort(
        sortConfiguration: SortConfiguration,
        characterList: List<PreviewCharacterData>
    ): List<PreviewCharacterData> {

        val comparator = when (sortConfiguration.sortOption) {
            SortOption.NAME -> {

                val nameComparator: Comparator<PreviewCharacterData> = when (
                    sortConfiguration.isDescending
                ) {
                    true -> compareByDescending { it.character }
                    false -> compareBy { it.character }
                }

                nameComparator

            }
            SortOption.FREQUENCY -> {

                val frequencyComparator: Comparator<PreviewCharacterData> = when (
                    sortConfiguration.isDescending
                ) {
                    true -> compareByDescending { it.frequency }
                    false -> compareBy { it.frequency }
                }

                frequencyComparator.thenBy { it.character }

            }
            SortOption.REVIEW_TIME -> {

                val reviewDateComparator: Comparator<PreviewCharacterData> = when (
                    sortConfiguration.isDescending
                ) {
                    true -> compareByDescending { it.lastReviewTime }
                    false -> compareBy { it.lastReviewTime }
                }

                reviewDateComparator
                    .thenBy { it.frequency }
                    .thenBy { it.character }

            }
        }

        return characterList.sortedWith(comparator)
    }

}
