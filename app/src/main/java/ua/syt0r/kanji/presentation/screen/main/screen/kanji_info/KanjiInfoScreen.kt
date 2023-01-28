package ua.syt0r.kanji.presentation.screen.main.screen.kanji_info

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.hilt.navigation.compose.hiltViewModel
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState
import ua.syt0r.kanji.presentation.screen.main.screen.kanji_info.ui.KanjiInfoScreenUI


@Composable
fun KanjiInfoScreen(
    kanji: String,
    mainNavigationState: MainNavigationState,
    viewModel: KanjiInfoScreenContract.ViewModel = hiltViewModel<KanjiInfoViewModel>(),
) {

    LaunchedEffect(Unit) {
        viewModel.loadCharacterInfo(kanji)
        viewModel.reportScreenShown(kanji)
    }

    val clipboardManager = LocalClipboardManager.current

    KanjiInfoScreenUI(
        char = kanji,
        state = viewModel.state,
        onUpButtonClick = { mainNavigationState.navigateBack() },
        onCopyButtonClick = { clipboardManager.setText(AnnotatedString(kanji)) },
        onFuriganaItemClick = { mainNavigationState.navigateToKanjiInfo(it) }
    )

}