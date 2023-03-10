package ua.syt0r.kanji.presentation.screen.main.screen.home

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ua.syt0r.kanji.presentation.getMultiplatformViewMode
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState
import ua.syt0r.kanji.presentation.screen.main.screen.home.data.HomeScreenTab
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.PracticeDashboardScreen
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.SearchScreen

@Composable
actual fun rememberHomeNavigationState(): HomeNavigationState {
    val navController = rememberNavController()

    val backStackEntryState = navController.currentBackStackEntryAsState()
    val tabState = rememberSaveable { mutableStateOf(HomeScreenTab.Default) }

    // Caching current tab value because back stack becomes null for a moment after config change
    // and wrong selected tab is displayed
    LaunchedEffect(backStackEntryState.value) {
        backStackEntryState.value
            ?.destination
            ?.route
            ?.let { route -> HomeScreenTab.valueOf(route) }
            ?.let { tabState.value = it }
    }

    return remember { AndroidHomeNavigationState(navController, tabState) }
}

private val HomeScreenTab.route: String
    get() = this.name

private class AndroidHomeNavigationState(
    val navHostController: NavHostController,
    tabState: State<HomeScreenTab>
) : HomeNavigationState {

    override val selectedTab: State<HomeScreenTab> = tabState

    override fun navigate(tab: HomeScreenTab) {
        navHostController.navigate(tab.route) {
            popUpTo(navHostController.currentDestination!!.route!!) {
                inclusive = true
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

}

@Composable
actual fun HomeNavigationContent(
    homeNavigationState: HomeNavigationState,
    mainNavigationState: MainNavigationState
) {

    homeNavigationState as AndroidHomeNavigationState

    NavHost(
        navController = homeNavigationState.navHostController,
        startDestination = HomeScreenTab.Default.route
    ) {

        composable(
            route = HomeScreenTab.PRACTICE_DASHBOARD.route,
            content = {
                PracticeDashboardScreen(
                    mainNavigationState = mainNavigationState,
                    viewModel = getMultiplatformViewMode()
                )
            }
        )

        composable(
            route = HomeScreenTab.SEARCH.route,
            content = {
                SearchScreen(
                    mainNavigationState = mainNavigationState,
                    viewModel = getMultiplatformViewMode()
                )
            }
        )

        composable(
            route = HomeScreenTab.SETTINGS.route,
            content = {
//                SettingsScreen(mainNavigationState = mainNavigationState)
            }
        )

    }

}