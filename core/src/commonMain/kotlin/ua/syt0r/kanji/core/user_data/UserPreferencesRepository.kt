package ua.syt0r.kanji.core.user_data

import kotlinx.datetime.LocalTime
import ua.syt0r.kanji.core.suspended_property.SuspendedProperty
import ua.syt0r.kanji.core.user_data.model.FilterOption
import ua.syt0r.kanji.core.user_data.model.PracticePreviewLayout
import ua.syt0r.kanji.core.user_data.model.PracticeType
import ua.syt0r.kanji.core.user_data.model.SortOption
import ua.syt0r.kanji.core.user_data.model.SupportedTheme

interface UserPreferencesRepository {

    suspend fun getAnalyticsEnabled(): Boolean
    suspend fun setAnalyticsEnabled(value: Boolean)

    suspend fun getShouldShowAnalyticsSuggestion(): Boolean
    suspend fun setShouldShowAnalyticsSuggestion(value: Boolean)

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

interface PracticeUserPreferencesRepository {

    val noTranslationLayout: SuspendedProperty<Boolean>
    val leftHandMode: SuspendedProperty<Boolean>
    val altStrokeEvaluator: SuspendedProperty<Boolean>

    val highlightRadicals: SuspendedProperty<Boolean>
    val kanaAutoPlay: SuspendedProperty<Boolean>

    val writingRomajiInsteadOfKanaWords: SuspendedProperty<Boolean>
    val writingToleratedMistakes: SuspendedProperty<Int>

    val readingRomajiFuriganaForKanaWords: SuspendedProperty<Boolean>
    val readingToleratedMistakes: SuspendedProperty<Int>

}
