package ua.syt0r.kanji.core.user_data

import kotlinx.datetime.LocalDateTime
import ua.syt0r.kanji.core.user_data.model.CharacterReviewResult
import ua.syt0r.kanji.core.user_data.model.Practice

interface PracticeRepository {

    fun createPractice(title: String, characters: List<String>)
    fun deletePractice(id: Long)
    fun updatePractice(
        id: Long,
        title: String,
        charactersToAdd: List<String>,
        charactersToRemove: List<String>
    )

    fun getAllPractices(): List<Practice>
    fun getPracticeInfo(id: Long): Practice
    fun getLatestWritingReviewTime(practiceId: Long): LocalDateTime?
    fun getLatestReadingReviewTime(practiceId: Long): LocalDateTime?

    fun getKanjiForPractice(id: Long): List<String>

    fun saveWritingReview(
        time: LocalDateTime,
        reviewResultList: List<CharacterReviewResult>,
        isStudyMode: Boolean
    )

    fun saveReadingReview(
        time: LocalDateTime,
        reviewResultList: List<CharacterReviewResult>
    )

    fun getReviewedCharactersCount(): Long

    fun getWritingReviewWithErrors(character: String): Map<LocalDateTime, Int>
    fun getReadingReviewWithErrors(character: String): Map<LocalDateTime, Int>

}
