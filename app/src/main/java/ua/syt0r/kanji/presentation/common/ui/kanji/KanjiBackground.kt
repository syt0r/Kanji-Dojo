package ua.syt0r.kanji.presentation.common.ui.kanji

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun KanjiBackground(
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 2.dp
) {

    Canvas(modifier) {

        val intervalLength = drawContext.size.maxDimension / 40
        val gapLength = drawContext.size.maxDimension / 40

        val pathEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(intervalLength, gapLength),
            phase = intervalLength / 5
        )

        drawLine(
            color = Color.Gray,
            start = Offset(0f, drawContext.size.height / 2),
            end = Offset(drawContext.size.width, drawContext.size.height / 2),
            strokeWidth = strokeWidth.toPx(),
            pathEffect = pathEffect
        )

        drawLine(
            color = Color.Gray,
            start = Offset(drawContext.size.width / 2, 0f),
            end = Offset(drawContext.size.width / 2, drawContext.size.height),
            strokeWidth = strokeWidth.toPx(),
            pathEffect = pathEffect
        )

        val secondaryStrokeWidth = strokeWidth.toPx() / 4

        drawLine(
            color = Color.Gray,
            start = Offset(0f, drawContext.size.height / 4),
            end = Offset(drawContext.size.width, drawContext.size.height / 4),
            strokeWidth = secondaryStrokeWidth,
            pathEffect = pathEffect
        )

        drawLine(
            color = Color.Gray,
            start = Offset(0f, drawContext.size.height / 4 * 3),
            end = Offset(drawContext.size.width, drawContext.size.height / 4 * 3),
            strokeWidth = secondaryStrokeWidth,
            pathEffect = pathEffect
        )

        drawLine(
            color = Color.Gray,
            start = Offset(drawContext.size.width / 4, 0f),
            end = Offset(drawContext.size.width / 4, drawContext.size.height),
            strokeWidth = secondaryStrokeWidth,
            pathEffect = pathEffect
        )

        drawLine(
            color = Color.Gray,
            start = Offset(drawContext.size.width / 4 * 3, 0f),
            end = Offset(drawContext.size.width / 4 * 3, drawContext.size.height),
            strokeWidth = secondaryStrokeWidth,
            pathEffect = pathEffect
        )

    }

}

@Preview(showBackground = true)
@Composable
fun KanjiBackgroundPreview() {

    Box(
        Modifier
            .size(400.dp)
            .background(Color.Black)
    ) {

        KanjiBackground(
            Modifier
                .size(200.dp)
                .align(Alignment.Center)
                .background(Color.White)
        )

    }


}