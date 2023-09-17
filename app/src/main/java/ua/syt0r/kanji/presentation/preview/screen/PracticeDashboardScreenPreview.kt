package ua.syt0r.kanji.presentation.preview.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import ua.syt0r.kanji.core.app_state.DailyGoalConfiguration
import ua.syt0r.kanji.core.app_state.DailyGoalLimitOption
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.PracticeDashboardScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.data.DailyIndicatorData
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.data.DailyProgress
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.data.PracticeDashboardItem
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.data.PracticeStudyProgress
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.ui.PracticeDashboardScreenUI
import kotlin.random.Random
import kotlin.time.Duration.Companion.days

private val dailyIndicatorData = DailyIndicatorData(
    configuration = DailyGoalConfiguration(
        DailyGoalLimitOption.Limited(6),
        DailyGoalLimitOption.Limited(12)
    ),
    progress = DailyProgress.Completed
)

private fun randomStudyProgress(): PracticeStudyProgress {
    return PracticeStudyProgress(
        known = Random.nextInt(1, 6),
        review = Random.nextInt(1, 6),
        new = Random.nextInt(1, 30),
        quickLearn = emptyList(),
        quickReview = emptyList(),
    )
}

@Preview
@Composable
private fun EmptyPreview(
    state: ScreenState = ScreenState.Loaded(
        practiceSets = emptyList(),
        dailyIndicatorData = dailyIndicatorData
    ),
    useDarkTheme: Boolean = false,
) {
    AppTheme(useDarkTheme) {
        PracticeDashboardScreenUI(
            state = rememberUpdatedState(newValue = state),
            onImportPredefinedSet = {},
            onCreateCustomSet = {},
            onPracticeSetSelected = {},
            onAnalyticsSuggestionAccepted = {},
            onAnalyticsSuggestionDismissed = {},
            quickPractice = {}
        )
    }
}

@Preview
@Composable
fun PracticeDashboardUIPreview() {
    EmptyPreview(
        state = ScreenState.Loaded(
            practiceSets = (1..5).map {
                PracticeDashboardItem(
                    practiceId = Random.nextLong(),
                    title = "Grade $it",
                    timeSinceLastPractice = 1.days,
                    writingProgress = randomStudyProgress(),
                    readingProgress = randomStudyProgress()
                )
            },
            dailyIndicatorData = dailyIndicatorData
        )
    )
}

@Preview(device = Devices.PIXEL_C)
@Composable
private fun TabletPreview() {
    EmptyPreview(
        state = ScreenState.Loaded(
            practiceSets = (0..10).map {
                PracticeDashboardItem(
                    practiceId = Random.nextLong(),
                    title = "Grade $it",
                    timeSinceLastPractice = if (it % 2 == 0) null else it.days,
                    writingProgress = randomStudyProgress(),
                    readingProgress = randomStudyProgress()
                )
            },
            dailyIndicatorData = dailyIndicatorData
        ),
        useDarkTheme = true
    )
}