package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import ua.syt0r.kanji.presentation.screen.main.MainDestination
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.ui.SearchScreenUI

@Composable
fun SearchScreen(
    mainNavigationState: MainNavigationState,
    viewModel: SearchScreenContract.ViewModel
) {

    LaunchedEffect(Unit) {
        viewModel.reportScreenShown()
    }

    SearchScreenUI(
        state = viewModel.state,
        radicalsState = viewModel.radicalsState,
        onSubmitInput = { viewModel.search(it) },
        onRadicalsSectionExpanded = { viewModel.loadRadicalsData() },
        onRadicalsSelected = { viewModel.radicalsSearch(it) },
        onCharacterClick = { mainNavigationState.navigate(MainDestination.KanjiInfo(it)) }
    )

}
