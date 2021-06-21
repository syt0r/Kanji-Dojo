package ua.syt0r.kanji.core.user_data

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalTime
import javax.inject.Inject

private val Context.preferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = "preferences")

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) : UserDataContract.PreferencesRepository {

    @VisibleForTesting
    val notificationsEnabledKey = booleanPreferencesKey("notifications_enabled")

    @VisibleForTesting
    val notificationsDisplayTimedKey = intPreferencesKey("notification_display_time")


    @Inject
    constructor(context: Context) : this(
        dataStore = context.preferencesDataStore
    )


    override fun areNotificationsEnabled(): Flow<Boolean> {
        return dataStore.data.map { it[notificationsEnabledKey] ?: false }
    }

    override suspend fun setNotificationEnabled(enabled: Boolean) {
        dataStore.edit { it[notificationsEnabledKey] = enabled }
    }

    override fun getNotificationDisplayTime(): Flow<LocalTime> {
        return dataStore.data
            .map { it[notificationsDisplayTimedKey] ?: LocalTime.of(9, 0).toSecondOfDay() }
            .map { LocalTime.ofSecondOfDay(it.toLong()) }
    }

    override suspend fun setNotificationDisplayTime(time: LocalTime) {
        dataStore.edit { it[notificationsDisplayTimedKey] = time.toSecondOfDay() }
    }

}