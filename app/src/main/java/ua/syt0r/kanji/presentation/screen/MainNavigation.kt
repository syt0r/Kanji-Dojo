package ua.syt0r.kanji.presentation.screen

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import ua.syt0r.kanji.presentation.common.navigation.NavigationContract
import ua.syt0r.kanji.presentation.screen.screen.about.AboutScreen
import ua.syt0r.kanji.presentation.screen.screen.home.HomeScreen
import ua.syt0r.kanji.presentation.screen.screen.kanji_info.KanjiInfoScreen
import ua.syt0r.kanji.presentation.screen.screen.writing_dashboard.WritingDashboardScreen
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.WritingPracticeScreen
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.PracticeConfiguration
import ua.syt0r.kanji.presentation.screen.screen.writing_practice_create.CreateWritingPracticeScreen
import ua.syt0r.kanji.presentation.screen.screen.writing_practice_preview.WritingPracticePreviewScreen

class MainNavigation(
    private val navHostController: NavHostController
) : NavigationContract.Host, MainContract.Navigation {

    companion object {
        private const val HOME_ROUTE = "home"
        private const val ABOUT_ROUTE = "about"

        private const val WRITING_DASHBOARD_ROUTE = "writing_dashboard"
        private const val WRITING_PRACTICE_ROUTE = "writing_practice"
        private const val WRITING_PRACTICE_CREATE_ROUTE = "writing_practice_create"
        private const val WRITING_PRACTICE_IMPORT_ROUTE = "writing_practice_import"
        private const val WRITING_PRACTICE_PREVIEW_ROUTE = "writing_practice_preview"

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
                content = { HomeScreen(mainNavigation = this@MainNavigation) }
            )

            composable(
                route = ABOUT_ROUTE,
                content = { AboutScreen() }
            )

            composable(
                route = WRITING_DASHBOARD_ROUTE,
                content = { WritingDashboardScreen(mainNavigation = this@MainNavigation) }
            )

            composable(
                route = "$WRITING_PRACTICE_ROUTE/{${PRACTICE_ID_KEY}}",
                arguments = listOf(
                    navArgument(PRACTICE_ID_KEY) { type = NavType.LongType }
                ),
                content = {
                    WritingPracticeScreen(
                        practiceId = 1,
                        navigation = this@MainNavigation
                    )
                }
            )

            composable(
                route = WRITING_PRACTICE_CREATE_ROUTE,
                content = { CreateWritingPracticeScreen(mainNavigation = this@MainNavigation) }
            )

            composable(
                route = "$WRITING_PRACTICE_PREVIEW_ROUTE/{${PRACTICE_ID_KEY}}",
                arguments = listOf(
                    navArgument(PRACTICE_ID_KEY) { type = NavType.LongType }
                ),
                content = {
                    WritingPracticePreviewScreen(
                        practiceId = it.arguments!!.getLong(PRACTICE_ID_KEY),
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
                        kanji = it.arguments!!.getString(KANJI_ARGUMENT_KEY)!!
                    )
                }
            )

        }

    }

    override fun navigateBack() {
        navHostController.navigateUp()
    }


    override fun navigateToHome() {
        navHostController.navigate(HOME_ROUTE)
    }

    override fun navigateToAbout() {
        navHostController.navigate(ABOUT_ROUTE)
    }


    override fun navigateToWritingDashboard() {
        navHostController.navigate(WRITING_DASHBOARD_ROUTE)
    }

    override fun navigateToWritingPractice(config: PracticeConfiguration) {
        navHostController.navigate("$WRITING_PRACTICE_ROUTE/${config.practiceId}")
    }

    override fun navigateToWritingPracticeCreate() {
        navHostController.navigate(WRITING_PRACTICE_CREATE_ROUTE)
    }

    override fun navigateToWritingPracticeImport() {
        TODO("Not yet implemented")
    }

    override fun navigateToWritingPracticePreview(practiceId: Long) {
        navHostController.navigate("$WRITING_PRACTICE_PREVIEW_ROUTE/$practiceId")
    }


    override fun navigateToKanjiInfo(kanji: String) {
        navHostController.navigate("$KANJI_INFO_ROUTE/$kanji")
    }

}
