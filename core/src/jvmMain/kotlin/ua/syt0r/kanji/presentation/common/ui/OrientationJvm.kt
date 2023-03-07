package ua.syt0r.kanji.presentation.common.ui

import androidx.compose.runtime.Composable

@Composable
actual fun getOrientation(): Orientation {
    return Orientation.Landscape // TODO check window aspect ratio
}