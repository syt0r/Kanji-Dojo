package ua.syt0r.kanji.presentation.screen.main.screen.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import ua.syt0r.kanji.presentation.getMultiplatformViewModel
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState
import ua.syt0r.kanji.presentation.screen.main.screen.home.data.HomeScreenTab
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.PracticeDashboardScreen
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.PracticeDashboardScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.SearchScreen
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.SearchScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings.SettingsScreen

@Composable
actual fun rememberHomeNavigationState(): HomeNavigationState {
    val tabState = rememberSaveable { mutableStateOf<HomeScreenTab>(HomeScreenTab.Default) }
    return rememberSaveable { MultiplatformHomeNavigationState(tabState) }
}

class MultiplatformHomeNavigationState(
    override val selectedTab: MutableState<HomeScreenTab>
) : HomeNavigationState {
    override fun navigate(tab: HomeScreenTab) {
        selectedTab.value = tab
    }
}

@Composable
actual fun HomeNavigationContent(
    homeNavigationState: HomeNavigationState,
    mainNavigationState: MainNavigationState
) {
    homeNavigationState as MultiplatformHomeNavigationState
    when (homeNavigationState.selectedTab.value) {
        HomeScreenTab.PRACTICE_DASHBOARD -> {
            val viewModel = getMultiplatformViewModel<PracticeDashboardScreenContract.ViewModel>()
            PracticeDashboardScreen(mainNavigationState, viewModel)
        }

        HomeScreenTab.SEARCH -> {
            val viewModel = getMultiplatformViewModel<SearchScreenContract.ViewModel>()
            SearchScreen(mainNavigationState, viewModel)
        }

        HomeScreenTab.SETTINGS -> {
            SettingsScreen(
                viewModel = getMultiplatformViewModel(),
                mainNavigationState = mainNavigationState
            )
        }
    }
}