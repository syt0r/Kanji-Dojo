package ua.syt0r.kanji

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import ua.syt0r.kanji.core.time.TimeUtils
import ua.syt0r.kanji.core.user_data.UserDataContract
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.CharacterReviewState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.use_case.PracticePreviewCharacterReviewSummary
import java.time.LocalDate
import kotlin.random.Random

class PracticePreviewCharacterStateUseCaseTest {

    lateinit var useCase: PracticePreviewCharacterReviewSummary

    @MockK
    lateinit var practiceRepository: UserDataContract.PracticeRepository

    @MockK
    lateinit var timeUtils: TimeUtils

    @Before
    fun init() {
        MockKAnnotations.init(this)
        useCase = PracticePreviewCharacterReviewSummary(practiceRepository, timeUtils)
    }


    @Test
    fun `verify continuous review cases`() {
        val today = LocalDate.now()
        every { timeUtils.getCurrentDay() } returns today

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
                reviewStartDate = today.minusDays(1),
                daysOfContinuousReviews = 1
            ),
            CharacterReviewState.RecentlyReviewed to genReviewData(
                reviewStartDate = today.minusDays(1),
                daysOfContinuousReviews = 2
            ),
            CharacterReviewState.NeedReview to genReviewData(
                reviewStartDate = today.minusDays(2),
                daysOfContinuousReviews = 1
            ),
            CharacterReviewState.RecentlyReviewed to genReviewData(
                reviewStartDate = today.minusDays(2),
                daysOfContinuousReviews = 2
            ),
            CharacterReviewState.RecentlyReviewed to genReviewData(
                reviewStartDate = today.minusDays(3),
                daysOfContinuousReviews = 2
            ),
            CharacterReviewState.NeedReview to genReviewData(
                reviewStartDate = today.minusDays(3),
                daysOfContinuousReviews = 2
            ).plus(today.minusDays(2) to Random.nextInt(10, 20)),
            CharacterReviewState.NeedReview to genReviewData(
                reviewStartDate = today.minusDays(4),
                daysOfContinuousReviews = 2
            ),
            CharacterReviewState.RecentlyReviewed to genReviewData(
                reviewStartDate = today.minusDays(5),
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
        val today = LocalDate.now()
        every { timeUtils.getCurrentDay() } returns today

        val expectedToReviewsMap: List<Pair<CharacterReviewState, Map<LocalDate, Int>>> = listOf(
            CharacterReviewState.RecentlyReviewed to mapOf(
                today.minusDays(11) to 0,
                today.minusDays(4) to 0
            ),
            CharacterReviewState.NeedReview to mapOf(
                today.minusDays(11) to 0,
                today.minusDays(8) to 0
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

    private fun genReviewData(
        reviewStartDate: LocalDate,
        daysOfContinuousReviews: Long
    ) = (0 until daysOfContinuousReviews)
        .associate { reviewStartDate.plusDays(it) to Random.nextInt(0, 2) }

}