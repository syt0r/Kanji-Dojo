package ua.syt0r.kanji.presentation.screen.main.screen.home

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState
import ua.syt0r.kanji.presentation.screen.main.screen.home.data.HomeScreenTab
import ua.syt0r.kanji.presentation.screen.main.screen.home.ui.HomeScreenUI

@Composable
fun HomeScreen(
    mainNavigationState: MainNavigationState,
    viewModel: HomeScreenContract.ViewModel = viewModel<HomeViewModel>()
) {

    val homeNavigationState = rememberHomeNavigationState()

    HomeScreenUI(
        availableTabs = HomeScreenTab.visibleTabs,
        selectedTabState = homeNavigationState.currentTab(),
        onTabSelected = { homeNavigationState.navigate(it) }
    ) {

        HomeNavigationContent(
            state = homeNavigationState,
            mainNavigationState = mainNavigationState
        )

    }

}
