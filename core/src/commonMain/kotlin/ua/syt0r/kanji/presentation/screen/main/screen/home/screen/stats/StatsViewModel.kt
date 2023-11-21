package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.stats

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import ua.syt0r.kanji.core.app_state.AppStateManager
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.core.time.TimeUtils
import ua.syt0r.kanji.core.user_data.PracticeRepository
import ua.syt0r.kanji.core.user_data.model.CharacterReviewResult
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.stats.StatsScreenContract.ScreenState
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class StatsViewModel(
    viewModelScope: CoroutineScope,
    private val appStateManager: AppStateManager,
    private val practiceRepository: PracticeRepository,
    private val timeUtils: TimeUtils
) : StatsScreenContract.ViewModel {

    override val state: MutableState<ScreenState> = mutableStateOf(ScreenState.Loading)

    init {
        appStateManager.appStateFlow
            .map {
                Logger.d(it.toString())
                if (it.isLoading) return@map ScreenState.Loading

                val today = timeUtils.getCurrentDate()

                val timeZone = TimeZone.currentSystemDefault()
                val periodStart = LocalDate(today.year, 1, 1).atStartOfDayIn(timeZone)
                val periodEnd = LocalDate(today.year + 1, 1, 1).atStartOfDayIn(timeZone)

                val reviews = practiceRepository.getReviews(periodStart, periodEnd)
                    .mapValues { (_, instant) -> instant.toLocalDateTime(timeZone).date }
                    .toList()

                val dateToReviews: Map<LocalDate, List<CharacterReviewResult>> = reviews
                    .groupBy { it.second }
                    .toList()
                    .associate { it.first to it.second.map { it.first } }

                val todayReviews = dateToReviews[today] ?: emptyList()

                ScreenState.Loaded(
                    today = today,
                    yearlyPractices = dateToReviews.mapValues { (_, practices) -> practices.size },
                    todayReviews = todayReviews.size,
                    todayTimeSpent = todayReviews.map { it.reviewDuration }
                        .fold(Duration.ZERO) { acc, duration -> acc.plus(duration) },
                    totalReviews = practiceRepository
                        .getTotalReviewsCount()
                        .toInt(),
                    totalTimeSpent = practiceRepository
                        .getTotalPracticeTime()
                        .milliseconds
                )
            }
            .onEach { state.value = it }
            .launchIn(viewModelScope)
    }
}