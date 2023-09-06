package ua.syt0r.kanji.core.theme_manager

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import ua.syt0r.kanji.core.user_data.model.SupportedTheme


open class ThemeManager(
    val getTheme: () -> SupportedTheme?,
    val setTheme: (SupportedTheme) -> Unit
) {

    private val mCurrentTheme = mutableStateOf(value = getTheme() ?: SupportedTheme.System)

    val currentTheme: State<SupportedTheme> = mCurrentTheme

    open fun changeTheme(theme: SupportedTheme) {
        mCurrentTheme.value = theme
        setTheme(theme)
    }

    val isDarkTheme: Boolean
        @Composable
        get() = when (currentTheme.value) {
            SupportedTheme.System -> isSystemInDarkTheme()
            SupportedTheme.Light -> false
            SupportedTheme.Dark -> true
        }

}

val LocalThemeManager = compositionLocalOf {
    // Dummy for previews, use instance from koin
    ThemeManager(getTheme = { null }, setTheme = {})
}