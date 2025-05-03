package ua.syt0r.kanji.presentation.screen.main.screen.sync

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalUriHandler
import ua.syt0r.kanji.presentation.getMultiplatformViewModel
import ua.syt0r.kanji.presentation.screen.main.MainDestination
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.general_dashboard.SocialButton

@Composable
fun SyncScreen(
    mainNavigationState: MainNavigationState,
    viewModel: SyncScreenContract.ViewModel = getMultiplatformViewModel()
) {

    val uriHandler = LocalUriHandler.current

    SyncScreenUI(
        state = viewModel.state.collectAsState(),
        onUpClick = mainNavigationState::navigateBack,
        navigateToAccountScreen = { mainNavigationState.navigate(MainDestination.Account()) },
        navigateToDiscord = { uriHandler.openUri(SocialButton.Discord.url) },
        sync = viewModel::sync
    )

}