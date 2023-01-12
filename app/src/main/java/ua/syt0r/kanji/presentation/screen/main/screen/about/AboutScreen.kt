package ua.syt0r.kanji.presentation.screen.main.screen.about

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import ua.syt0r.kanji.presentation.common.openUrl
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState
import ua.syt0r.kanji.presentation.screen.main.screen.about.ui.AboutScreenUI


@Composable
fun AboutScreen(
    mainNavigationState: MainNavigationState
) {

    val context = LocalContext.current

    AboutScreenUI(
        onUpButtonClick = { mainNavigationState.navigateBack() },
        openLink = { url -> context.openUrl(url) }
    )

}
