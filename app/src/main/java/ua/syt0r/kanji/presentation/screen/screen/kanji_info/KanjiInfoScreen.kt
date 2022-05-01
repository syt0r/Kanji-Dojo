package ua.syt0r.kanji.presentation.screen.screen.kanji_info

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import ua.syt0r.kanji.presentation.screen.MainContract
import ua.syt0r.kanji.presentation.screen.screen.kanji_info.ui.KanjiInfoScreenUI

@Composable
fun KanjiInfoScreen(
    kanji: String,
    navigation: MainContract.Navigation,
    viewModel: KanjiInfoScreenContract.ViewModel = hiltViewModel<KanjiInfoViewModel>(),
) {

    LaunchedEffect(Unit) {
        viewModel.loadCharacterInfo(kanji)
    }

    val screenState = viewModel.state.value

    KanjiInfoScreenUI(
        char = kanji,
        screenState = screenState,
        onUpButtonClick = { navigation.navigateBack() },
        onCopyButtonClick = {}
    )

}