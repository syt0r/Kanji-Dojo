package ua.syt0r.kanji.core.suspended_property

import java.util.prefs.Preferences

private abstract class JvmSuspendedProperty<T>(
    private val preferences: Preferences,
    override val key: String
) : SuspendedProperty<T> {

    override suspend fun isModified(): Boolean {
        return preferences.keys().contains(key)
    }

}

class JvmSuspendedPropertyProvider(
    private val preferences: Preferences
) : SuspendedPropertyProvider {

    override fun createBooleanProperty(
        key: String,
        initialValueProvider: () -> Boolean
    ): SuspendedProperty<Boolean> {
        return object : JvmSuspendedProperty<Boolean>(preferences, key), BooleanSuspendedProperty {
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
        return object : JvmSuspendedProperty<Int>(preferences, key), IntegerSuspendedProperty {
            override suspend fun get(): Int {
                return preferences.getInt(key, initialValueProvider())
            }

            override suspend fun set(value: Int) {
                preferences.putInt(key, value)
            }
        }
    }

    override fun createStringProperty(
        key: String,
        initialValueProvider: () -> String
    ): SuspendedProperty<String> {
        return object : JvmSuspendedProperty<String>(preferences, key), StringSuspendedProperty {
            override suspend fun get(): String {
                return preferences.get(key, initialValueProvider())
            }

            override suspend fun set(value: String) {
                preferences.put(key, value)
            }
        }
    }

}
