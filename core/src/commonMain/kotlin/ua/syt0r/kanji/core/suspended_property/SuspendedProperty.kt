package ua.syt0r.kanji.core.suspended_property

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.int

interface SuspendedProperty<T> {

    val key: String

    suspend fun get(): T
    suspend fun set(value: T)

    suspend fun isModified(): Boolean
    suspend fun backup(): JsonElement
    suspend fun restore(value: JsonElement)

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

interface BooleanSuspendedProperty : SuspendedProperty<Boolean> {

    override suspend fun backup(): JsonElement {
        return JsonPrimitive(get())
    }

    override suspend fun restore(value: JsonElement) {
        value as JsonPrimitive
        set(value.boolean)
    }

}

interface IntegerSuspendedProperty : SuspendedProperty<Int> {

    override suspend fun backup(): JsonElement {
        return JsonPrimitive(get())
    }

    override suspend fun restore(value: JsonElement) {
        value as JsonPrimitive
        set(value.int)
    }

}
