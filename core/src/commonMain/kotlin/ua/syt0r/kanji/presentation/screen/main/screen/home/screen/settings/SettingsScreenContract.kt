package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings

import androidx.compose.runtime.Composable
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState


interface SettingsScreenContract {

    interface Content {

        @Composable
        fun Draw(mainNavigationState: MainNavigationState)

    }

}