package ua.syt0r.kanji.presentation.common.ui.kanji

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import ua.syt0r.kanji.core.kanji_data.data.CharacterRadical

private const val ColorsInPalette = 10
private val StrokeColorPalette: List<Color> = (0 until ColorsInPalette)
    .asSequence()
    .map {
        val hue = 360f / ColorsInPalette * it
        Color.hsv(hue, 0.67f, 0.9f)
    }
    .toList()

@Composable
fun RadicalKanji(
    strokes: List<Path>,
    radicals: List<CharacterRadical>,
    modifier: Modifier = Modifier
) {

    val strokeIndexToRadicals: Map<Int, CharacterRadical?> = remember {
        strokes.indices.associateWith { strokeIndex ->
            radicals
                .filter {
                    it.startPosition <= strokeIndex &&
                            strokeIndex < it.startPosition + it.strokesCount
                }
                .maxByOrNull { it.strokesCount }
        }
    }

    val strokeToColors = remember {

        var colorIndex = 0

        strokeIndexToRadicals.map { (index, radical) ->
            val color = StrokeColorPalette[colorIndex % StrokeColorPalette.size]

            if (strokeIndexToRadicals[index + 1].let { it != radical || it == null }) {
                colorIndex++
            }

            strokes[index] to color
        }

    }

    Box(modifier = modifier) {
        strokeToColors.forEach { (path, color) ->
            Stroke(
                path = path,
                modifier = Modifier.fillMaxSize(),
                color = color
            )
        }
    }

}

//@Preview
//@Composable
//private fun Preview(darkTheme: Boolean = false) {
//    AppTheme(darkTheme) {
//        Surface {
//            RadicalKanji(
//                strokes = PreviewKanji.strokes,
//                radicals = PreviewKanji.radicals,
//                modifier = Modifier
//                    .padding(60.dp)
//                    .size(80.dp)
//            )
//        }
//    }
//}
//
//@Preview
//@Composable
//private fun DarkPreview() {
//    Preview(darkTheme = true)
//}
