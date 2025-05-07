package ua.syt0r.kanji.presentation.common.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import ua.syt0r.kanji.presentation.common.theme.Dimens

@Composable
fun VerticalScrollbar(
    scrollState: ScrollState,
    modifier: Modifier,
    scrollBarWidth: Dp = Dimens.SpacingSmall,
) {

    val scrollBarColor: Color = MaterialTheme.colorScheme.surfaceVariant

    Canvas(
        modifier = modifier
            .padding(horizontal = Dimens.SpacingSmall)
            .width(scrollBarWidth)
    ) {
        drawScrollbar(scrollState, scrollBarColor, scrollBarWidth)
    }

}

fun DrawScope.drawScrollbar(
    scrollState: ScrollState,
    color: Color,
    scrollBarWidth: Dp,
    alpha: Float = 1f
) {

    val needDrawScrollbar = scrollState.maxValue != Int.MAX_VALUE
            && scrollState.maxValue > 0

    if (needDrawScrollbar) {

        val totalContentHeightPx = scrollState.maxValue + scrollState.viewportSize
        val visibleAreaEndPx = scrollState.value.toFloat() + scrollState.viewportSize
        val startHeightFraction = scrollState.value.toFloat() / totalContentHeightPx
        val endHeightFraction = visibleAreaEndPx / totalContentHeightPx

        drawRoundRect(
            color = color,
            topLeft = Offset(
                x = size.width - scrollBarWidth.toPx(),
                y = startHeightFraction * size.height
            ),
            size = Size(
                width = scrollBarWidth.toPx(),
                height = (endHeightFraction - startHeightFraction) * size.height
            ),
            alpha = alpha,
            cornerRadius = CornerRadius(Dimens.SpacingTiny.toPx())
        )
    }

}
