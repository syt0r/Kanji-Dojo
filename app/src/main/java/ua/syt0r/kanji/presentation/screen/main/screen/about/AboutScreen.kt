package ua.syt0r.kanji.presentation.screen.main.screen.about

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import ua.syt0r.kanji.presentation.common.openUrl
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState


@Composable
fun AboutScreen(
    mainNavigationState: MainNavigationState,
    viewModel: AboutScreenContract.ViewModel = hiltViewModel<AboutScreenViewModel>()
) {

    LaunchedEffect(Unit) {
        viewModel.reportScreenShown()
    }

    val context = LocalContext.current

    AboutScreenUI(
        onUpButtonClick = { mainNavigationState.navigateBack() },
        openLink = { url ->
            context.openUrl(url)
            viewModel.reportUrlClick(url)
        }
    )

}
