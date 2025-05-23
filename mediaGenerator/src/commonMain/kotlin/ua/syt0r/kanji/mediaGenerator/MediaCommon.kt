package ua.syt0r.kanji.mediaGenerator

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import media.Quicksand_Bold
import org.jetbrains.compose.resources.Font
import org.jetbrains.skia.ImageFilter
import ua.syt0r.kanji.presentation.common.ui.LocalOrientation
import ua.syt0r.kanji.presentation.common.ui.Orientation


@Composable
fun ScreenshotColumn(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.padding(
            vertical = 60.dp,
            horizontal = 60.dp * if (LocalOrientation.current == Orientation.Portrait) 1 else 2
        ),
        verticalArrangement = Arrangement.spacedBy(60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val fontFamily = FontFamily(Font(media.Res.font.Quicksand_Bold))

        Text(
            text = title,
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            lineHeight = 38.sp,
            textAlign = TextAlign.Center
        )

        DeviceFrame { content() }

    }
}

@Composable
fun DeviceFrame(
    content: @Composable () -> Unit
) {
    val borderRadius = 16.dp
    Box(
        modifier = Modifier
            .customShadow(borderRadius = borderRadius)
            .clip(RoundedCornerShape(borderRadius))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(borderRadius)
            )
    ) {
        content()
    }
}

fun Modifier.customShadow(
    color: Color = Color.LightGray,
    borderRadius: Dp = 0.dp,
    blurRadius: Dp = 12.dp
) = drawBehind {
    drawIntoCanvas {
        val paint = Paint()
        paint.color = color
        paint.asFrameworkPaint().imageFilter = ImageFilter.makeDropShadow(
            dx = 0f,
            dy = 0f,
            sigmaX = blurRadius.toPx(),
            sigmaY = blurRadius.toPx(),
            color = color.toArgb()
        )
        it.drawRoundRect(
            left = 0f,
            top = 0f,
            right = size.width,
            bottom = size.height,
            radiusX = borderRadius.toPx(),
            radiusY = borderRadius.toPx(),
            paint = paint
        )
    }
}