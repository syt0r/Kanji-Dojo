package ua.syt0r.kanji

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import ua.syt0r.kanji.core.time.TimeUtils
import ua.syt0r.kanji.core.user_data.PracticeRepository
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.CharacterReviewState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticeType
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.use_case.PracticePreviewCharacterReviewSummary
import kotlin.random.Random

class PracticePreviewCharacterStateUseCaseTest {

    lateinit var useCase: PracticePreviewCharacterReviewSummary

    @MockK
    lateinit var practiceRepository: PracticeRepository

    @MockK
    lateinit var timeUtils: TimeUtils

    @Before
    fun init() {
        MockKAnnotations.init(this)
        useCase = PracticePreviewCharacterReviewSummary(practiceRepository, timeUtils)
    }


    @Test
    fun `verify continuous review cases`() {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        every { timeUtils.getCurrentDate() } returns today

        val expectedToReviewsMap: List<Pair<CharacterReviewState, Map<LocalDate, Int>>> = listOf(
            CharacterReviewState.NeverReviewed to genReviewData(
                reviewStartDate = today,
                daysOfContinuousReviews = 0
            ),
            CharacterReviewState.RecentlyReviewed to genReviewData(
                reviewStartDate = today,
                daysOfContinuousReviews = 1
            ),
            CharacterReviewState.NeedReview to genReviewData(
                reviewStartDate = today.minus(1, DateTimeUnit.DAY),
                daysOfContinuousReviews = 1
            ),
            CharacterReviewState.RecentlyReviewed to genReviewData(
                reviewStartDate = today.minus(1, DateTimeUnit.DAY),
                daysOfContinuousReviews = 2
            ),
            CharacterReviewState.NeedReview to genReviewData(
                reviewStartDate = today.minus(2, DateTimeUnit.DAY),
                daysOfContinuousReviews = 1
            ),
            CharacterReviewState.RecentlyReviewed to genReviewData(
                reviewStartDate = today.minus(2, DateTimeUnit.DAY),
                daysOfContinuousReviews = 2
            ),
            CharacterReviewState.RecentlyReviewed to genReviewData(
                reviewStartDate = today.minus(3, DateTimeUnit.DAY),
                daysOfContinuousReviews = 2
            ),
            CharacterReviewState.NeedReview to genReviewData(
                reviewStartDate = today.minus(3, DateTimeUnit.DAY),
                daysOfContinuousReviews = 2
            ).plus(today.minus(2, DateTimeUnit.DAY) to Random.nextInt(10, 20)),
            CharacterReviewState.NeedReview to genReviewData(
                reviewStartDate = today.minus(4, DateTimeUnit.DAY),
                daysOfContinuousReviews = 2
            ),
            CharacterReviewState.RecentlyReviewed to genReviewData(
                reviewStartDate = today.minus(5, DateTimeUnit.DAY),
                daysOfContinuousReviews = 3
            ),
        )

        val character = Random.nextInt().toChar().toString()

        expectedToReviewsMap.forEachIndexed { index, (expected, map) ->
            println("Running test $index")
            coEvery { practiceRepository.getWritingReviewWithErrors(character) } coAnswers { map }
            val actual = runBlocking { useCase.getSummary(character) }
            assertEquals(expected, actual)
        }
    }


    @Test
    fun `verify long time memory review cases`() {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        every { timeUtils.getCurrentDate() } returns today

        val expectedToReviewsMap: List<Pair<CharacterReviewState, Map<LocalDate, Int>>> = listOf(
            CharacterReviewState.RecentlyReviewed to mapOf(
                today.minus(11, DateTimeUnit.DAY) to 0,
                today.minus(4, DateTimeUnit.DAY) to 0
            ),
            CharacterReviewState.NeedReview to mapOf(
                today.minus(11, DateTimeUnit.DAY) to 0,
                today.minus(8, DateTimeUnit.DAY) to 0
            ),
        )

        val character = Random.nextInt().toChar().toString()

        expectedToReviewsMap.forEachIndexed { index, (expected, map) ->
            println("Running test $index")
            coEvery { practiceRepository.getWritingReviewWithErrors(character) } coAnswers { map }
            val actual = runBlocking { useCase.getSummary(character, PracticeType.Writing) }
            assertEquals(expected, actual)
        }
    }

    private fun genReviewData(
        reviewStartDate: LocalDate,
        daysOfContinuousReviews: Long
    ) = (0 until daysOfContinuousReviews)
        .associate { reviewStartDate.plus(it, DateTimeUnit.DAY) to Random.nextInt(0, 2) }

}