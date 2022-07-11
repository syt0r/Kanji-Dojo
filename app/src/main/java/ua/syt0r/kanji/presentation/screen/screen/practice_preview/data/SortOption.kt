package ua.syt0r.kanji.presentation.screen.screen.practice_preview.data

enum class SortOption(
    val title: String,
    val ascTitle: String,
    val descTitle: String,
    val hint: String? = null
) {
    REVIEW_TIME(
        title = "Review date",
        ascTitle = "Not reviewed",
        descTitle = "Last reviewed",
    ),
    FREQUENCY(
        title = "Frequency",
        ascTitle = "Frequent first",
        descTitle = "Frequent last",
        hint = "Occurrence frequency of a character in newspapers"
    ),
    NAME(
        title = "Name",
        ascTitle = "Ascending",
        descTitle = "Descending"
    )
}