package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.use_case

import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.PracticePreviewScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticePreviewItem
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.SortOption

class PracticePreviewSortItemsUseCase :
    PracticePreviewScreenContract.SortItemsUseCase {

    override fun sort(
        items: List<PracticePreviewItem>,
        sortOption: SortOption,
        isDescending: Boolean
    ): List<PracticePreviewItem> {

        val comparator = when (sortOption) {
            SortOption.ADD_ORDER -> {

                val reviewDateComparator: Comparator<PracticePreviewItem> = when (
                    isDescending
                ) {
                    true -> compareByDescending { it.positionInPractice }
                    false -> compareBy { it.positionInPractice }
                }

                reviewDateComparator

            }
            SortOption.NAME -> {

                val nameComparator: Comparator<PracticePreviewItem> = when (isDescending) {
                    true -> compareByDescending { it.character }
                    false -> compareBy { it.character }
                }

                nameComparator

            }
            SortOption.FREQUENCY -> {

                val frequencyComparator: Comparator<PracticePreviewItem> = when (isDescending) {
                    true -> compareByDescending { it.frequency }
                    false -> compareBy { it.frequency }
                }

                frequencyComparator.thenBy { it.character }

            }
        }

        return items.sortedWith(comparator)
    }

}
