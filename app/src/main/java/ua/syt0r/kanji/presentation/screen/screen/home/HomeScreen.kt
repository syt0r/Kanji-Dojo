package ua.syt0r.kanji.presentation.screen.screen.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import ua.syt0r.kanji.presentation.screen.MainContract
import ua.syt0r.kanji.presentation.screen.screen.home.data.HomeScreenTab
import ua.syt0r.kanji.presentation.screen.screen.home.ui.HomeScreenUI

@Composable
fun HomeScreen(
    viewModel: HomeScreenContract.ViewModel = viewModel<HomeViewModel>(),
    mainNavigation: MainContract.Navigation
) {

    val navController = rememberNavController()
    val homeNavigation = HomeNavigation(navController, mainNavigation)

    val visibleTabs = listOf<HomeScreenTab>(
        HomeScreenTab.DASHBOARD,
        HomeScreenTab.SETTINGS
    )

    // Using rememberSaveable to survive navigation changes
    val selectedTab = rememberSaveable { mutableStateOf(HomeScreenTab.DASHBOARD) }

    HomeScreenUI(
        tabs = visibleTabs,
        initialSelectedTab = selectedTab,
        onTabSelected = {
            selectedTab.value = it
            it.navigate(homeNavigation)
        }
    ) {

        homeNavigation.DrawContent()

    }

}
