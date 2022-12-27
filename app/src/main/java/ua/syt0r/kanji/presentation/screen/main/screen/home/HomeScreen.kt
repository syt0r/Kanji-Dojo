package ua.syt0r.kanji.presentation.screen.main.screen.home

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import ua.syt0r.kanji.presentation.screen.main.MainContract
import ua.syt0r.kanji.presentation.screen.main.screen.home.data.HomeScreenTab
import ua.syt0r.kanji.presentation.screen.main.screen.home.ui.HomeScreenUI

@Composable
fun HomeScreen(
    mainNavigation: MainContract.Navigation,
    viewModel: HomeScreenContract.ViewModel = viewModel<HomeViewModel>()
) {

    HomeNav(mainNavigation) { currentTab, tabContentComposable ->

        HomeScreenUI(
            tabs = HomeScreenTab.visibleTabs,
            selectedTab = currentTab,
            onTabSelected = { tab -> navigate(tab) }
        ) {

            tabContentComposable()

        }

    }

}
