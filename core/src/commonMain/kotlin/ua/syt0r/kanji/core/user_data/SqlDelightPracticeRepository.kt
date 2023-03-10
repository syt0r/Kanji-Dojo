package ua.syt0r.kanji.core.user_data

import kotlinx.datetime.LocalDateTime
import ua.syt0r.kanji.core.user_data.model.CharacterReviewResult
import ua.syt0r.kanji.core.user_data.model.Practice

class SqlDelightPracticeRepository : PracticeRepository {

    override fun createPractice(title: String, characters: List<String>) {
        TODO("Not yet implemented")
    }

    override fun deletePractice(id: Long) {
        TODO("Not yet implemented")
    }

    override fun updatePractice(
        id: Long,
        title: String,
        charactersToAdd: List<String>,
        charactersToRemove: List<String>
    ) {
        TODO("Not yet implemented")
    }

    override fun getAllPractices(): List<Practice> {
        return emptyList()
    }

    override fun getPracticeInfo(id: Long): Practice {
        TODO("Not yet implemented")
    }

    override fun getLatestWritingReviewTime(practiceId: Long): LocalDateTime? {
        TODO("Not yet implemented")
    }

    override fun getLatestReadingReviewTime(practiceId: Long): LocalDateTime? {
        TODO("Not yet implemented")
    }

    override fun getKanjiForPractice(id: Long): List<String> {
        TODO("Not yet implemented")
    }

    override fun saveWritingReview(
        time: LocalDateTime,
        reviewResultList: List<CharacterReviewResult>,
        isStudyMode: Boolean
    ) {
        TODO("Not yet implemented")
    }

    override fun saveReadingReview(
        time: LocalDateTime,
        reviewResultList: List<CharacterReviewResult>
    ) {
        TODO("Not yet implemented")
    }

    override fun getReviewedCharactersCount(): Long {
        TODO("Not yet implemented")
    }

    override fun getWritingReviewWithErrors(character: String): Map<LocalDateTime, Int> {
        TODO("Not yet implemented")
    }

    override fun getReadingReviewWithErrors(character: String): Map<LocalDateTime, Int> {
        TODO("Not yet implemented")
    }


}
