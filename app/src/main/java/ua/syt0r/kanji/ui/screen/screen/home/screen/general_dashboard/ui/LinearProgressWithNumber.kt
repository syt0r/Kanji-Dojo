package ua.syt0r.kanji.ui.screen.screen.home.screen.general_dashboard.ui

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.min
import kotlin.math.roundToInt

@Preview(showBackground = true)
@Composable
fun LinearProgressWithNumberPreview() {

    Box(
        Modifier
            .width(200.dp)
            .height(IntrinsicSize.Max)
            .background(Color.White)
    ) {
        LinearProgressWithNumber(
            progress = 0.5f,
            Modifier.fillMaxSize()
        )
    }

}

@Composable
fun LinearProgressWithNumber(
    progress: Float,
    modifier: Modifier = Modifier,
    primaryProgressColor: Color = Color.Red,
    secondaryProgressColor: Color = Color.LightGray
) {

    val primaryProgressBrush = SolidColor(primaryProgressColor)
    val secondaryProgressBrush = SolidColor(secondaryProgressColor)

    val text = (progress * 100).roundToInt().toString() + "%"

    Canvas(modifier) {

        val strokeWidth = 3.dp.toPx()
        val textSize = 12.sp.toPx()

        val textPaint = Paint()
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.isAntiAlias = true
        textPaint.color = Color.Black.toArgb()
        textPaint.textSize = textSize
        textPaint.typeface = Typeface.DEFAULT_BOLD

        val textWidth = textPaint.measureText(text)
        val textPadding = 8.dp.toPx()

        drawLine(
            brush = secondaryProgressBrush,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width / 2 - textWidth / 2 - textPadding, size.height / 2),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            brush = secondaryProgressBrush,
            start = Offset(size.width / 2 + textWidth / 2 + textPadding, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        drawIntoCanvas {
            it.nativeCanvas.drawText(
                text,
                size.width / 2,
                size.height / 2 - ((textPaint.descent() + textPaint.ascent()) / 2),
                textPaint
            )
        }

        val progressX = size.width * progress
        val startProgressMaxX = size.width / 2 - textWidth / 2 - textPadding

        drawLine(
            brush = primaryProgressBrush,
            start = Offset(0f, size.height / 2),
            end = Offset(
                min(startProgressMaxX, progressX),
                size.height / 2
            ),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )


        val endProgressMinX = size.width / 2 + textWidth / 2 + textPadding

        if (endProgressMinX < progressX) {
            drawLine(
                brush = primaryProgressBrush,
                start = Offset(endProgressMinX, size.height / 2),
                end = Offset(progressX, size.height / 2),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }

    }

}