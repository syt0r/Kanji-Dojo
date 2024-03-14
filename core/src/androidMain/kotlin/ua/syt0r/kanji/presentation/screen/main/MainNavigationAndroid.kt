package ua.syt0r.kanji.presentation.screen.main

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.presentation.getMultiplatformViewModel
import ua.syt0r.kanji.presentation.screen.main.screen.about.AboutScreen
import ua.syt0r.kanji.presentation.screen.main.screen.backup.BackupScreen
import ua.syt0r.kanji.presentation.screen.main.screen.feedback.FeedbackScreen
import ua.syt0r.kanji.presentation.screen.main.screen.home.HomeScreen
import ua.syt0r.kanji.presentation.screen.main.screen.home.rememberHomeNavigationState
import ua.syt0r.kanji.presentation.screen.main.screen.kanji_info.KanjiInfoScreen
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.PracticeCreateScreen
import ua.syt0r.kanji.presentation.screen.main.screen.practice_import.PracticeImportScreen
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.PracticePreviewScreen
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.ReadingPracticeScreen
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.WritingPracticeScreen
import kotlin.reflect.KClass

@Composable
actual fun rememberMainNavigationState(): MainNavigationState {
    val navController = rememberNavController()
    return remember { AndroidMainNavigationState(navController) }
}

private val <T : MainDestination> KClass<T>.route: String
    get() = this.simpleName!!

private const val MainDestinationArgumentKey = "arg"

private val <T : MainDestination> KClass<T>.routeWithArg: String
    get() = "$route/{$MainDestinationArgumentKey}"

private val MainDestinationNavArgument = navArgument(MainDestinationArgumentKey) {
    type = NavType.StringType
}

private fun MainDestination.asSerializedArgument(): String {
    return Uri.encode(Json.encodeToString(MainDestination.serializer(), this))
}

private inline fun <reified T : MainDestination> NavBackStackEntry.deserializeDestination(): T {
    return arguments!!.getString(MainDestinationArgumentKey)!!
        .let { Uri.decode(it) }
        .let {
            Logger.d("decodingJson[$it]")
            Json.decodeFromString<MainDestination>(it) as T
        }
}

@Composable
actual fun MainNavigation(state: MainNavigationState) {
    state as AndroidMainNavigationState

    NavHost(
        navController = state.navHostController,
        startDestination = MainDestination.Home::class.route
    ) {

        composable(
            route = MainDestination.Home::class.route,
            content = {
                val homeNavigationState = rememberHomeNavigationState()
                HomeScreen(
                    viewModel = getMultiplatformViewModel(),
                    mainNavigationState = rememberUpdatedState(state),
                    homeNavigationState = homeNavigationState
                )
            }
        )

        composable(
            route = MainDestination.About::class.route,
            content = {
                AboutScreen(
                    mainNavigationState = state,
                    viewModel = getMultiplatformViewModel()
                )
            }
        )

        composable(
            route = MainDestination.Practice.Writing::class.routeWithArg,
            arguments = listOf(MainDestinationNavArgument),
            content = {
                val destination = it.deserializeDestination<MainDestination.Practice.Writing>()
                WritingPracticeScreen(
                    configuration = destination,
                    mainNavigationState = state,
                    viewModel = getMultiplatformViewModel()
                )
            }
        )

        composable(
            route = MainDestination.Practice.Reading::class.routeWithArg,
            content = {
                val configuration: MainDestination.Practice.Reading = it.deserializeDestination()
                ReadingPracticeScreen(
                    navigationState = state,
                    configuration = configuration,
                    viewModel = getMultiplatformViewModel()
                )
            }
        )

        composable(
            route = MainDestination.CreatePractice::class.routeWithArg,
            content = {
                val configuration: MainDestination.CreatePractice = it.deserializeDestination()
                PracticeCreateScreen(configuration, state, getMultiplatformViewModel())
            }
        )

        composable(
            route = MainDestination.ImportPractice::class.route,
            content = {
                PracticeImportScreen(state, getMultiplatformViewModel())
            }
        )

        composable(
            route = MainDestination.PracticePreview::class.routeWithArg,
            arguments = listOf(MainDestinationNavArgument),
            content = {
                val destination = it.deserializeDestination<MainDestination.PracticePreview>()
                PracticePreviewScreen(destination.id, state, getMultiplatformViewModel())
            }
        )

        composable(
            route = MainDestination.KanjiInfo::class.routeWithArg,
            arguments = listOf(MainDestinationNavArgument),
            content = {
                val destination = it.deserializeDestination<MainDestination.KanjiInfo>()
                KanjiInfoScreen(
                    kanji = destination.character,
                    mainNavigationState = state,
                    viewModel = getMultiplatformViewModel()
                )
            }
        )

        composable(
            route = MainDestination.Backup::class.route,
            content = {
                BackupScreen(
                    mainNavigationState = state
                )
            }
        )

        composable(
            route = MainDestination.Feedback::class.routeWithArg,
            arguments = listOf(MainDestinationNavArgument),
            content = {
                FeedbackScreen(mainNavigationState = state)
            }
        )

    }
}

@Immutable
private class AndroidMainNavigationState(
    val navHostController: NavHostController
) : MainNavigationState {

    override fun navigateBack() {
        navHostController.navigateUp()
    }

    override fun popUpToHome() {
        navHostController.popBackStack(MainDestination.Home::class.route, false)
    }

    override fun navigate(destination: MainDestination) {
        val route = when (destination) {
            is MainDestination.Practice.Reading,
            is MainDestination.KanjiInfo,
            is MainDestination.PracticePreview,
            is MainDestination.Practice.Writing,
            is MainDestination.Feedback -> {
                "${destination::class.route}/${destination.asSerializedArgument()}"
            }

            is MainDestination.CreatePractice -> {
                "${MainDestination.CreatePractice::class.route}/${destination.asSerializedArgument()}"
            }

            MainDestination.About,
            MainDestination.Home,
            MainDestination.ImportPractice,
            MainDestination.Backup -> destination::class.route
        }
        Logger.d("navigatingToRoute[$route]")
        navHostController.navigate(route)
    }

}