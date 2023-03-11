package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings

import androidx.compose.runtime.State


interface SettingsScreenContract {

    interface ViewModel {

        val state: State<ScreenState>

        fun refresh()
        fun updateNoTranslationLayout(enabled: Boolean)
        fun updateAnalyticsEnabled(enabled: Boolean)

        fun reportScreenShown()

    }

    sealed class ScreenState {
        object Loading : ScreenState()
        data class Loaded(
            val analyticsEnabled: Boolean,
            val noTranslationLayoutEnabled: Boolean
        ) : ScreenState()
    }

}