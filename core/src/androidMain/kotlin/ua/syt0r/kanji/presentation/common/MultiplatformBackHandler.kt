package ua.syt0r.kanji.presentation.common

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

@Composable
actual fun MultiplatformBackHandler(onBack: () -> Unit) {
    BackHandler(onBack = onBack)
}