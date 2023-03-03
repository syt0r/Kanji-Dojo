package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data

import androidx.annotation.StringRes
import ua.syt0r.kanji.R

data class PracticePreviewScreenConfiguration(
    val practiceType: PracticeType = PracticeType.Writing,
    val filterOption: FilterOption = FilterOption.All,
    val sortOption: SortOption = SortOption.ADD_ORDER,
    val isDescending: Boolean = false
)

enum class PracticeType(
    @StringRes val stringResId: Int
) {
    Writing(R.string.practice_preview_practice_type_writing),
    Reading(R.string.practice_preview_practice_type_reading)
}

enum class FilterOption(
    @StringRes val title: Int
) {
    All(R.string.practice_preview_filter_all),
    ReviewOnly(R.string.practice_preview_filter_review_only),
    NewOnly(R.string.practice_preview_filter_new_only)
}

enum class SortOption(
    @StringRes val title: Int,
    @StringRes val hint: Int
) {
    ADD_ORDER(
        title = R.string.practice_preview_sort_option_add_order_title,
        hint = R.string.practice_preview_sort_option_add_order_hint,
    ),
    FREQUENCY(
        title = R.string.practice_preview_sort_option_frequency_title,
        hint = R.string.practice_preview_sort_option_frequency_hint
    ),
    NAME(
        title = R.string.practice_preview_sort_option_name_title,
        hint = R.string.practice_preview_sort_option_name_hint
    )
}
