package ua.syt0r.kanji.core

import androidx.appcompat.app.AppCompatDelegate
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import ua.syt0r.kanji.core.theme_manager.ThemeManager
import ua.syt0r.kanji.core.user_data.preferences.PreferencesContract.AppPreferences
import ua.syt0r.kanji.core.user_data.preferences.PreferencesTheme

class AndroidThemeManager(
    appPreferences: AppPreferences,
    dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ThemeManager(appPreferences, dispatcher) {

    override fun platformInvalidate(theme: PreferencesTheme) {
        AppCompatDelegate.setDefaultNightMode(theme.toUIMode())
    }

    private fun PreferencesTheme.toUIMode(): Int {
        return when (this) {
            PreferencesTheme.System -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            PreferencesTheme.Light -> AppCompatDelegate.MODE_NIGHT_NO
            PreferencesTheme.Dark -> AppCompatDelegate.MODE_NIGHT_YES
        }
    }

}