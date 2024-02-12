package ua.syt0r.kanji.core.suspended_property

import java.util.prefs.Preferences

class JvmSuspendedPropertyProvider(
    private val preferences: Preferences
) : SuspendedPropertyProvider {

    override fun createBooleanProperty(
        key: String,
        initialValueProvider: () -> Boolean
    ): SuspendedProperty<Boolean> {
        return object : SuspendedProperty<Boolean> {
            override suspend fun get(): Boolean {
                return preferences.getBoolean(key, initialValueProvider())
            }

            override suspend fun set(value: Boolean) {
                preferences.putBoolean(key, value)
            }
        }
    }

    override fun createIntProperty(
        key: String,
        initialValueProvider: () -> Int
    ): SuspendedProperty<Int> {
        return object : SuspendedProperty<Int> {
            override suspend fun get(): Int {
                return preferences.getInt(key, initialValueProvider())
            }

            override suspend fun set(value: Int) {
                preferences.putInt(key, value)
            }
        }
    }

}