package ua.syt0r.kanji.ui.common.kanji

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun KanjiBackground(
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 2.dp,
    content: @Composable () -> Unit
) {

    Card(
        modifier
            .clip(RoundedCornerShape(8.dp))
    ) {

        Canvas(modifier = Modifier.fillMaxSize()) {

            val intervalLength = drawContext.size.maxDimension / 20
            val gapLength = drawContext.size.maxDimension / 20

            val pathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(intervalLength, gapLength),
                phase = drawContext.size.maxDimension / 40
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

        }

        content()

    }

}

@Preview(showBackground = true)
@Composable
fun KanjiBackgroundPreview() {

    Box(
        Modifier
            .size(400.dp)
            .background(Color.Red)
    ) {

        KanjiBackground(
            Modifier.size(200.dp)
        ) {

        }

    }


}