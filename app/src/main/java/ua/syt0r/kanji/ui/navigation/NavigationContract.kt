package ua.syt0r.kanji.ui.navigation

import androidx.compose.runtime.Composable

interface NavigationContract {

    interface Host {

        @Composable
        fun setup()

    }

}