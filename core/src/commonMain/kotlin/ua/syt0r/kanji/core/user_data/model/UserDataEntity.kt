package ua.syt0r.kanji.core.user_data.model

import kotlinx.datetime.Instant
import kotlin.math.roundToLong
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.DurationUnit

data class Practice(
    val id: Long,
    val name: String,
    val position: Int,
)

data class CharacterStudyProgress(
    val character: String,
    val practiceType: PracticeType,
    val lastReviewTime: Instant,
    val repeats: Int,
    val lapses: Int
) {

    fun getExpectedReviewTime(srsInterval: Float): Instant {
        val additionalMillis = srsInterval * 1.days.toLong(DurationUnit.MILLISECONDS) * repeats
        return Instant.fromEpochMilliseconds(lastReviewTime.toEpochMilliseconds() + additionalMillis.roundToLong())
    }

}

enum class CharacterReviewOutcome { Success, Fail }

interface CharacterReviewResult {
    val character: String
    val practiceId: Long
    val mistakes: Int
    val reviewDuration: Duration
    val outcome: CharacterReviewOutcome
}

data class CharacterWritingReviewResult(
    override val character: String,
    override val practiceId: Long,
    override val mistakes: Int,
    override val reviewDuration: Duration,
    override val outcome: CharacterReviewOutcome,
    val isStudy: Boolean
) : CharacterReviewResult

data class CharacterReadingReviewResult(
    override val character: String,
    override val practiceId: Long,
    override val mistakes: Int,
    override val reviewDuration: Duration,
    override val outcome: CharacterReviewOutcome,
) : CharacterReviewResult
