package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.stats

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.stats.StatsScreenContract.ScreenState

class StatsViewModel(
    viewModelScope: CoroutineScope
) : StatsScreenContract.ViewModel {

    override val state: State<ScreenState> = mutableStateOf(ScreenState.Loading)

}