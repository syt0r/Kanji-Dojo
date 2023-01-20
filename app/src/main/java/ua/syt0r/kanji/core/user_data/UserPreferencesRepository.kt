package ua.syt0r.kanji.core.user_data

import android.content.Context
import androidx.compose.ui.text.intl.Locale
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.SortConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.SortOption

private const val PreferencesFileName = "preferences"

class UserPreferencesRepository private constructor(
    private val dataStore: DataStore<Preferences>,
    private val defaultAnalyticsEnabled: Boolean,
    private val defaultAnalyticsSuggestionEnabled: Boolean,
    private val isSystemLanguageJapanese: Boolean,
) : UserDataContract.PreferencesRepository {

    private val analyticsEnabledKey = booleanPreferencesKey("analytics_enabled")
    private val analyticsSuggestionKey = booleanPreferencesKey("analytics_suggestion_enabled")
    private val noTranslationsLayoutEnabledKey = booleanPreferencesKey("no_trans_layout_enabled")
    private val sortOptionKey = stringPreferencesKey("sort_option")
    private val isSortDescendingKey = booleanPreferencesKey("is_desc")
    private val shouldHighlightRadicalsKey = booleanPreferencesKey("highlight_radicals")

    constructor(
        @ApplicationContext context: Context,
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


    override suspend fun getSortConfiguration(): SortConfiguration? {
        return dataStore.data.first().let {
            val sortOption = it[sortOptionKey]
            val isDesc = it[isSortDescendingKey]
            if (sortOption != null && isDesc != null) {
                SortConfiguration(
                    sortOption = SortOption.valueOf(sortOption),
                    isDescending = isDesc
                )
            } else {
                null
            }
        }
    }

    override suspend fun setSortConfiguration(configuration: SortConfiguration) {
        dataStore.edit {
            it[sortOptionKey] = configuration.sortOption.name
            it[isSortDescendingKey] = configuration.isDescending
        }
    }

    override suspend fun getShouldHighlightRadicals(): Boolean {
        return dataStore.data.first()[shouldHighlightRadicalsKey] ?: true
    }

    override suspend fun setShouldHighlightRadicals(value: Boolean) {
        dataStore.edit { it[shouldHighlightRadicalsKey] = value }
    }

}
