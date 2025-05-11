package ua.syt0r.kanji.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.backhandler.BackHandler

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun MultiplatformBackHandler(onBack: () -> Unit) {
    BackHandler(true, onBack)
}