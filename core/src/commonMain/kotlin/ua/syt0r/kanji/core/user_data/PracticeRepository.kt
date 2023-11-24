package ua.syt0r.kanji.core.user_data

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import ua.syt0r.kanji.core.user_data.model.CharacterReadingReviewResult
import ua.syt0r.kanji.core.user_data.model.CharacterReviewResult
import ua.syt0r.kanji.core.user_data.model.CharacterStudyProgress
import ua.syt0r.kanji.core.user_data.model.CharacterWritingReviewResult
import ua.syt0r.kanji.core.user_data.model.Practice
import ua.syt0r.kanji.core.user_data.model.PracticeType

interface PracticeRepository {

    val practiceChangeFlow: Flow<Unit>

    suspend fun createPractice(title: String, characters: List<String>)
    suspend fun deletePractice(id: Long)
    suspend fun updatePractice(
        id: Long,
        title: String,
        charactersToAdd: List<String>,
        charactersToRemove: List<String>
    )

    suspend fun getAllPractices(): List<Practice>
    suspend fun getPracticeInfo(id: Long): Practice

    suspend fun getKanjiForPractice(id: Long): List<String>

    suspend fun saveWritingReviews(
        practiceTime: Instant,
        reviewResultList: List<CharacterWritingReviewResult>
    )

    suspend fun saveReadingReviews(
        practiceTime: Instant,
        reviewResultList: List<CharacterReadingReviewResult>
    )

    suspend fun getReviewedCharactersCount(): Long
    suspend fun getFirstReviewTime(character: String, type: PracticeType): Instant?
    suspend fun getLastReviewTime(practiceId: Long, type: PracticeType): Instant?
    suspend fun getStudyProgresses(): List<CharacterStudyProgress>

    suspend fun getReviews(start: Instant, end: Instant): Map<CharacterReviewResult, Instant>
    suspend fun getTotalReviewsCount(): Long
    suspend fun getTotalPracticeTime(): Long

}
