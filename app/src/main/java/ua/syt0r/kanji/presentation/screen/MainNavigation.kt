package ua.syt0r.kanji.presentation.screen

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ua.syt0r.kanji.presentation.common.navigation.NavigationContract
import ua.syt0r.kanji.presentation.screen.screen.about.AboutScreen
import ua.syt0r.kanji.presentation.screen.screen.home.HomeScreen
import ua.syt0r.kanji.presentation.screen.screen.kanji_info.KanjiInfoScreen
import ua.syt0r.kanji.presentation.screen.screen.practice_create.CreateWritingPracticeScreen
import ua.syt0r.kanji.presentation.screen.screen.practice_create.data.CreatePracticeConfiguration
import ua.syt0r.kanji.presentation.screen.screen.practice_import.PracticeImportScreen
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.PracticePreviewScreen
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.WritingPracticeScreen
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.WritingPracticeConfiguration

class MainNavigation(
    private val navHostController: NavHostController,
    private val mainViewModel: MainContract.ViewModel
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
                content = { AboutScreen(navigation = this@MainNavigation) }
            )

            composable(
                route = "$WRITING_PRACTICE_ROUTE/{${PRACTICE_ID_KEY}}",
                content = {
                    WritingPracticeScreen(
                        mainViewModel.writingPracticeConfiguration!!,
                        this@MainNavigation
                    )
                }
            )

            composable(
                route = PRACTICE_CREATE_ROUTE,
                content = {
                    CreateWritingPracticeScreen(
                        mainViewModel.createPracticeConfiguration!!,
                        this@MainNavigation
                    )
                }
            )

            composable(
                route = PRACTICE_IMPORT_ROUTE,
                content = { PracticeImportScreen(this@MainNavigation) }
            )

            composable(
                route = "$PRACTICE_PREVIEW_ROUTE?id={id}&title={title}",
                arguments = listOf(
                    navArgument("id") { type = NavType.LongType },
                    navArgument("title") { type = NavType.StringType }
                ),
                content = {
                    PracticePreviewScreen(
                        practiceId = it.arguments!!.getLong("id"),
                        practiceTitle = it.arguments!!.getString("title")!!,
                        navigation = this@MainNavigation
                    )
                }
            )

            composable(
                route = "$KANJI_INFO_ROUTE/{${KANJI_ARGUMENT_KEY}}",
                arguments = listOf(
                    navArgument(KANJI_ARGUMENT_KEY) { type = NavType.StringType }
                ),
                content = {
                    KanjiInfoScreen(
                        kanji = it.arguments!!.getString(KANJI_ARGUMENT_KEY)!!,
                        navigation = this@MainNavigation
                    )
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
