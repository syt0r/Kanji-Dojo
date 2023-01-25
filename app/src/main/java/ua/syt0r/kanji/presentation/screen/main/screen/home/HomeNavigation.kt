package ua.syt0r.kanji.presentation.screen.main.screen.home

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ua.syt0r.kanji.core.analytics.LocalAnalyticsManager
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState
import ua.syt0r.kanji.presentation.screen.main.screen.home.data.HomeScreenTab
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.PracticeDashboardScreen
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.SearchScreen
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings.SettingsScreen

interface HomeNavigationState {
    fun currentTab(): State<HomeScreenTab>
    fun navigate(tab: HomeScreenTab)
}

@Composable
fun rememberHomeNavigationState(): HomeNavigationState {
    val navController = rememberNavController()

    val backStackEntryState = navController.currentBackStackEntryAsState()
    val tabState = remember {
        derivedStateOf {
            backStackEntryState.value
                ?.destination
                ?.route
                ?.let { route ->
                    HomeTabToNavRouteMapping.entries.find { it.value == route }
                }
                ?.key
                ?: HomeScreenTab.defaultTab
        }
    }

    return remember { HomeNavigationStateImpl(navController, tabState) }
}

private val HomeTabToNavRouteMapping: Map<HomeScreenTab, String> = HomeScreenTab.values()
    .associateWith { it.name }

private class HomeNavigationStateImpl(
    val navHostController: NavHostController,
    private val tabState: State<HomeScreenTab>
) : HomeNavigationState {

    override fun currentTab(): State<HomeScreenTab> = tabState

    override fun navigate(tab: HomeScreenTab) {
        navHostController.navigate(HomeTabToNavRouteMapping.getValue(tab)) {
            navHostController.graph.startDestinationRoute?.let { route ->
                Logger.d("route[$route]")
                popUpTo(route) { saveState = true }
            }
            launchSingleTop = true
            restoreState = true
        }
    }

}

@Composable
fun HomeNavigationContent(
    state: HomeNavigationState,
    mainNavigationState: MainNavigationState,
) {

    val analyticsManager = LocalAnalyticsManager.current

    NavHost(
        navController = (state as HomeNavigationStateImpl).navHostController,
        startDestination = HomeTabToNavRouteMapping.getValue(HomeScreenTab.defaultTab)
    ) {

        composable(
            route = HomeTabToNavRouteMapping.getValue(HomeScreenTab.PRACTICE_DASHBOARD),
            content = {
                LaunchedEffect(Unit) { analyticsManager.setScreen("practice_dashboard") }
                PracticeDashboardScreen(mainNavigationState)
            }
        )

        composable(
            route = HomeTabToNavRouteMapping.getValue(HomeScreenTab.SEARCH),
            content = {
                LaunchedEffect(Unit) { analyticsManager.setScreen("search") }
                SearchScreen(mainNavigationState)
            }
        )

        composable(
            route = HomeTabToNavRouteMapping.getValue(HomeScreenTab.SETTINGS),
            content = {
                LaunchedEffect(Unit) { analyticsManager.setScreen("settings") }
                SettingsScreen(mainNavigationState = mainNavigationState)
            }
        )
    }

}
