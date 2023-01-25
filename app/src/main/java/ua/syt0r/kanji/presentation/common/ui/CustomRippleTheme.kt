package ua.syt0r.kanji.presentation.common.ui

import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


class CustomRippleTheme(
    private val alphaProvider: @Composable () -> RippleAlpha = {
        RippleTheme.defaultRippleAlpha(
            Color.Black,
            MaterialTheme.colors.isLight
        )
    },
    private val colorProvider: @Composable () -> Color = {
        RippleTheme.defaultRippleColor(
            contentColor = LocalContentColor.current,
            lightTheme = MaterialTheme.colors.isLight
        )
    },
) : RippleTheme {

    @Composable
    override fun defaultColor(): Color {
        return colorProvider()
    }

    @Composable
    override fun rippleAlpha(): RippleAlpha {
        return alphaProvider()
    }

}