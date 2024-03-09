package ua.syt0r.kanji.core.suspended_property

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.first

private abstract class AndroidSuspendedProperty<T>(
    private val dataStore: DataStore<Preferences>,
    override val key: String,
    private val initialValueProvider: () -> T
) : SuspendedProperty<T> {

    abstract val dataStoreKey: Preferences.Key<T>

    override suspend fun isModified(): Boolean {
        return dataStore.data.first().contains(dataStoreKey)
    }

    override suspend fun get(): T {
        return dataStore.data.first()[dataStoreKey] ?: initialValueProvider()
    }

    override suspend fun set(value: T) {
        dataStore.edit { it[dataStoreKey] = value }
    }

}

class DataStoreSuspendedPropertyProvider(
    private val dataStore: DataStore<Preferences>,
) : SuspendedPropertyProvider {

    override fun createBooleanProperty(
        key: String,
        initialValueProvider: () -> Boolean
    ): SuspendedProperty<Boolean> {
        return object : AndroidSuspendedProperty<Boolean>(
            dataStore = dataStore,
            key = key,
            initialValueProvider = initialValueProvider
        ), BooleanSuspendedProperty {

            override val dataStoreKey: Preferences.Key<Boolean> = booleanPreferencesKey(key)

        }
    }

    override fun createIntProperty(
        key: String,
        initialValueProvider: () -> Int
    ): SuspendedProperty<Int> {
        return object : AndroidSuspendedProperty<Int>(
            dataStore = dataStore,
            key = key,
            initialValueProvider = initialValueProvider
        ), IntegerSuspendedProperty {

            override val dataStoreKey: Preferences.Key<Int> = intPreferencesKey(key)

        }
    }

}