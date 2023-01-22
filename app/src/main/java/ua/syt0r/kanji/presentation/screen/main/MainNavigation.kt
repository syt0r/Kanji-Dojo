package ua.syt0r.kanji.presentation.screen.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ua.syt0r.kanji.core.analytics.LocalAnalyticsManager
import ua.syt0r.kanji.presentation.screen.main.screen.about.AboutScreen
import ua.syt0r.kanji.presentation.screen.main.screen.home.HomeScreen
import ua.syt0r.kanji.presentation.screen.main.screen.kanji_info.KanjiInfoScreen
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.CreateWritingPracticeScreen
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.data.CreatePracticeConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.practice_import.PracticeImportScreen
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.PracticePreviewScreen
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.WritingPracticeScreen
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.WritingPracticeConfiguration

interface MainNavigationState {

    fun navigateBack()

    fun popUpToHome()
    fun navigateToAbout()

    fun navigateToPracticeCreate(configuration: CreatePracticeConfiguration)
    fun navigateToPracticeImport()
    fun navigateToPracticePreview(practiceId: Long, title: String)

    fun navigateToWritingPractice(configuration: WritingPracticeConfiguration)

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
            navArgument("id") { type = NavType.LongType },
            navArgument("title") { type = NavType.StringType }
        )
    )

    object WritingPractice : MainRoutes("writing_practice")

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

    val analyticsManager = LocalAnalyticsManager.current

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
            content = {
                LaunchedEffect(Unit) { analyticsManager.setScreen("about") }
                AboutScreen(state)
            }
        )

        composable(
            route = MainRoutes.WritingPractice.route,
            content = {
                val configuration = state.mainViewModel.writingPracticeConfiguration!!

                LaunchedEffect(Unit) {
                    analyticsManager.setScreen("writing_practice")
                    analyticsManager.sendEvent("writing_practice_configuration") {
                        putInt("list_size", configuration.characterList.size)
                    }
                }

                WritingPracticeScreen(configuration, state)
            }
        )

        composable(
            route = MainRoutes.PracticeCreate.route,
            content = {
                val configuration = state.mainViewModel.createPracticeConfiguration!!

                LaunchedEffect(Unit) {
                    analyticsManager.setScreen("practice_create")
                    analyticsManager.sendEvent("writing_practice_configuration") {
                        when (configuration) {
                            is CreatePracticeConfiguration.Import -> {
                                putString("mode", "import")
                                putString("practice_title", configuration.title)
                            }
                            is CreatePracticeConfiguration.EditExisting -> {
                                putString("mode", "edit")
                            }
                            is CreatePracticeConfiguration.NewPractice -> {
                                putString("mode", "new")
                            }
                        }
                    }
                }

                CreateWritingPracticeScreen(configuration, state)
            }
        )

        composable(
            route = MainRoutes.PracticeImport.route,
            content = {
                LaunchedEffect(Unit) { analyticsManager.setScreen("import") }
                PracticeImportScreen(state)
            }
        )

        composable(
            route = "${MainRoutes.PracticePreview.route}?id={id}&title={title}",
            arguments = MainRoutes.PracticePreview.arguments,
            content = {

                val practiceId = it.arguments!!.getLong("id")
                val practiceTitle = it.arguments!!.getString("title")!!

                LaunchedEffect(Unit) { analyticsManager.setScreen("practice_preview") }
                PracticePreviewScreen(practiceId, practiceTitle, state)

            }
        )

        composable(
            route = "${MainRoutes.KanjiInfo.route}?kanji={kanji}",
            arguments = MainRoutes.KanjiInfo.arguments,
            content = {
                val kanji = it.arguments!!.getString("kanji")!!
                LaunchedEffect(Unit) {
                    analyticsManager.setScreen("kanji_info")
                    analyticsManager.sendEvent("kanji_info_open") {
                        putString("character", kanji)
                    }
                }
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


    override fun navigateToWritingPractice(configuration: WritingPracticeConfiguration) {
        mainViewModel.writingPracticeConfiguration = configuration
        navHostController.navigate(MainRoutes.WritingPractice.route)
    }

    override fun navigateToPracticeCreate(configuration: CreatePracticeConfiguration) {
        mainViewModel.createPracticeConfiguration = configuration
        navHostController.navigate(MainRoutes.PracticeCreate.route)
    }

    override fun navigateToPracticeImport() {
        navHostController.navigate(MainRoutes.PracticeImport.route)
    }

    override fun navigateToPracticePreview(practiceId: Long, title: String) {
        navHostController.navigate("${MainRoutes.PracticePreview.route}?id=$practiceId&title=$title")
    }


    override fun navigateToKanjiInfo(kanji: String) {
        navHostController.navigate("${MainRoutes.KanjiInfo.route}?kanji=$kanji")
    }

}
