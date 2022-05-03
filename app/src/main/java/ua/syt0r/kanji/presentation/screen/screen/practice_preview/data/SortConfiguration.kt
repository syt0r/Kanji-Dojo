package ua.syt0r.kanji.presentation.screen.screen.practice_preview.data

data class SortConfiguration(
    val sortOption: SortOption,
    val isDescending: Boolean
) {

    companion object {
        val default = SortConfiguration(sortOption = SortOption.NAME, isDescending = false)
    }

}