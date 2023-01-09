package ua.syt0r.kanji.core.user_data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.SortConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.SortOption
import javax.inject.Inject
import javax.inject.Singleton

private const val PreferencesFileName = "preferences"

@Singleton
class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) : UserDataContract.PreferencesRepository {

    private val analyticsEnabledKey = booleanPreferencesKey("analytics_enabled")
    private val sortOptionKey = stringPreferencesKey("sort_option")
    private val isSortDescendingKey = booleanPreferencesKey("is_desc")
    private val shouldHighlightRadicalsKey = booleanPreferencesKey("highlight_radicals")

    @Inject
    constructor(@ApplicationContext context: Context) : this(
        dataStore = PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile(PreferencesFileName)
        }
    )

    override val analyticsEnabled: Flow<Boolean>
        get() = dataStore.data
            .map {
                Logger.d("analyticsEnabled update")
                it[analyticsEnabledKey] ?: true
            }

    override suspend fun setAnalyticsEnabled(enabled: Boolean) {
        dataStore.edit {
            Logger.d("analyticsEnabled edit")
            it[analyticsEnabledKey] = enabled
        }
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