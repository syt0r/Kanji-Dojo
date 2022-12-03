package ua.syt0r.kanji.presentation.screen.screen.practice_preview.data

import androidx.annotation.StringRes
import ua.syt0r.kanji.R

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