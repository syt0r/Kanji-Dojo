package ua.syt0r.kanji.core.user_data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.data.SortConfiguration
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.data.SortOption
import javax.inject.Inject
import javax.inject.Singleton

private val Context.preferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = "preferences")

@Singleton
class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) : UserDataContract.PreferencesRepository {

    private val analyticsEnabledKey = booleanPreferencesKey("analytics_enabled")

    private val sortOptionKey = stringPreferencesKey("sort_option")
    private val isSortDescendingKey = booleanPreferencesKey("is_desc")

    @Inject
    constructor(@ApplicationContext context: Context) : this(
        dataStore = context.preferencesDataStore
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

}