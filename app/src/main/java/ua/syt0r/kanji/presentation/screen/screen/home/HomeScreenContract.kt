package ua.syt0r.kanji.presentation.screen.screen.home

interface HomeScreenContract {

    interface ViewModel

    interface Navigation {
        fun navigateToDashboard()
        fun navigateToSettings()
    }

}