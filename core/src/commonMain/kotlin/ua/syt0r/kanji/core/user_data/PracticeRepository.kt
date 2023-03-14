package ua.syt0r.kanji.core.user_data

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import ua.syt0r.kanji.core.user_data.model.CharacterReviewResult
import ua.syt0r.kanji.core.user_data.model.Practice

interface PracticeRepository {

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
    suspend fun getLatestWritingReviewTime(practiceId: Long): LocalDateTime?
    suspend fun getLatestReadingReviewTime(practiceId: Long): LocalDateTime?

    suspend fun getKanjiForPractice(id: Long): List<String>

    suspend fun saveWritingReview(
        time: Instant,
        reviewResultList: List<CharacterReviewResult>,
        isStudyMode: Boolean
    )

    suspend fun saveReadingReview(
        time: LocalDateTime,
        reviewResultList: List<CharacterReviewResult>
    )

    suspend fun getReviewedCharactersCount(): Long

    suspend fun getWritingReviewWithErrors(character: String): Map<LocalDateTime, Int>
    suspend fun getReadingReviewWithErrors(character: String): Map<LocalDateTime, Int>

}
