package ua.syt0r.kanji.presentation.common.navigation

import androidx.compose.runtime.Composable

interface NavigationContract {

    interface Host {

        @Composable
        fun DrawContent()

    }

}