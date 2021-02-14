package ua.syt0r.kanji.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NamedNavArgument
import androidx.navigation.compose.composable

abstract class NavDestination {

    abstract val route: String
    protected open val arguments: List<NamedNavArgument> = emptyList()

    protected abstract val contentProvider: @Composable (NavBackStackEntry) -> Unit

    open fun setupRoute(builder: NavGraphBuilder) {
        builder.composable(
            route = route,
            arguments = arguments,
            content = { contentProvider.invoke(it) }
        )
    }

    open fun createRoute(): String = route

}