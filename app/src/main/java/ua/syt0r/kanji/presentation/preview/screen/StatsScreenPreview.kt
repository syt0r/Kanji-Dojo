package ua.syt0r.kanji.presentation.preview.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.datetime.LocalDate
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.stats.StatsScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.stats.StatsScreenUI
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private val fakeData = StatsScreenContract.ScreenState.Loaded(
    today = LocalDate(2023, 11, 21),
    yearlyPractices = (0..40).associate {
        LocalDate(2023, Random.nextInt(1, 12), Random.nextInt(1, 27)) to
                Random.nextInt(1, 10)
    },
    todayReviews = 4,
    todayTimeSpent = 205.seconds,
    totalReviews = 200,
    totalTimeSpent = 60004.seconds
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
