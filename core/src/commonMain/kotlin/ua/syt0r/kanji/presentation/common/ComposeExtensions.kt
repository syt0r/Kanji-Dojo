package ua.syt0r.kanji.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.findRootCoordinates
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.onHeightFromScreenBottomFound(receiver: (Dp) -> Unit): Modifier {
    val density = LocalDensity.current.density
    return onPlaced {
        val screenHeightPx = it.findRootCoordinates().size.height
        val fabTopHeightPx = it.boundsInRoot().top
        val heightDp = (screenHeightPx - fabTopHeightPx) / density
        receiver(heightDp.dp)
    }
}