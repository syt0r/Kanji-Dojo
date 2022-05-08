package ua.syt0r.kanji.presentation.screen.screen.home.screen.settings

import androidx.compose.runtime.State


interface SettingsScreenContract {

    interface ViewModel {
        val state: State<ScreenState>
        fun updateAnalyticsEnabled(enabled: Boolean)
    }

    sealed class ScreenState {
        object Loading : ScreenState()
        data class Loaded(
            val analyticsEnabled: Boolean
        ) : ScreenState()
    }

}