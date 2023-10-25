package ua.syt0r.kanji.presentation.common.ui

import androidx.compose.runtime.Composable
import ua.syt0r.kanji.LocalWindowState


@Composable
actual fun getOrientation(): Orientation {
    return when (LocalWindowState.current.size.run { height > width }) {
        true -> Orientation.Portrait
        false -> Orientation.Landscape
    }
}