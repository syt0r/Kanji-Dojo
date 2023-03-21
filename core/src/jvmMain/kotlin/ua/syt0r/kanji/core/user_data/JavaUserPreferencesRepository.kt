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
        private const val leftHandedModeEnabledKey = "left_handed_mode"
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

    override suspend fun getLeftHandedModeEnabled(): Boolean {
        return preferences.getBoolean(leftHandedModeEnabledKey, false)
    }

    override suspend fun setLeftHandedModeEnabled(value: Boolean) {
        preferences.putBoolean(leftHandedModeEnabledKey, value)
    }

    override suspend fun getPracticeType(): PracticeType? {
        return preferences.get(practiceTypeKey, null)?.let { PracticeType.valueOf(it) }
    }

    override suspend fun setPracticeType(type: PracticeType) {
        return preferences.put(practiceTypeKey, type.name)
    }

    override suspend fun getFilterOption(): FilterOption? {
        return preferences.get(filterOptionKey, null)?.let { FilterOption.valueOf(it) }
    }

    override suspend fun setFilterOption(filterOption: FilterOption) {
        preferences.put(filterOptionKey, filterOption.name)
    }

    override suspend fun getSortOption(): SortOption? {
        return preferences.get(sortOptionKey, null)?.let { SortOption.valueOf(it) }
    }

    override suspend fun setSortOption(sortOption: SortOption) {
        preferences.put(sortOptionKey, sortOption.name)
    }

    override suspend fun getIsSortDescending(): Boolean? {
        return preferences.get(isSortDescendingKey, null)?.let { it.toBoolean() }
    }

    override suspend fun setIsSortDescending(isDescending: Boolean) {
        preferences.put(isSortDescendingKey, isDescending.toString())
    }

    override suspend fun getShouldHighlightRadicals(): Boolean {
        return preferences.getBoolean(shouldHighlightRadicalsKey, true)
    }

    override suspend fun setShouldHighlightRadicals(value: Boolean) {
        preferences.putBoolean(shouldHighlightRadicalsKey, value)
    }

}