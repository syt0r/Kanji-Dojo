package ua.syt0r.kanji.presentation.screen.main

import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ua.syt0r.kanji.presentation.screen.main.screen.about.AboutScreen
import ua.syt0r.kanji.presentation.screen.main.screen.home.HomeScreen
import ua.syt0r.kanji.presentation.screen.main.screen.kanji_info.KanjiInfoScreen
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.CreateWritingPracticeScreen
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.data.CreatePracticeConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.practice_import.PracticeImportScreen
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.PracticePreviewScreen
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticeScreenConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.ReadingPracticeScreen
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.WritingPracticeScreen

interface MainNavigationState {

    fun navigateBack()

    fun popUpToHome()
    fun navigateToAbout()

    fun navigateToPracticeCreate(configuration: CreatePracticeConfiguration)
    fun navigateToPracticeImport()
    fun navigateToPracticePreview(practiceId: Long, title: String)

    fun navigateToWritingPractice(configuration: PracticeScreenConfiguration.Writing)
    fun navigateToReadingPractice(configuration: PracticeScreenConfiguration.Reading)

    fun navigateToKanjiInfo(kanji: String)

}

@Composable
fun rememberMainNavigationState(viewModel: MainContract.ViewModel): MainNavigationState {
    val navController = rememberNavController()
    return MainNavigationStateImpl(navController, viewModel)
}

private sealed class MainRoutes(
    val route: String,
    val arguments: List<NamedNavArgument> = emptyList()
) {

    object Home : MainRoutes("home")
    object About : MainRoutes("about")

    object PracticeCreate : MainRoutes("practice_create")
    object PracticeImport : MainRoutes("practice_import")
    object PracticePreview : MainRoutes(
        route = "practice_preview",
        arguments = listOf(
            navArgument("id") { type = NavType.LongType }
        )
    )

    object WritingPractice : MainRoutes("writing_practice")
    object ReadingPractice : MainRoutes("reading_practice")

    object KanjiInfo : MainRoutes(
        route = "kanji_info",
        arguments = listOf(
            navArgument("kanji") { type = NavType.StringType }
        )
    )

}

private val StartRoute = MainRoutes.Home

@Composable
fun MainNavigationContent(
    state: MainNavigationState
) {

    state as MainNavigationStateImpl

    NavHost(
        navController = state.navHostController,
        startDestination = StartRoute.route
    ) {

        composable(
            route = MainRoutes.Home.route,
            content = { HomeScreen(state) }
        )

        composable(
            route = MainRoutes.About.route,
            content = { AboutScreen(state) }
        )

        composable(
            route = MainRoutes.WritingPractice.route,
            content = {
                val configuration = state.mainViewModel
                    .practiceConfiguration!! as PracticeScreenConfiguration.Writing
                WritingPracticeScreen(configuration, state)
            }
        )

        composable(
            route = MainRoutes.ReadingPractice.route,
            content = {
                ReadingPracticeScreen(
                    state,
                    state.mainViewModel.practiceConfiguration
                        .let { it as PracticeScreenConfiguration.Reading }
                )
            }
        )

        composable(
            route = MainRoutes.PracticeCreate.route,
            content = {
                val configuration = state.mainViewModel.createPracticeConfiguration!!
                CreateWritingPracticeScreen(configuration, state)
            }
        )

        composable(
            route = MainRoutes.PracticeImport.route,
            content = { PracticeImportScreen(state) }
        )

        composable(
            route = "${MainRoutes.PracticePreview.route}?id={id}",
            arguments = MainRoutes.PracticePreview.arguments,
            content = {
                val practiceId = it.arguments!!.getLong("id")
                PracticePreviewScreen(practiceId, state)
            }
        )

        composable(
            route = "${MainRoutes.KanjiInfo.route}?kanji={kanji}",
            arguments = MainRoutes.KanjiInfo.arguments,
            content = {
                val kanji = it.arguments!!.getString("kanji")!!
                KanjiInfoScreen(kanji, state)
            }
        )

    }
}

private class MainNavigationStateImpl(
    val navHostController: NavHostController,
    val mainViewModel: MainContract.ViewModel
) : MainNavigationState {

    override fun navigateBack() {
        navHostController.navigateUp()
    }

    override fun popUpToHome() {
        navHostController.popBackStack(MainRoutes.Home.route, false)
    }

    override fun navigateToAbout() {
        navHostController.navigate(MainRoutes.About.route)
    }


    override fun navigateToWritingPractice(configuration: PracticeScreenConfiguration.Writing) {
        mainViewModel.practiceConfiguration = configuration
        navHostController.navigate(MainRoutes.WritingPractice.route)
    }

    override fun navigateToReadingPractice(configuration: PracticeScreenConfiguration.Reading) {
        mainViewModel.practiceConfiguration = configuration
        navHostController.navigate(MainRoutes.ReadingPractice.route)
    }

    override fun navigateToPracticeCreate(configuration: CreatePracticeConfiguration) {
        mainViewModel.createPracticeConfiguration = configuration
        navHostController.navigate(MainRoutes.PracticeCreate.route)
    }

    override fun navigateToPracticeImport() {
        navHostController.navigate(MainRoutes.PracticeImport.route)
    }

    override fun navigateToPracticePreview(practiceId: Long, title: String) {
        navHostController.navigate("${MainRoutes.PracticePreview.route}?id=$practiceId")
    }


    override fun navigateToKanjiInfo(kanji: String) {
        navHostController.navigate("${MainRoutes.KanjiInfo.route}?kanji=$kanji")
    }

}
