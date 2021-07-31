package ua.syt0r.kanji.presentation.screen.screen.kanji_info

import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import ua.syt0r.kanji.presentation.screen.screen.kanji_info.KanjiInfoScreenContract.State
import ua.syt0r.kanji.presentation.screen.screen.kanji_info.ui.KanjiInfoScreenUI

@Composable
fun KanjiInfoScreen(
    viewModel: KanjiInfoScreenContract.ViewModel = hiltViewModel<KanjiInfoViewModel>(),
    kanji: String
) {

    val mutableState = viewModel.state.observeAsState(State.Init)

    if (mutableState.value == State.Init) {
        viewModel.loadKanjiInfo(kanji)
    }

    KanjiInfoScreenUI(
        state = mutableState.value
    )

}