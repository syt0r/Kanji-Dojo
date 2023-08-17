package ua.syt0r.kanji.presentation.preview.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.stats.StatsScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.stats.StatsScreenUI
import kotlin.time.Duration

private val fakeData = StatsScreenContract.ScreenState.Loaded(
    todayStats = StatsScreenContract.TodayStats.NoData,
    totalStats = StatsScreenContract.TotalStats(
        practiceCount = emptyMap(),
        practiceCountTotal = 1,
        distinctCharactersPracticed = 1,
        timeSpent = Duration.ZERO
    ),
    difficultyRankingList = emptyList()
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun Preview() {
    AppTheme {
        StatsScreenUI(rememberUpdatedState(fakeData))
    }
}

@Preview(showSystemUi = true, device = Devices.PIXEL_C)
@Composable
private fun TabletPreview() {
    ua.syt0r.kanji.presentation.preview.screen.Preview()
}
