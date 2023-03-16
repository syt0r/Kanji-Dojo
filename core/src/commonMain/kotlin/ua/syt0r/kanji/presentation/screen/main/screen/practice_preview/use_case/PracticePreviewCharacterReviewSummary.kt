package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.use_case

import kotlinx.datetime.*
import ua.syt0r.kanji.core.time.TimeUtils
import ua.syt0r.kanji.core.user_data.PracticeRepository
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.PracticePreviewScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.CharacterReviewState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticeSummary
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticeType
import kotlin.math.sqrt

/***
 * See PracticePreviewCharacterStateUseCaseTest test cases for examples of how
 * algorithm works
 */
class PracticePreviewCharacterReviewSummary(
    private val practiceRepository: PracticeRepository,
    private val timeUtils: TimeUtils,
    private val timeZone: TimeZone = TimeZone.currentSystemDefault()
) : PracticePreviewScreenContract.GetPracticeSummary {

    companion object {
        private const val MinLongTermReviewDays = 5
    }

    override suspend fun getSummary(
        character: String,
        type: PracticeType
    ): PracticeSummary {
        val (reviewTimeToMistakes, toleratedMistakesCount) = practiceRepository.run {
            when (type) {
                PracticeType.Writing -> getWritingReviewWithErrors(character) to 2
                PracticeType.Reading -> getReadingReviewWithErrors(character) to 0
            }
        }

        return PracticeSummary(
            firstReviewDate = reviewTimeToMistakes.minOfOrNull { (time, _) -> time }
                ?.toLocalDateTime(timeZone),
            lastReviewDate = reviewTimeToMistakes.maxOfOrNull { (time, _) -> time }
                ?.toLocalDateTime(timeZone),
            state = calculateState(
                reviewTimeToMistakes = reviewTimeToMistakes,
                toleratedMistakesCount = toleratedMistakesCount
            )
        )
    }

    private fun calculateState(
        reviewTimeToMistakes: Map<Instant, Int>,
        toleratedMistakesCount: Int = 2
    ): CharacterReviewState {
        val sortedPracticeDatesToMistakes = reviewTimeToMistakes
            .map { (time, mistakes) -> time.toLocalDateTime(timeZone).date to mistakes }
            .groupBy { it.first }
            .map { (date, pairs) -> date to pairs.maxOf { (_, mistakes) -> mistakes } }
            .sortedBy { (date, _) -> date }


        if (sortedPracticeDatesToMistakes.isEmpty()) {
            return CharacterReviewState.NeverReviewed
        }

        val today = timeUtils.getCurrentDate()

        val isReviewedToday = sortedPracticeDatesToMistakes.any { (date, _) -> date == today }
        if (isReviewedToday) {
            return CharacterReviewState.RecentlyReviewed
        }

        val indexOfReviewPeriodStart = sortedPracticeDatesToMistakes
            .indexOfLast { (_, errors) -> errors > toleratedMistakesCount }
            .let { if (it == -1) 0 else it }

        val reviewPeriodStartDate = sortedPracticeDatesToMistakes[indexOfReviewPeriodStart].first
        val reviewPeriodEndDate = sortedPracticeDatesToMistakes.last().first

        val daysBetweenFirstAndLastReview = reviewPeriodStartDate.until(
            other = reviewPeriodEndDate,
            unit = DateTimeUnit.DAY
        )
        val daysBetweenFirstReviewAndToday = reviewPeriodStartDate.until(
            other = today,
            unit = DateTimeUnit.DAY
        )

        val daysWithReviews = sortedPracticeDatesToMistakes.size - indexOfReviewPeriodStart
        val expectedDaysWithReviews = sqrt(daysBetweenFirstReviewAndToday.toFloat())

        val isRecentlyReviewed = daysWithReviews > expectedDaysWithReviews
                || (daysBetweenFirstReviewAndToday > MinLongTermReviewDays && daysBetweenFirstAndLastReview * 2 > daysBetweenFirstReviewAndToday)

        return if (isRecentlyReviewed) {
            CharacterReviewState.RecentlyReviewed
        } else {
            CharacterReviewState.NeedReview
        }
    }

}