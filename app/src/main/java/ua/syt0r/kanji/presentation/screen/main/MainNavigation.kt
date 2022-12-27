package ua.syt0r.kanji.presentation.screen.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import ua.syt0r.kanji.presentation.common.navigation.NavigationContract
import ua.syt0r.kanji.presentation.screen.main.screen.about.AboutScreen
import ua.syt0r.kanji.presentation.screen.main.screen.home.HomeScreen
import ua.syt0r.kanji.presentation.screen.main.screen.kanji_info.KanjiInfoScreen
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.CreateWritingPracticeScreen
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.data.CreatePracticeConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.practice_import.PracticeImportScreen
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.PracticePreviewScreen
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.WritingPracticeScreen
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.WritingPracticeConfiguration

class MainNavigation(
    private val navHostController: NavHostController,
    private val mainViewModel: MainContract.ViewModel,
    private val analyticsManager: AnalyticsManager
) : NavigationContract.Host, MainContract.Navigation {

    companion object {
        private const val HOME_ROUTE = "home"
        private const val ABOUT_ROUTE = "about"

        private const val PRACTICE_CREATE_ROUTE = "practice_create"
        private const val PRACTICE_IMPORT_ROUTE = "practice_import"
        private const val PRACTICE_PREVIEW_ROUTE = "practice_preview"

        private const val WRITING_PRACTICE_ROUTE = "writing_practice"

        private const val KANJI_INFO_ROUTE = "kanji_info"

        private const val KANJI_ARGUMENT_KEY = "kanji"
        private const val PRACTICE_ID_KEY = "practice_id"
    }

    @Composable
    override fun DrawContent() {

        NavHost(
            navController = navHostController,
            startDestination = HOME_ROUTE
        ) {

            composable(
                route = HOME_ROUTE,
                content = { HomeScreen(this@MainNavigation) }
            )

            composable(
                route = ABOUT_ROUTE,
                content = {
                    LaunchedEffect(Unit) { analyticsManager.setScreen("about") }
                    AboutScreen(navigation = this@MainNavigation)
                }
            )

            composable(
                route = "$WRITING_PRACTICE_ROUTE/{$PRACTICE_ID_KEY}",
                content = {
                    val configuration = mainViewModel.writingPracticeConfiguration!!

                    LaunchedEffect(Unit) {
                        analyticsManager.setScreen("writing_practice")
                        analyticsManager.sendEvent("writing_practice_configuration") {
                            putInt("list_size", configuration.characterList.size)
                        }
                    }

                    WritingPracticeScreen(configuration, this@MainNavigation)
                }
            )

            composable(
                route = PRACTICE_CREATE_ROUTE,
                content = {
                    val configuration = mainViewModel.createPracticeConfiguration!!

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

                    CreateWritingPracticeScreen(configuration, this@MainNavigation)
                }
            )

            composable(
                route = PRACTICE_IMPORT_ROUTE,
                content = {
                    LaunchedEffect(Unit) { analyticsManager.setScreen("import") }
                    PracticeImportScreen(this@MainNavigation)
                }
            )

            composable(
                route = "$PRACTICE_PREVIEW_ROUTE?id={id}&title={title}",
                arguments = listOf(
                    navArgument("id") { type = NavType.LongType },
                    navArgument("title") { type = NavType.StringType }
                ),
                content = {
                    LaunchedEffect(Unit) { analyticsManager.setScreen("practice_preview") }
                    PracticePreviewScreen(
                        practiceId = it.arguments!!.getLong("id"),
                        practiceTitle = it.arguments!!.getString("title")!!,
                        navigation = this@MainNavigation
                    )
                }
            )

            composable(
                route = "$KANJI_INFO_ROUTE/{$KANJI_ARGUMENT_KEY}",
                arguments = listOf(
                    navArgument(KANJI_ARGUMENT_KEY) { type = NavType.StringType }
                ),
                content = {

                    val kanji = it.arguments!!.getString(KANJI_ARGUMENT_KEY)!!
                    LaunchedEffect(Unit) { analyticsManager.setScreen("kanji_info") }
                    KanjiInfoScreen(kanji, this@MainNavigation)
                }
            )

        }

    }

    override fun navigateBack() {
        navHostController.navigateUp()
    }


    override fun popUpToHome() {
        navHostController.popBackStack(HOME_ROUTE, false)
    }

    override fun navigateToAbout() {
        navHostController.navigate(ABOUT_ROUTE)
    }


    override fun navigateToWritingPractice(configuration: WritingPracticeConfiguration) {
        mainViewModel.writingPracticeConfiguration = configuration
        navHostController.navigate("$WRITING_PRACTICE_ROUTE/${configuration.practiceId}")
    }

    override fun navigateToPracticeCreate(configuration: CreatePracticeConfiguration) {
        mainViewModel.createPracticeConfiguration = configuration
        navHostController.navigate(PRACTICE_CREATE_ROUTE)
    }

    override fun navigateToPracticeImport() {
        navHostController.navigate(PRACTICE_IMPORT_ROUTE)
    }

    override fun navigateToPracticePreview(practiceId: Long, title: String) {
        navHostController.navigate("$PRACTICE_PREVIEW_ROUTE?id=$practiceId&title=$title")
    }


    override fun navigateToKanjiInfo(kanji: String) {
        navHostController.navigate("$KANJI_INFO_ROUTE/$kanji")
    }

}
