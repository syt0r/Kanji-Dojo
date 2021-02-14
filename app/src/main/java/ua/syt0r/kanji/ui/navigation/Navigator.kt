package ua.syt0r.kanji.ui.navigation

import androidx.navigation.NavHostController
import androidx.navigation.compose.navigate

class Navigator(
    private val navHostController: NavHostController
) : NavigationContract.Navigator {

    override fun navigate(route: String) {
        navHostController.navigate(route)
    }

}