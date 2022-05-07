package ua.syt0r.kanji.presentation.screen.screen.practice_preview.data

data class SortConfiguration(
    val sortOption: SortOption,
    val isDescending: Boolean // smaller frequency and character are considered as larger
) {

    companion object {
        val default = SortConfiguration(sortOption = SortOption.REVIEW_TIME, isDescending = true)
    }

}