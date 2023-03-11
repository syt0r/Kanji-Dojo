package ua.syt0r.kanji.core.user_data

import android.content.Context
import androidx.compose.ui.text.intl.Locale
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.first
import ua.syt0r.kanji.core.user_data.model.FilterOption
import ua.syt0r.kanji.core.user_data.model.PracticeType
import ua.syt0r.kanji.core.user_data.model.SortOption

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

    private val practiceTypeKey = stringPreferencesKey("practice_type")
    private val filterOptionKey = stringPreferencesKey("filter_option")
    private val sortOptionKey = stringPreferencesKey("sort_option")
    private val isSortDescendingKey = booleanPreferencesKey("is_desc")

    private val shouldHighlightRadicalsKey = booleanPreferencesKey("highlight_radicals")

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


    override suspend fun setPracticeType(type: PracticeType) {
        dataStore.edit { it[practiceTypeKey] = type.name }
    }

    override suspend fun getPracticeType(): PracticeType? {
        return dataStore.data.first()[practiceTypeKey]?.let { PracticeType.valueOf(it) }
    }

    override suspend fun setFilterOption(filterOption: FilterOption) {
        dataStore.edit { it[filterOptionKey] = filterOption.name }
    }

    override suspend fun getFilterOption(): FilterOption? {
        return dataStore.data.first()[filterOptionKey]?.let { FilterOption.valueOf(it) }
    }

    override suspend fun setSortOption(sortOption: SortOption) {
        dataStore.edit { it[sortOptionKey] = sortOption.name }
    }

    override suspend fun getSortOption(): SortOption? {
        return dataStore.data.first()[sortOptionKey]?.let { SortOption.valueOf(it) }
    }

    override suspend fun setIsSortDescending(isDescending: Boolean) {
        dataStore.edit { it[isSortDescendingKey] = isDescending }
    }

    override suspend fun getIsSortDescending(): Boolean? {
        return dataStore.data.first()[isSortDescendingKey]
    }

    override suspend fun getShouldHighlightRadicals(): Boolean {
        return dataStore.data.first()[shouldHighlightRadicalsKey] ?: true
    }

    override suspend fun setShouldHighlightRadicals(value: Boolean) {
        dataStore.edit { it[shouldHighlightRadicalsKey] = value }
    }

}