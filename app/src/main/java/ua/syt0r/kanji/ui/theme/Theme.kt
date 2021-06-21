package ua.syt0r.kanji.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Color.Black,
    onPrimary = Color.White,
    primaryVariant = secondaryDark,
    secondary = secondary,
    onSecondary = onSecondary
)

private val LightColorPalette = lightColors(
    primary = primary,
    onPrimary = onPrimary,
    primaryVariant = primaryDark,
    secondary = secondary,
    onSecondary = onSecondary
)

@Composable
fun KanjiDojoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}