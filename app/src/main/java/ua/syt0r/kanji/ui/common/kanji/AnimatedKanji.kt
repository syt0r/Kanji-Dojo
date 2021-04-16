package ua.syt0r.kanji.ui.common.kanji

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.core.svg.SvgPathCreator
import ua.syt0r.svg.SvgCommandParser

@Composable
fun AnimatedKanji(
    modifier: Modifier = Modifier,
    animateStroke: Int = 0,
    strokes: List<Path>
) {

    val drawStrokes = strokes.subList(0, animateStroke)

    val animatedPath = Path()
    val pathMeasure = PathMeasure()

    pathMeasure.setPath(strokes[animateStroke], false)

    val value by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring()
    )

    Canvas(modifier) {
        Log.d("123", "value[$value]")
        val length = value * pathMeasure.length
        pathMeasure.getSegment(0f, length, animatedPath)


        val (width, height) = drawContext.size.run { width to height }

        scale(width / kanjiSize, height / kanjiSize, Offset.Zero) {
            drawStrokes.forEach {
                drawPath(
                    path = it,
                    color = Color.Black,
                    style = Stroke(
                        width = 3f,
                        cap = StrokeCap.Round
                    )
                )
            }
            drawPath(
                path = animatedPath,
                color = Color.Black,
                style = Stroke(
                    width = 3f,
                    cap = StrokeCap.Round
                )
            )
        }

    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AnimatedKanjiPreview() {
    val strokes = listOf(
        "M23.78,19c0.58,0.35,1.93,2.58,1.93,3.28c0,7.24,0.24,37.86,0.24,47.16",
        "M13.33,46.23c0.64,0.38,1.33,1.99,1.28,2.76c-0.36,5.3-0.36,13.37-1.63,21.34c-0.28,1.73,0.19,2.13,1.75,1.74c8.88-2.22,12.96-2.7,23.76-4.9",
        "M38.72,42.05c0.64,0.38,1.28,2.84,1.28,3.61c0,5.96-1.38,16.48-1.38,26.13",

        "M49.67,16.18c0.28,0.53,0.78,1.89,0.93,2.54c1.23,5.38,2.27,18.05,2.98,27.93",
        "M50.97,17.51c9.81-0.34,30.08-2.62,37.41-2.99c2.69-0.14,3.29,1.55,3.07,4.8c-0.36,5.28-1.65,14.9-3.26,25.19",
        "M52.83,30.92c3.2-0.19,34.5-2.6,37.14-2.55",
        "M53.73,44.61c10.03-1.09,24.8-2.33,34.56-2.27"
    ).map { SvgCommandParser.parse(it) }.map { SvgPathCreator.convert(it) }

    AnimatedKanji(
        modifier = Modifier
            .size(200.dp)
            .background(Color.Green),
        animateStroke = 1,
        strokes = strokes
    )
}