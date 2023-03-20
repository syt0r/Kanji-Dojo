package ua.syt0r.kanji.presentation.common

import ua.syt0r.kanji.core.kanji_data.data.JapaneseWord

data class PaginatableJapaneseWordList(
    val totalCount: Int,
    val items: List<JapaneseWord>
) {
    val canLoadMore: Boolean = totalCount > items.size
}