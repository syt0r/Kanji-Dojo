package ua.syt0r.kanji.core.user_data

import kotlinx.datetime.LocalTime
import ua.syt0r.kanji.core.user_data.model.FilterOption
import ua.syt0r.kanji.core.user_data.model.OutcomeSelectionConfiguration
import ua.syt0r.kanji.core.user_data.model.PracticePreviewLayout
import ua.syt0r.kanji.core.user_data.model.PracticeType
import ua.syt0r.kanji.core.user_data.model.SortOption
import ua.syt0r.kanji.core.user_data.model.SupportedTheme

interface UserPreferencesRepository {

    suspend fun getAnalyticsEnabled(): Boolean
    suspend fun setAnalyticsEnabled(value: Boolean)

    suspend fun getShouldShowAnalyticsSuggestion(): Boolean
    suspend fun setShouldShowAnalyticsSuggestion(value: Boolean)

    suspend fun getNoTranslationsLayoutEnabled(): Boolean
    suspend fun setNoTranslationsLayoutEnabled(value: Boolean)

    suspend fun getLeftHandedModeEnabled(): Boolean
    suspend fun setLeftHandedModeEnabled(value: Boolean)

    suspend fun getAltStrokeEvaluatorEnabled(): Boolean
    suspend fun setAltStrokeEvaluatorEnabled(value: Boolean)

    suspend fun setPracticeType(type: PracticeType)
    suspend fun getPracticeType(): PracticeType?
    suspend fun setFilterOption(filterOption: FilterOption)
    suspend fun getFilterOption(): FilterOption?
    suspend fun getSortOption(): SortOption?
    suspend fun setSortOption(sortOption: SortOption)
    suspend fun setIsSortDescending(isDescending: Boolean)
    suspend fun getIsSortDescending(): Boolean?

    suspend fun getPracticePreviewLayout(): PracticePreviewLayout?
    suspend fun setPracticePreviewLayout(layout: PracticePreviewLayout)
    suspend fun getKanaGroupsEnabled(): Boolean
    suspend fun setKanaGroupsEnabled(value: Boolean)

    suspend fun getShouldHighlightRadicals(): Boolean
    suspend fun setShouldHighlightRadicals(value: Boolean)

    suspend fun getWritingOutcomeSelectionConfiguration(): OutcomeSelectionConfiguration?
    suspend fun setWritingOutcomeSelectionConfiguration(config: OutcomeSelectionConfiguration)

    suspend fun getReadingOutcomeSelectionConfiguration(): OutcomeSelectionConfiguration?
    suspend fun setReadingOutcomeSelectionConfiguration(config: OutcomeSelectionConfiguration)

    suspend fun getTheme(): SupportedTheme?
    suspend fun setTheme(theme: SupportedTheme)

    suspend fun getDailyLimitEnabled(): Boolean
    suspend fun setDailyLimitEnabled(value: Boolean)
    suspend fun getDailyLearnLimit(): Int?
    suspend fun setDailyLearnLimit(value: Int)
    suspend fun getDailyReviewLimit(): Int?
    suspend fun setDailyReviewLimit(value: Int)

    suspend fun getReminderEnabled(): Boolean?
    suspend fun setReminderEnabled(value: Boolean)
    suspend fun getReminderTime(): LocalTime?
    suspend fun setReminderTime(value: LocalTime)

    suspend fun getLastAppVersionWhenChangesDialogShown(): String?
    suspend fun setLastAppVersionWhenChangesDialogShown(value: String)

    suspend fun getDashboardSortByTime(): Boolean
    suspend fun setDashboardSortByTime(value: Boolean)

}