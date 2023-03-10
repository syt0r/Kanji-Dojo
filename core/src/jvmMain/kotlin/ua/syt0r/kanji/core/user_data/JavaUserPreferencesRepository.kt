package ua.syt0r.kanji.core.user_data

import ua.syt0r.kanji.core.user_data.model.FilterOption
import ua.syt0r.kanji.core.user_data.model.PracticeType
import ua.syt0r.kanji.core.user_data.model.SortOption
import java.util.prefs.Preferences

class JavaUserPreferencesRepository(
    private val preferences: Preferences
) : UserPreferencesRepository {

    companion object {

        private const val analyticsEnabledKey = "analytics_enabled"
        private const val analyticsSuggestionKey = "analytics_suggestion_enabled"
        private const val noTranslationsLayoutEnabledKey = "no_trans_layout_enabled"
        private const val practiceTypeKey = "practice_type"
        private const val filterOptionKey = "filter_option"
        private const val sortOptionKey = "sort_option"
        private const val isSortDescendingKey = "is_desc"
        private const val shouldHighlightRadicalsKey = "highlight_radicals"

        fun defaultPreferences(): Preferences = Preferences.userRoot().node("user_preferences")

    }

    override suspend fun getAnalyticsEnabled(): Boolean {
        return preferences.getBoolean(analyticsEnabledKey, false)
    }

    override suspend fun setAnalyticsEnabled(value: Boolean) {
        preferences.putBoolean(analyticsEnabledKey, value)
    }

    override suspend fun getShouldShowAnalyticsSuggestion(): Boolean {
        return preferences.getBoolean(analyticsSuggestionKey, false)
    }

    override suspend fun setShouldShowAnalyticsSuggestion(value: Boolean) {
        return preferences.putBoolean(analyticsSuggestionKey, value)
    }

    override suspend fun getNoTranslationsLayoutEnabled(): Boolean {
        return preferences.getBoolean(noTranslationsLayoutEnabledKey, false)
    }

    override suspend fun setNoTranslationsLayoutEnabled(value: Boolean) {
        preferences.putBoolean(noTranslationsLayoutEnabledKey, value)
    }

    override suspend fun getPracticeType(): PracticeType? {
        return PracticeType.valueOf(preferences.get(practiceTypeKey, PracticeType.Writing.name))
    }

    override suspend fun setPracticeType(type: PracticeType) {
        return preferences.put(practiceTypeKey, type.name)
    }

    override suspend fun getFilterOption(): FilterOption? {
        TODO("Not yet implemented")
    }

    override suspend fun setFilterOption(filterOption: FilterOption) {
        TODO("Not yet implemented")
    }

    override suspend fun getSortOption(): SortOption? {
        TODO("Not yet implemented")
    }

    override suspend fun setSortOption(sortOption: SortOption) {
        TODO("Not yet implemented")
    }

    override suspend fun getIsSortDescending(): Boolean? {
        TODO("Not yet implemented")
    }

    override suspend fun setIsSortDescending(isDescending: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun getShouldHighlightRadicals(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun setShouldHighlightRadicals(value: Boolean) {
        TODO("Not yet implemented")
    }

}