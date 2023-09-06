package ua.syt0r.kanji.core

import androidx.appcompat.app.AppCompatDelegate
import ua.syt0r.kanji.core.theme_manager.ThemeManager
import ua.syt0r.kanji.core.user_data.model.SupportedTheme

class AndroidThemeManager(
    getTheme: () -> SupportedTheme?,
    setTheme: (SupportedTheme) -> Unit
) : ThemeManager(getTheme, setTheme) {

    init {
        applyThemeToActivity()
    }

    override fun changeTheme(theme: SupportedTheme) {
        super.changeTheme(theme)
        applyThemeToActivity()
    }

    private fun applyThemeToActivity() {
        AppCompatDelegate.setDefaultNightMode(currentTheme.value.toUIMode())
    }

    private fun SupportedTheme.toUIMode(): Int {
        return when (this) {
            SupportedTheme.System -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            SupportedTheme.Light -> AppCompatDelegate.MODE_NIGHT_NO
            SupportedTheme.Dark -> AppCompatDelegate.MODE_NIGHT_YES
        }
    }

}