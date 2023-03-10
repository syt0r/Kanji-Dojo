package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.data

sealed interface RadicalSearchListItem {
    data class StrokeGroup(val count: Int) : RadicalSearchListItem
    data class Character(
        val character: String,
        val isEnabled: Boolean
    ) : RadicalSearchListItem
}