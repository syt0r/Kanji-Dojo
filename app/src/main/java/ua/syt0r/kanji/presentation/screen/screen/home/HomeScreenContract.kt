package ua.syt0r.kanji.presentation.screen.screen.home

import ua.syt0r.kanji.presentation.screen.screen.home.data.HomeScreenTab

interface HomeScreenContract {

    interface ViewModel

    interface Navigation {
        fun navigate(tab: HomeScreenTab)
    }

}