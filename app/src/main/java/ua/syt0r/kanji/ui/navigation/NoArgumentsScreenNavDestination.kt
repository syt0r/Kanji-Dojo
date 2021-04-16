package ua.syt0r.kanji.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate

abstract class NoArgumentsScreenNavDestination {

    abstract val routeName: String

    @Composable
    abstract fun ShowScreen()

    fun setup(navGraphBuilder: NavGraphBuilder) {
        navGraphBuilder.composable(
            route = routeName,
            content = { ShowScreen() }
        )
    }

    fun navigate(navHostController: NavHostController) {
        navHostController.navigate(routeName)
    }


}