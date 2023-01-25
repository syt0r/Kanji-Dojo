package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.ui.SearchScreenUI

@Composable
fun SearchScreen(
    mainNavigationState: MainNavigationState,
    viewModel: SearchScreenContract.ViewModel = hiltViewModel<SearchViewModel>()
) {

    SearchScreenUI(
        state = viewModel.state,
        onSubmitInput = { viewModel.search(it) },
        onCharacterClick = { mainNavigationState.navigateToKanjiInfo(it) },
        onWordClick = {}
    )

}
