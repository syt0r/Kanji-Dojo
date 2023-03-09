package ua.syt0r.kanji.presentation.screen.main.screen.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState
import ua.syt0r.kanji.presentation.screen.main.screen.home.data.HomeScreenTab

interface NewHomeNavigationState {
    val selectedTab: State<HomeScreenTab>
    fun navigate(tab: HomeScreenTab)
}

@Composable
expect fun rememberNewHomeNavigationState(): NewHomeNavigationState

@Composable
expect fun NewHomeNavigationContent(
    homeNavigationState: NewHomeNavigationState,
    mainNavigationState: MainNavigationState
)
