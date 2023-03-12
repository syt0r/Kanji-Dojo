package ua.syt0r.kanji.presentation.screen.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ua.syt0r.kanji.presentation.getMultiplatformViewModel
import ua.syt0r.kanji.presentation.screen.main.screen.about.AboutScreen
import ua.syt0r.kanji.presentation.screen.main.screen.home.HomeScreen
import ua.syt0r.kanji.presentation.screen.main.screen.home.rememberHomeNavigationState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.PracticeCreateScreen
import ua.syt0r.kanji.presentation.screen.main.screen.practice_import.PracticeImportScreen
import kotlin.reflect.KClass

@Composable
actual fun rememberMainNavigationState(): MainNavigationState {
    val navController = rememberNavController()
    val persistentPracticeDestination = rememberSaveable {
        mutableStateOf<MainDestination.Practice?>(null)
    }
    val persistentCreatePracticeDestination = rememberSaveable {
        mutableStateOf<MainDestination.CreatePractice?>(null)
    }
    return AndroidMainNavigationState(
        navController,
        persistentPracticeDestination,
        persistentCreatePracticeDestination
    )
}

private val <T : MainDestination> KClass<T>.route: String
    get() = this.simpleName!!

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
                    mainNavigationState = state,
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
            route = MainDestination.Practice.Writing::class.route,
            content = {
                val configuration = state.persistentPracticeDestination.value
                        as MainDestination.Practice.Writing
//                WritingPracticeScreen(configuration, state)
            }
        )

        composable(
            route = MainDestination.Practice.Reading::class.route,
            content = {
                val configuration = state.persistentPracticeDestination.value
                        as MainDestination.Practice.Reading
//                ReadingPracticeScreen(
//                    state,
//                    configuration
//                )
            }
        )

        composable(
            route = MainDestination.CreatePractice::class.route,
            content = {
                val configuration = state.persistentCreatePracticeDestination.value
                        as MainDestination.CreatePractice
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
            route = "${MainDestination.PracticePreview::class.route}?id={id}",
            arguments = listOf(
                navArgument("id") { type = NavType.LongType }
            ),
            content = {
                val practiceId = it.arguments!!.getLong("id")
//                PracticePreviewScreen(practiceId, state)
            }
        )

        composable(
            route = "${MainDestination.KanjiInfo::class.route}?kanji={kanji}",
            arguments = listOf(
                navArgument("kanji") { type = NavType.StringType }
            ),
            content = {
                val kanji = it.arguments!!.getString("kanji")!!
//                KanjiInfoScreen(kanji, state)
            }
        )

    }
}

private class AndroidMainNavigationState(
    val navHostController: NavHostController,
    val persistentPracticeDestination: MutableState<MainDestination.Practice?>,
    val persistentCreatePracticeDestination: MutableState<MainDestination.CreatePractice?>
) : MainNavigationState {

    override fun navigateBack() {
        navHostController.navigateUp()
    }

    override fun popUpToHome() {
        navHostController.popBackStack(MainDestination.Home::class.route, false)
    }

    override fun navigate(destination: MainDestination) {
        when (destination) {
            MainDestination.Home -> {
                navHostController.navigate(MainDestination.Home::class.route)
            }
            MainDestination.About -> {
                navHostController.navigate(MainDestination.About::class.route)
            }
            MainDestination.ImportPractice -> {
                navHostController.navigate(MainDestination.ImportPractice::class.route)
            }
            is MainDestination.CreatePractice -> {
                persistentCreatePracticeDestination.value = destination
                navHostController.navigate(MainDestination.CreatePractice::class.route)
            }
            is MainDestination.KanjiInfo -> {
                val routeName = MainDestination.KanjiInfo::class.route
                navHostController.navigate("$routeName?kanji=${destination.character}")
            }
            is MainDestination.PracticePreview -> {
                val routeName = MainDestination.PracticePreview::class.route
                navHostController.navigate("$routeName?id=${destination.id}")
            }
            is MainDestination.Practice.Writing -> {
                persistentPracticeDestination.value = destination
                navHostController.navigate(MainDestination.Practice.Writing::class.route)
            }
            is MainDestination.Practice.Reading -> {
                persistentPracticeDestination.value = destination
                navHostController.navigate(MainDestination.Practice.Reading::class.route)
            }
        }
    }

}
