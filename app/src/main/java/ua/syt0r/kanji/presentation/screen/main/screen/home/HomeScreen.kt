package ua.syt0r.kanji.presentation.screen.main.screen.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
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

    // To make sure rememberSaveable works in tab content (it's not by default because of
    // orientation specific changes in HomeScreenUI)
    val tabContent = remember {
        movableContentOf {
            HomeNavigationContent(
                state = homeNavigationState,
                mainNavigationState = mainNavigationState
            )
        }
    }

    HomeScreenUI(
        availableTabs = HomeScreenTab.visibleTabs,
        selectedTabState = homeNavigationState.currentTab(),
        onTabSelected = { homeNavigationState.navigate(it) }
    ) {

        tabContent()

    }

}
