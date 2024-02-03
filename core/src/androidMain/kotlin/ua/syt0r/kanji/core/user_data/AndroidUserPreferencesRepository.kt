package ua.syt0r.kanji.core.user_data

import android.content.Context
import androidx.compose.ui.text.intl.Locale
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalTime
import ua.syt0r.kanji.core.user_data.model.FilterOption
import ua.syt0r.kanji.core.user_data.model.OutcomeSelectionConfiguration
import ua.syt0r.kanji.core.user_data.model.PracticePreviewLayout
import ua.syt0r.kanji.core.user_data.model.PracticeType
import ua.syt0r.kanji.core.user_data.model.SortOption
import ua.syt0r.kanji.core.user_data.model.SupportedTheme

private const val PreferencesFileName = "preferences"

class AndroidUserPreferencesRepository private constructor(
    private val dataStore: DataStore<Preferences>,
    private val defaultAnalyticsEnabled: Boolean,
    private val defaultAnalyticsSuggestionEnabled: Boolean,
    private val isSystemLanguageJapanese: Boolean,
) : UserPreferencesRepository {

    private val analyticsEnabledKey = booleanPreferencesKey("analytics_enabled")
    private val analyticsSuggestionKey = booleanPreferencesKey("analytics_suggestion_enabled")
    private val noTranslationsLayoutEnabledKey = booleanPreferencesKey("no_trans_layout_enabled")
    private val leftHandedModeKey = booleanPreferencesKey("left_handed_mode")
    private val altStrokeEvaluatorKey = booleanPreferencesKey("use_alt_stroke_evaluator")
    private val showStrokeCountKey = booleanPreferencesKey("show_stroke_count")

    private val practiceTypeKey = stringPreferencesKey("practice_type")
    private val filterOptionKey = stringPreferencesKey("filter_option")
    private val sortOptionKey = stringPreferencesKey("sort_option")
    private val isSortDescendingKey = booleanPreferencesKey("is_desc")
    private val practicePreviewLayoutKey = intPreferencesKey("practice_preview_layout")
    private val kanaGroupsEnabledKey = booleanPreferencesKey("kana_groups_enabled")

    private val shouldHighlightRadicalsKey = booleanPreferencesKey("highlight_radicals")

    private val writingPracticeToleratedMistakesCountKey =
        intPreferencesKey("writing_tolerated_mistakes")
    private val readingPracticeToleratedMistakesCountKey =
        intPreferencesKey("reading_tolerated_mistakes")

    private val themeKey = stringPreferencesKey("theme")

    private val dailyLimitEnabledKey = booleanPreferencesKey("daily_limit_enabled")
    private val dailyLearnLimitKey = intPreferencesKey("daily_learn_limit")
    private val dailyReviewLimitKey = intPreferencesKey("daily_review_limit")

    private val reminderEnabledKey = booleanPreferencesKey("reminder_enabled")
    private val reminderTimeKey = intPreferencesKey("reminder_time")

    private val lastVersionWhenChangesDialogShownKey =
        stringPreferencesKey("last_changes_dialog_version_shown")

    private val dashboardSortByTimeKey = booleanPreferencesKey("dashboard_sort_by_time")

    constructor(
        context: Context,
        defaultAnalyticsEnabled: Boolean,
        defaultAnalyticsSuggestionEnabled: Boolean
    ) : this(
        dataStore = PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile(PreferencesFileName)
        },
        defaultAnalyticsEnabled = defaultAnalyticsEnabled,
        defaultAnalyticsSuggestionEnabled = defaultAnalyticsSuggestionEnabled,
        isSystemLanguageJapanese = Locale.current.language == "ja"
    )


    override suspend fun getAnalyticsEnabled(): Boolean {
        return dataStore.data.first()[analyticsEnabledKey] ?: defaultAnalyticsEnabled
    }

    override suspend fun setAnalyticsEnabled(value: Boolean) {
        dataStore.edit { it[analyticsEnabledKey] = value }
    }

    override suspend fun getShouldShowAnalyticsSuggestion(): Boolean {
        return dataStore.data.first()[analyticsSuggestionKey] ?: defaultAnalyticsSuggestionEnabled
    }

    override suspend fun setShouldShowAnalyticsSuggestion(value: Boolean) {
        dataStore.edit { it[analyticsSuggestionKey] = value }
    }

    override suspend fun getNoTranslationsLayoutEnabled(): Boolean {
        return dataStore.data.first()[noTranslationsLayoutEnabledKey]
            ?: isSystemLanguageJapanese.also { setNoTranslationsLayoutEnabled(it) }
    }

    override suspend fun setNoTranslationsLayoutEnabled(value: Boolean) {
        dataStore.edit { it[noTranslationsLayoutEnabledKey] = value }
    }

    override suspend fun getLeftHandedModeEnabled(): Boolean {
        return dataStore.data.first()[leftHandedModeKey] ?: false
    }

    override suspend fun setLeftHandedModeEnabled(value: Boolean) {
        dataStore.edit { it[leftHandedModeKey] = value }
    }

    override suspend fun getAltStrokeEvaluatorEnabled(): Boolean {
        return dataStore.data.first()[altStrokeEvaluatorKey] ?: false
    }

    override suspend fun setAltStrokeEvaluatorEnabled(value: Boolean) {
        dataStore.edit { it[altStrokeEvaluatorKey] = value }
    }

    override suspend fun getShowStrokeCountEnabled(): Boolean {
        return dataStore.data.first()[showStrokeCountKey] ?: false
    }

    override suspend fun setShowStrokeCountEnabled(value: Boolean) {
        dataStore.edit { it[showStrokeCountKey] = value }
    }


    override suspend fun setPracticeType(type: PracticeType) {
        dataStore.edit { it[practiceTypeKey] = type.name }
    }

    override suspend fun getPracticeType(): PracticeType? {
        return dataStore.data.first()[practiceTypeKey]
            ?.let { value -> PracticeType.values().find { it.name == value } }
    }

    override suspend fun setFilterOption(filterOption: FilterOption) {
        dataStore.edit { it[filterOptionKey] = filterOption.name }
    }

    override suspend fun getFilterOption(): FilterOption? {
        return dataStore.data.first()[filterOptionKey]
            ?.let { value -> FilterOption.values().find { it.name == value } }
    }

    override suspend fun setSortOption(sortOption: SortOption) {
        dataStore.edit { it[sortOptionKey] = sortOption.name }
    }

    override suspend fun getSortOption(): SortOption? {
        return dataStore.data.first()[sortOptionKey]
            ?.let { value -> SortOption.values().find { it.name == value } }
    }

    override suspend fun setIsSortDescending(isDescending: Boolean) {
        dataStore.edit { it[isSortDescendingKey] = isDescending }
    }

    override suspend fun getIsSortDescending(): Boolean? {
        return dataStore.data.first()[isSortDescendingKey]
    }

    override suspend fun setPracticePreviewLayout(layout: PracticePreviewLayout) {
        dataStore.edit { it[practicePreviewLayoutKey] = layout.ordinal }
    }

    override suspend fun getPracticePreviewLayout(): PracticePreviewLayout? {
        return dataStore.data.first()[practicePreviewLayoutKey]
            ?.let { value -> PracticePreviewLayout.values().find { it.ordinal == value } }
    }

    override suspend fun setKanaGroupsEnabled(value: Boolean) {
        dataStore.edit { it[kanaGroupsEnabledKey] = value }
    }

    override suspend fun getKanaGroupsEnabled(): Boolean {
        return dataStore.data.first()[kanaGroupsEnabledKey] ?: true
    }

    override suspend fun getShouldHighlightRadicals(): Boolean {
        return dataStore.data.first()[shouldHighlightRadicalsKey] ?: true
    }

    override suspend fun setShouldHighlightRadicals(value: Boolean) {
        dataStore.edit { it[shouldHighlightRadicalsKey] = value }
    }

    override suspend fun getWritingOutcomeSelectionConfiguration(): OutcomeSelectionConfiguration? {
        return dataStore.data.first()[writingPracticeToleratedMistakesCountKey]
            ?.let { OutcomeSelectionConfiguration(it) }
    }

    override suspend fun setWritingOutcomeSelectionConfiguration(config: OutcomeSelectionConfiguration) {
        dataStore.edit {
            it[writingPracticeToleratedMistakesCountKey] = config.toleratedMistakesCount
        }
    }

    override suspend fun getReadingOutcomeSelectionConfiguration(): OutcomeSelectionConfiguration? {
        return dataStore.data.first()[readingPracticeToleratedMistakesCountKey]
            ?.let { OutcomeSelectionConfiguration(it) }
    }

    override suspend fun setReadingOutcomeSelectionConfiguration(config: OutcomeSelectionConfiguration) {
        dataStore.edit {
            it[readingPracticeToleratedMistakesCountKey] = config.toleratedMistakesCount
        }
    }

    override suspend fun getTheme(): SupportedTheme? {
        return dataStore.data.first()[themeKey]
            ?.let { value -> SupportedTheme.values().find { it.name == value } }
    }

    override suspend fun setTheme(theme: SupportedTheme) {
        dataStore.edit { it[themeKey] = theme.name }
    }

    override suspend fun getDailyLimitEnabled(): Boolean {
        return dataStore.data.first()[dailyLimitEnabledKey] ?: false
    }

    override suspend fun setDailyLimitEnabled(value: Boolean) {
        dataStore.edit { it[dailyLimitEnabledKey] = value }
    }

    override suspend fun getDailyLearnLimit(): Int? {
        return dataStore.data.first()[dailyLearnLimitKey]
    }

    override suspend fun setDailyLearnLimit(value: Int) {
        dataStore.edit { it[dailyLearnLimitKey] = value }
    }

    override suspend fun getDailyReviewLimit(): Int? {
        return dataStore.data.first()[dailyReviewLimitKey]
    }

    override suspend fun setDailyReviewLimit(value: Int) {
        dataStore.edit { it[dailyReviewLimitKey] = value }
    }

    override suspend fun getReminderEnabled(): Boolean? {
        return dataStore.data.first()[reminderEnabledKey]
    }

    override suspend fun setReminderEnabled(value: Boolean) {
        dataStore.edit { it[reminderEnabledKey] = value }
    }

    override suspend fun getReminderTime(): LocalTime? {
        return dataStore.data.first()[reminderTimeKey]?.let { LocalTime.fromSecondOfDay(it) }
    }

    override suspend fun setReminderTime(value: LocalTime) {
        dataStore.edit { it[reminderTimeKey] = value.toSecondOfDay() }
    }

    override suspend fun getLastAppVersionWhenChangesDialogShown(): String? {
        return dataStore.data.first()[lastVersionWhenChangesDialogShownKey]
    }

    override suspend fun setLastAppVersionWhenChangesDialogShown(value: String) {
        dataStore.edit { it[lastVersionWhenChangesDialogShownKey] = value }
    }

    override suspend fun getDashboardSortByTime(): Boolean {
        return dataStore.data.first()[dashboardSortByTimeKey] ?: false
    }

    override suspend fun setDashboardSortByTime(value: Boolean) {
        dataStore.edit { it[dashboardSortByTimeKey] = value }
    }

}