package ua.syt0r.kanji.presentation.screen.main.screen.home

import kotlinx.coroutines.flow.StateFlow

interface HomeScreenContract {

    interface ViewModel {
        val defaultTab: HomeScreenTab
        val syncIconState: StateFlow<SyncIconState>
        fun trySync(): Boolean
    }

}