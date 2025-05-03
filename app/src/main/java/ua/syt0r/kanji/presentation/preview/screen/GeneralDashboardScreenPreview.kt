package ua.syt0r.kanji.presentation.preview.screen

import android.annotation.SuppressLint
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.general_dashboard.GeneralDashboardScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.general_dashboard.GeneralDashboardScreenUI
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.general_dashboard.GeneralDashboardStats
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.general_dashboard.StudyTarget
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.general_dashboard.StudyTargetProgress
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.general_dashboard.StudyTargetState

@SuppressLint("UnrememberedMutableState")
@Preview(device = Devices.PIXEL_TABLET)
@Composable
private fun Preview() {
    AppTheme {
        Surface {
            GeneralDashboardScreenUI(
                state = rememberUpdatedState(
                    GeneralDashboardScreenContract.ScreenState.Loaded(
                        studyTargets = mutableStateOf(
                            listOf(
                                StudyTargetState(
                                    studyTarget = StudyTarget.LetterWriting,
                                    enabled = true,
                                    progress = StudyTargetProgress.NoDecks
                                )
                            )
                        ),
                        stats = GeneralDashboardStats(
                            currentStreak = 1,
                            longestStreak = 1,
                            reviewsToday = 1
                        )
                    )
                ),
                navigateToDailyLimitConfiguration = {},
                navigateToCreateLetterDeck = {},
                navigateToCreateVocabDeck = {},
                navigateToLetterPractice = {},
                navigateToVocabPractice = {},
                downloadsClick = {},
                socialClick = {},
                textAnalysisClick = {}
            )
        }
    }
}