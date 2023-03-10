package ua.syt0r.kanji.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.getKoin

@Composable
actual inline fun <reified T> getMultiplatformViewMode(): T {
    val coroutineScope = rememberCoroutineScope()
    return remember { getKoin().get { parametersOf(coroutineScope) } }
}