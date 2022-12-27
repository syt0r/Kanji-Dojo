package ua.syt0r.kanji.presentation.screen.main.screen.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ua.syt0r.kanji.core.analytics.LocalAnalyticsManager
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.presentation.screen.main.MainContract
import ua.syt0r.kanji.presentation.screen.main.screen.home.data.HomeScreenTab
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.PracticeDashboardScreen
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings.SettingsScreen

private val tabToRouteMapping: Map<HomeScreenTab, String> = HomeScreenTab.values()
    .associateWith { it.name }

typealias DrawHomeTabContent = @Composable () -> Unit

@Composable
fun HomeNav(
    mainNavigation: MainContract.Navigation,
    content: @Composable HomeScreenContract.Navigation.(HomeScreenTab, DrawHomeTabContent) -> Unit
) {

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()

    val currentRoute = backStackEntry?.destination?.route

    val currentTab = tabToRouteMapping.firstNotNullOfOrNull { (tab, route) ->
        tab.takeIf { currentRoute == route }
    } ?: HomeScreenTab.defaultTab

    content(HomeNavigation(navController), currentTab) {

        val analyticsManager = LocalAnalyticsManager.current

        NavHost(
            navController = navController,
            startDestination = tabToRouteMapping.getValue(HomeScreenTab.defaultTab)
        ) {

            composable(
                route = tabToRouteMapping.getValue(HomeScreenTab.PRACTICE_DASHBOARD),
                content = {
                    LaunchedEffect(Unit) { analyticsManager.setScreen("practice_dashboard") }
                    PracticeDashboardScreen(mainNavigation)
                }
            )

            composable(
                route = tabToRouteMapping.getValue(HomeScreenTab.SETTINGS),
                content = {
                    LaunchedEffect(Unit) { analyticsManager.setScreen("settings") }
                    SettingsScreen(navigation = mainNavigation)
                }
            )
        }

    }

}

private class HomeNavigation(
    private val navHostController: NavHostController
) : HomeScreenContract.Navigation {

    override fun navigate(tab: HomeScreenTab) {
        navHostController.navigate(tabToRouteMapping.getValue(tab)) {
            navHostController.graph.startDestinationRoute?.let { route ->
                Logger.d("route[$route]")
                popUpTo(route) { saveState = true }
            }
            launchSingleTop = true
            restoreState = true
        }
    }

}