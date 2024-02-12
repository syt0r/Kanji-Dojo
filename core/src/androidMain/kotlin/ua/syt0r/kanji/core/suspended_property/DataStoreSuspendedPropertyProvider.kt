package ua.syt0r.kanji.core.suspended_property

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.first

class DataStoreSuspendedPropertyProvider(
    private val dataStore: DataStore<Preferences>,
) : SuspendedPropertyProvider {

    override fun createBooleanProperty(
        key: String,
        initialValueProvider: () -> Boolean
    ): SuspendedProperty<Boolean> {
        val preferenceKey = booleanPreferencesKey(key)
        return object : SuspendedProperty<Boolean> {
            override suspend fun get(): Boolean {
                return dataStore.data.first()[preferenceKey] ?: initialValueProvider()
            }

            override suspend fun set(value: Boolean) {
                dataStore.edit { it[preferenceKey] = value }
            }
        }
    }

    override fun createIntProperty(
        key: String,
        initialValueProvider: () -> Int
    ): SuspendedProperty<Int> {
        val preferenceKey = intPreferencesKey(key)
        return object : SuspendedProperty<Int> {
            override suspend fun get(): Int {
                return dataStore.data.first()[preferenceKey] ?: initialValueProvider()
            }

            override suspend fun set(value: Int) {
                dataStore.edit { it[preferenceKey] = value }
            }
        }
    }

}