package ua.syt0r.kanji.core.user_data

import kotlinx.datetime.LocalTime
import ua.syt0r.kanji.core.user_data.model.FilterOption
import ua.syt0r.kanji.core.user_data.model.PracticePreviewLayout
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
        private const val practiceTypeKey = "practice_type"
        private const val filterOptionKey = "filter_option"
        private const val sortOptionKey = "sort_option"
        private const val isSortDescendingKey = "is_desc"
        private const val themeKey = "theme"
        private const val dailyLimitEnabledKey = "daily_limit_enabled"
        private const val dailyLearnLimitKey = "daily_learn_limit"
        private const val dailyReviewLimitKey = "daily_review_limit"
        private const val reminderEnabledKey = "reminder_enabled"
        private const val reminderTimeKey = "reminder_time"
        private const val lastVersionWhenChangesDialogShownKey = "last_changes_dialog_version_shown"
        private const val practicePreviewLayoutKey = "practice_preview_layout"
        private const val kanaGroupsEnabledKey = "kana_groups_enabled"
        private const val dashboardSortByTimeKey = "dashboard_sort_by_time"

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

    override suspend fun setPracticePreviewLayout(layout: PracticePreviewLayout) {
        preferences.putInt(practicePreviewLayoutKey, layout.ordinal)
    }

    override suspend fun getPracticePreviewLayout(): PracticePreviewLayout? {
        return preferences.getInt(practicePreviewLayoutKey, -1)
            .let { value -> PracticePreviewLayout.values().find { it.ordinal == value } }
    }

    override suspend fun setKanaGroupsEnabled(value: Boolean) {
        preferences.putBoolean(kanaGroupsEnabledKey, value)
    }

    override suspend fun getKanaGroupsEnabled(): Boolean {
        return preferences.getBoolean(kanaGroupsEnabledKey, true)
    }

    override suspend fun getTheme(): SupportedTheme? {
        return preferences.get(themeKey, null)
            ?.let { name -> SupportedTheme.values().find { it.name == name } }
    }

    override suspend fun setTheme(theme: SupportedTheme) {
        preferences.put(themeKey, theme.name)
    }

    override suspend fun getDailyLimitEnabled(): Boolean {
        return preferences.getBoolean(dailyLimitEnabledKey, false)
    }

    override suspend fun setDailyLimitEnabled(value: Boolean) {
        preferences.putBoolean(dailyLimitEnabledKey, value)
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

    override suspend fun getReminderEnabled(): Boolean? {
        return preferences.keys().contains(reminderEnabledKey)
            .takeIf { it }
            ?.let { preferences.getBoolean(reminderEnabledKey, false) }
    }

    override suspend fun setReminderEnabled(value: Boolean) {
        preferences.putBoolean(reminderEnabledKey, value)
    }

    override suspend fun getReminderTime(): LocalTime? {
        return preferences.getInt(reminderTimeKey, -1).takeIf { it != -1 }
            ?.let { LocalTime.fromSecondOfDay(it) }
    }

    override suspend fun setReminderTime(value: LocalTime) {
        preferences.putInt(reminderTimeKey, value.toSecondOfDay())
    }

    override suspend fun getLastAppVersionWhenChangesDialogShown(): String? {
        return preferences.get(lastVersionWhenChangesDialogShownKey, "").takeIf { it.isNotEmpty() }
    }

    override suspend fun setLastAppVersionWhenChangesDialogShown(value: String) {
        preferences.put(lastVersionWhenChangesDialogShownKey, value)
    }


    override suspend fun getDashboardSortByTime(): Boolean {
        return preferences.getBoolean(dashboardSortByTimeKey, false)
    }

    override suspend fun setDashboardSortByTime(value: Boolean) {
        preferences.putBoolean(dashboardSortByTimeKey, value)
    }

}