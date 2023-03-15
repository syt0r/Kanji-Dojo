package ua.syt0r.kanji.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.findRootCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ua.syt0r.kanji.common.CharactersClassification
import ua.syt0r.kanji.presentation.common.resources.string.resolveString

data class ItemHeightData(
    val density: Density,
    val layoutCoordinates: LayoutCoordinates,
    val heightFromScreenBottom: Dp
)

@Composable
fun Modifier.trackScreenHeight(
    receiver: (ItemHeightData) -> Unit
): Modifier {
    val density = LocalDensity.current
    return onGloballyPositioned {
        val screenHeightPx = it.findRootCoordinates().size.height
        val fabTopHeightPx = it.boundsInRoot().top
        val heightDp = (screenHeightPx - fabTopHeightPx) / density.density
        receiver(ItemHeightData(density, it, heightDp.dp))
    }
}

inline fun <reified T> jsonSaver() = Saver<T, String>(
    save = { Json.encodeToString(it) },
    restore = { Json.decodeFromString(it) }
)

@Composable
fun CharactersClassification.Kana.resolveString(): String {
    val kana = this
    return resolveString {
        when (kana) {
            CharactersClassification.Kana.Hiragana -> hiragana
            CharactersClassification.Kana.Katakana -> katakana
        }
    }
}