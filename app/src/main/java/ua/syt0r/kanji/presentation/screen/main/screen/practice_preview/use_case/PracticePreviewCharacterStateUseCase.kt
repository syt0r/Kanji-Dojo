package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.use_case

import ua.syt0r.kanji.core.time.TimeUtils
import ua.syt0r.kanji.core.user_data.UserDataContract
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.PracticePreviewScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.CharacterReviewState
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.math.sqrt

/***
 * See PracticePreviewCharacterStateUseCaseTest test cases for examples of how
 * algorithm works
 */
class PracticePreviewCharacterStateUseCase @Inject constructor(
    private val practiceRepository: UserDataContract.PracticeRepository,
    private val timeUtils: TimeUtils
) : PracticePreviewScreenContract.PracticePreviewCharacterStateUseCase {

    override suspend fun calculateState(character: String): CharacterReviewState {
        val reviewDateToMaxPracticeErrors: List<Pair<LocalDate, Int>> = practiceRepository
            .getReviewDatesWithErrors(character)
            .toList()
            .sortedBy { it.first }

        if (reviewDateToMaxPracticeErrors.isEmpty()) {
            return CharacterReviewState.NeverReviewed
        }

        val today = timeUtils.getCurrentDay()

        val isReviewedToday = reviewDateToMaxPracticeErrors.any { (date, _) -> date == today }
        if (isReviewedToday) {
            return CharacterReviewState.RecentlyReviewed
        }

        val indexOfReviewPeriodStart = reviewDateToMaxPracticeErrors
            .indexOfLast { (_, errors) -> errors > 2 }
            .let { if (it == -1) 0 else it }
        val reviewPeriodStartDate = reviewDateToMaxPracticeErrors[indexOfReviewPeriodStart]
            .first

        val reviewDaysSincePeriodStart = reviewDateToMaxPracticeErrors.size -
                indexOfReviewPeriodStart

        val totalDaysSincePeriodStart = ChronoUnit.DAYS.between(reviewPeriodStartDate, today)
        val expectedReviewCountThreshold = sqrt(totalDaysSincePeriodStart.toFloat())

        return if (reviewDaysSincePeriodStart > expectedReviewCountThreshold) {
            CharacterReviewState.RecentlyReviewed
        } else {
            CharacterReviewState.NeedReview
        }
    }

}