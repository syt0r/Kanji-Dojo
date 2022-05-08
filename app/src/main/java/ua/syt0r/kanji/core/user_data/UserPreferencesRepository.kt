package ua.syt0r.kanji.core.user_data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ua.syt0r.kanji.core.logger.Logger
import javax.inject.Inject
import javax.inject.Singleton

private val Context.preferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = "preferences")

@Singleton
class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) : UserDataContract.PreferencesRepository {

    private val showAnalyticsDialogKey = booleanPreferencesKey("show_analytics_dialog")
    private val analyticsEnabledKey = booleanPreferencesKey("analytics_enabled")

    @Inject
    constructor(@ApplicationContext context: Context) : this(
        dataStore = context.preferencesDataStore
    )

    override val showAnalyticsDialog: Flow<Boolean>
        get() = dataStore.data.map {
            Logger.d("showAnalyticsDialog update")
            it[showAnalyticsDialogKey] ?: true
        }

    override suspend fun setShouldShownAnalyticsDialog(shouldShow: Boolean) {
        dataStore.edit {
            Logger.d("showAnalyticsDialog edit")
            it[showAnalyticsDialogKey] = shouldShow
        }
    }

    override val analyticsEnabled: Flow<Boolean>
        get() = dataStore.data
            .map {
                Logger.d("analyticsEnabled update")
                it[analyticsEnabledKey] ?: false
            }

    override suspend fun setAnalyticsEnabled(enabled: Boolean) {
        dataStore.edit {
            Logger.d("analyticsEnabled edit")
            it[analyticsEnabledKey] = true
        }
    }


}