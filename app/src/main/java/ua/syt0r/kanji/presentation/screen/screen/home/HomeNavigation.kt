package ua.syt0r.kanji.presentation.screen.screen.home

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ua.syt0r.kanji.presentation.common.navigation.NavigationContract
import ua.syt0r.kanji.presentation.screen.MainContract
import ua.syt0r.kanji.presentation.screen.screen.home.screen.dashboard.DashboardScreen
import ua.syt0r.kanji.presentation.screen.screen.home.screen.settings.SettingsScreen


class HomeNavigation(
    private val navHostController: NavHostController,
    private val mainNavigation: MainContract.Navigation
) : NavigationContract.Host, HomeScreenContract.Navigation {

    companion object {
        private const val DASHBOARD_ROUTE = "dashboard"
        private const val SETTINGS_ROUTE = "settings"
    }

    @Composable
    override fun DrawContent() {

        NavHost(
            navController = navHostController,
            startDestination = DASHBOARD_ROUTE
        ) {

            composable(
                route = DASHBOARD_ROUTE,
                content = { DashboardScreen(navigation = mainNavigation) }
            )

            composable(
                route = SETTINGS_ROUTE,
                content = { SettingsScreen(navigation = mainNavigation) }
            )
        }

    }

    override fun navigateToDashboard() {
        navHostController.navigate(DASHBOARD_ROUTE)
    }

    override fun navigateToSettings() {
        navHostController.navigate(SETTINGS_ROUTE)
    }

}