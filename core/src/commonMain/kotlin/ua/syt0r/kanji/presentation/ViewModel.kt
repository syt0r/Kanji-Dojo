package ua.syt0r.kanji.presentation

import androidx.compose.runtime.Composable

@Composable
expect inline fun <reified T> getMultiplatformViewMode(): T