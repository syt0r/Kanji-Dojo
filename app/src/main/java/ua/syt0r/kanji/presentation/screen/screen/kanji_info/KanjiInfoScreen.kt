package ua.syt0r.kanji.presentation.screen.screen.kanji_info

import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import ua.syt0r.kanji.presentation.screen.MainContract
import ua.syt0r.kanji.presentation.screen.screen.kanji_info.KanjiInfoScreenContract.State
import ua.syt0r.kanji.presentation.screen.screen.kanji_info.ui.KanjiInfoScreenUI

@Composable
fun KanjiInfoScreen(
    kanji: String,
    navigation: MainContract.Navigation,
    viewModel: KanjiInfoScreenContract.ViewModel = hiltViewModel<KanjiInfoViewModel>(),
) {

    val mutableState = viewModel.state.observeAsState(State.Init)

    if (mutableState.value == State.Init) {
        viewModel.loadKanjiInfo(kanji)
    }

    KanjiInfoScreenUI(
        state = mutableState.value,
        onUpButtonClick = { navigation.navigateBack() }
    )

}