package ua.syt0r.kanji.presentation.screen.screen.about

import androidx.compose.runtime.Composable
import ua.syt0r.kanji.presentation.screen.MainContract
import ua.syt0r.kanji.presentation.screen.screen.about.ui.AboutScreenUI

@Composable
fun AboutScreen(
    navigation: MainContract.Navigation
) {

    AboutScreenUI(
        onUpButtonClick = { navigation.navigateBack() },
        onGithubClick = {}
    )

}
