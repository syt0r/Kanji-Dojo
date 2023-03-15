package ua.syt0r.kanji.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.getKoin

@Composable
actual inline fun <reified T> getMultiplatformViewModel(): T {
    val coroutineScope = rememberCoroutineScope()
    return rememberSaveable { getKoin().get { parametersOf(coroutineScope) } }
}