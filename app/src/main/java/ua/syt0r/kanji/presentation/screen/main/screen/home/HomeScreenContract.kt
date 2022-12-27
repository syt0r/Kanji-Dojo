package ua.syt0r.kanji.presentation.screen.main.screen.home

import ua.syt0r.kanji.presentation.screen.main.screen.home.data.HomeScreenTab

interface HomeScreenContract {

    interface ViewModel

    interface Navigation {
        fun navigate(tab: HomeScreenTab)
    }

}