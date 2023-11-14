package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.stats

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.stats.StatsScreenContract.ScreenState
import kotlin.time.Duration

class StatsViewModel(
    viewModelScope: CoroutineScope
) : StatsScreenContract.ViewModel {

    val a = StatsScreenContract.ScreenState.Loaded(
        todayStats = StatsScreenContract.TodayStats.NoData,
        totalStats = StatsScreenContract.TotalStats(
            practiceCount = emptyMap(),
            practiceCountTotal = 1,
            distinctCharactersPracticed = 1,
            timeSpent = Duration.ZERO
        ),
        difficultyRankingList = emptyList()
    )

    override val state: State<ScreenState> = mutableStateOf(a)

}