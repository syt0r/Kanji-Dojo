package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.use_case

import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.PracticePreviewScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticeGroupItem
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.SortOption

class PracticePreviewSortGroupItemsUseCase :
    PracticePreviewScreenContract.SortGroupItemsUseCase {

    override fun sort(
        items: List<PracticeGroupItem>,
        sortOption: SortOption,
        isDescending: Boolean
    ): List<PracticeGroupItem> {

        val comparator = when (sortOption) {
            SortOption.ADD_ORDER -> {

                val reviewDateComparator: Comparator<PracticeGroupItem> = when (
                    isDescending
                ) {
                    true -> compareByDescending { it.positionInPractice }
                    false -> compareBy { it.positionInPractice }
                }

                reviewDateComparator

            }
            SortOption.NAME -> {

                val nameComparator: Comparator<PracticeGroupItem> = when (isDescending) {
                    true -> compareByDescending { it.character }
                    false -> compareBy { it.character }
                }

                nameComparator

            }
            SortOption.FREQUENCY -> {

                val frequencyComparator: Comparator<PracticeGroupItem> = when (isDescending) {
                    true -> compareByDescending { it.frequency }
                    false -> compareBy { it.frequency }
                }

                frequencyComparator.thenBy { it.character }

            }
        }

        return items.sortedWith(comparator)
    }

}
