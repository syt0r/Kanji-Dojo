package ua.syt0r.kanji.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import ua.syt0r.kanji.ui.navigation.NavigationContract
import ua.syt0r.kanji.ui.screen.screen.about.AboutNavDestination
import ua.syt0r.kanji.ui.screen.screen.create_custom_set.CreateCustomSetNavDestination
import ua.syt0r.kanji.ui.screen.screen.home.HomeNavDestination
import ua.syt0r.kanji.ui.screen.screen.kanji_info.KanjiInfoNavDestination
import ua.syt0r.kanji.ui.screen.screen.practice_set.PracticeSetPreviewNavDestination
import ua.syt0r.kanji.ui.screen.screen.writing_practice.WritingPracticeNavDestination

val LocalMainNavigator = staticCompositionLocalOf<MainContract.Navigation> {
    error("Navigator does not exists")
}

class MainNavigation(
    private val navHostController: NavHostController
) : NavigationContract.Host, MainContract.Navigation {

    @Composable
    override fun setup() {

        NavHost(
            navController = navHostController,
            startDestination = HomeNavDestination.routeName
        ) {

            AboutNavDestination.setup(this)
            KanjiInfoNavDestination.setup(this)
            HomeNavDestination.setup(this)
            CreateCustomSetNavDestination.setup(this)
            PracticeSetPreviewNavDestination.setup(this)
            WritingPracticeNavDestination.setup(this)

        }

    }

    override fun navigateBack() {
        navHostController.navigateUp()
    }

    override fun navigateToHome() {
        HomeNavDestination.navigate(navHostController)
    }

    override fun navigateToCreateCustomPracticeSet() {
        CreateCustomSetNavDestination.navigate(navHostController)
    }

    override fun navigateToPracticeSet(practiceSetId: Long) {
        PracticeSetPreviewNavDestination.navigate(navHostController)
    }

    override fun navigateToWritingPractice(kanjiList: List<String>) {
        WritingPracticeNavDestination.navigate(navHostController, kanjiList)
    }

    override fun navigateToAbout() {
        AboutNavDestination.navigate(navHostController)
    }

    override fun navigateToKanjiInfo(kanji: String) {
        KanjiInfoNavDestination.navigate(navHostController, kanji)
    }

}
