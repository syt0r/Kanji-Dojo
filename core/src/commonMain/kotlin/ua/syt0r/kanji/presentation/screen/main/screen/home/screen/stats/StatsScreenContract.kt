package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.stats

import androidx.compose.runtime.State
import kotlinx.datetime.LocalDate
import kotlin.time.Duration

interface StatsScreenContract {

    interface ViewModel {
        val state: State<ScreenState>
    }

    sealed interface ScreenState {
        object Loading : ScreenState
        data class Loaded(
            val todayStats: TodayStats,
            val totalStats: TotalStats,
            val difficultyRankingList: List<DifficultyRankingItem>
        ) : ScreenState
    }

    sealed interface TodayStats {

        object NoData : TodayStats

        data class TodayPracticeStats(
            val timeSpent: Duration,
            val charactersReviewed: Int
        ) : TodayStats

    }

    data class TotalStats(
        val practiceCount: Map<LocalDate, Int>,
        val practiceCountTotal: Int,
        val distinctCharactersPracticed: Int,
        val timeSpent: Duration
    )

    data class DifficultyRankingItem(
        val mistakes: Int,
        val character: String
    )

}
