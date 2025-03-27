package ua.syt0r.kanji.presentation.screen.main.screen.about

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import ua.syt0r.kanji.presentation.getMultiplatformViewModel
import ua.syt0r.kanji.presentation.screen.main.MainDestination
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState


@Composable
fun AboutScreen(
    mainNavigationState: MainNavigationState,
    viewModel: AboutScreenContract.ViewModel = getMultiplatformViewModel()
) {

    val uriHandler = LocalUriHandler.current

    AboutScreenUI(
        onUpButtonClick = { mainNavigationState.navigateBack() },
        openLink = { url ->
            uriHandler.openUri(url)
            viewModel.reportUrlClick(url)
        },
        navigateToCredits = { mainNavigationState.navigate(MainDestination.Credits) }
    )

}
