package ua.syt0r.kanji.presentation.common.ui

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

@Composable
actual fun getOrientation(): Orientation {
    return when (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT) {
        true -> Orientation.Portrait
        false -> Orientation.Landscape
    }
}