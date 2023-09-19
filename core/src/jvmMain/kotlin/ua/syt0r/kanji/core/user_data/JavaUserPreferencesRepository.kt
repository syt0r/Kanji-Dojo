package ua.syt0r.kanji.core.user_data

import ua.syt0r.kanji.core.user_data.model.FilterOption
import ua.syt0r.kanji.core.user_data.model.OutcomeSelectionConfiguration
import ua.syt0r.kanji.core.user_data.model.PracticeType
import ua.syt0r.kanji.core.user_data.model.SortOption
import ua.syt0r.kanji.core.user_data.model.SupportedTheme
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
        private const val writingPracticeToleratedMistakesCountKey = "writing_tolerated_mistakes"
        private const val readingPracticeToleratedMistakesCountKey = "reading_tolerated_mistakes"
        private const val themeKey = "theme"
        private const val dailyLearnLimitKey = "daily_learn_limit"
        private const val dailyReviewLimitKey = "daily_review_limit"

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
        return preferences.get(practiceTypeKey, null)
            ?.let { value -> PracticeType.values().find { it.name == value } }
    }

    override suspend fun setPracticeType(type: PracticeType) {
        return preferences.put(practiceTypeKey, type.name)
    }

    override suspend fun getFilterOption(): FilterOption? {
        return preferences.get(filterOptionKey, null)
            ?.let { value -> FilterOption.values().find { it.name == value } }
    }

    override suspend fun setFilterOption(filterOption: FilterOption) {
        preferences.put(filterOptionKey, filterOption.name)
    }

    override suspend fun getSortOption(): SortOption? {
        return preferences.get(sortOptionKey, null)
            ?.let { value -> SortOption.values().find { it.name == value } }
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

    override suspend fun getWritingOutcomeSelectionConfiguration(): OutcomeSelectionConfiguration? {
        return preferences.getInt(writingPracticeToleratedMistakesCountKey, -1)
            .takeUnless { it == -1 }
            ?.let { OutcomeSelectionConfiguration(it) }
    }

    override suspend fun setWritingOutcomeSelectionConfiguration(config: OutcomeSelectionConfiguration) {
        preferences.putInt(writingPracticeToleratedMistakesCountKey, config.toleratedMistakesCount)
    }

    override suspend fun getReadingOutcomeSelectionConfiguration(): OutcomeSelectionConfiguration? {
        return preferences.getInt(readingPracticeToleratedMistakesCountKey, -1)
            .takeUnless { it == -1 }
            ?.let { OutcomeSelectionConfiguration(it) }
    }

    override suspend fun setReadingOutcomeSelectionConfiguration(config: OutcomeSelectionConfiguration) {
        preferences.putInt(readingPracticeToleratedMistakesCountKey, config.toleratedMistakesCount)
    }

    override suspend fun getTheme(): SupportedTheme? {
        return preferences.get(themeKey, null)
            ?.let { name -> SupportedTheme.values().find { it.name == name } }
    }

    override suspend fun setTheme(theme: SupportedTheme) {
        preferences.put(themeKey, theme.name)
    }

    override suspend fun getDailyLearnLimit(): Int? {
        return preferences.getInt(dailyLearnLimitKey, -1).takeUnless { it == -1 }
    }

    override suspend fun setDailyLearnLimit(value: Int) {
        preferences.putInt(dailyLearnLimitKey, value)
    }

    override suspend fun getDailyReviewLimit(): Int? {
        return preferences.getInt(dailyReviewLimitKey, -1).takeUnless { it == -1 }
    }

    override suspend fun setDailyReviewLimit(value: Int) {
        preferences.putInt(dailyReviewLimitKey, value)
    }

}