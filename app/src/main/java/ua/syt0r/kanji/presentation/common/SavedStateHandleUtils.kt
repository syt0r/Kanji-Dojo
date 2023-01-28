package ua.syt0r.kanji.presentation.common

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private class SavedStateProperty<T>(
    private val savedStateHandle: SavedStateHandle
) : ReadWriteProperty<ViewModel, T?> {

    override fun getValue(thisRef: ViewModel, property: KProperty<*>): T? {
        return savedStateHandle[property.name]
    }

    override fun setValue(thisRef: ViewModel, property: KProperty<*>, value: T?) {
        savedStateHandle[property.name] = value
    }

}

fun <T : Parcelable> SavedStateHandle.parcelableProperty(): ReadWriteProperty<ViewModel, T?> {
    return SavedStateProperty(this)
}