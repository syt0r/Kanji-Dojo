package ua.syt0r.kanji.presentation.screen.screen.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.presentation.screen.MainContract
import ua.syt0r.kanji.presentation.screen.screen.home.data.HomeScreenTab
import ua.syt0r.kanji.presentation.screen.screen.home.screen.dashboard.DashboardScreen
import ua.syt0r.kanji.presentation.screen.screen.home.screen.settings.SettingsScreen
import ua.syt0r.kanji.presentation.screen.screen.home.screen.writing_dashboard.WritingDashboardScreen

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
    } ?: HomeScreenTab.DASHBOARD

    content(HomeNavigation(navController), currentTab) {

        NavHost(
            navController = navController,
            startDestination = tabToRouteMapping.getValue(HomeScreenTab.DASHBOARD)
        ) {

            composable(
                route = tabToRouteMapping.getValue(HomeScreenTab.DASHBOARD),
                content = { DashboardScreen(navigation = mainNavigation) }
            )

            composable(
                route = tabToRouteMapping.getValue(HomeScreenTab.WRITING),
                content = { WritingDashboardScreen(mainNavigation) }
            )

            composable(
                route = tabToRouteMapping.getValue(HomeScreenTab.SETTINGS),
                content = { SettingsScreen(navigation = mainNavigation) }
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