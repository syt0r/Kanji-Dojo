package ua.syt0r.kanji.core.user_data

import ua.syt0r.kanji.core.user_data.model.FilterOption
import ua.syt0r.kanji.core.user_data.model.PracticeType
import ua.syt0r.kanji.core.user_data.model.SortOption

interface UserPreferencesRepository {

    suspend fun getAnalyticsEnabled(): Boolean
    suspend fun setAnalyticsEnabled(value: Boolean)

    suspend fun getShouldShowAnalyticsSuggestion(): Boolean
    suspend fun setShouldShowAnalyticsSuggestion(value: Boolean)

    suspend fun getNoTranslationsLayoutEnabled(): Boolean
    suspend fun setNoTranslationsLayoutEnabled(value: Boolean)

    suspend fun setPracticeType(type: PracticeType)
    suspend fun getPracticeType(): PracticeType?
    suspend fun setFilterOption(filterOption: FilterOption)
    suspend fun getFilterOption(): FilterOption?
    suspend fun getSortOption(): SortOption?
    suspend fun setSortOption(sortOption: SortOption)
    suspend fun setIsSortDescending(isDescending: Boolean)
    suspend fun getIsSortDescending(): Boolean?

    suspend fun getShouldHighlightRadicals(): Boolean
    suspend fun setShouldHighlightRadicals(value: Boolean)

}