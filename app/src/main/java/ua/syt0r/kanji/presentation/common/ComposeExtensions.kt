package ua.syt0r.kanji.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.LiveData

@Composable
fun <T> LiveData<T>.observeAsNonNullState(): State<T> = observeAsState() as State<T>