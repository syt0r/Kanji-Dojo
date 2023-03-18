package ua.syt0r.kanji.presentation.preview.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.PracticeDashboardScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.data.PracticeDashboardItem
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.ui.PracticeDashboardScreenUI
import kotlin.random.Random
import kotlin.time.Duration.Companion.days

@Preview
@Composable
private fun EmptyPreview(
    state: ScreenState = ScreenState.Loaded(
        practiceSets = emptyList(),
        shouldShowAnalyticsSuggestion = false
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
            onAnalyticsSuggestionDismissed = {}
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
                    reviewToNowDuration = 1.days
                )
            },
            shouldShowAnalyticsSuggestion = false
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
                    reviewToNowDuration = if (it % 2 == 0) null else it.days
                )
            },
            shouldShowAnalyticsSuggestion = false
        ),
        useDarkTheme = true
    )
}