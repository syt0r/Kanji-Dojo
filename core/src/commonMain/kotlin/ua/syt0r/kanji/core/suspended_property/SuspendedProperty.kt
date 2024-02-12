package ua.syt0r.kanji.core.suspended_property

interface SuspendedProperty<T> {
    suspend fun get(): T
    suspend fun set(value: T)
}

interface SuspendedPropertyProvider {

    fun createBooleanProperty(
        key: String,
        initialValueProvider: () -> Boolean
    ): SuspendedProperty<Boolean>

    fun createIntProperty(
        key: String,
        initialValueProvider: () -> Int
    ): SuspendedProperty<Int>

}