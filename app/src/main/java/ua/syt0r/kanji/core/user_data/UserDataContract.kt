package ua.syt0r.kanji.core.user_data

import ua.syt0r.kanji.core.user_data.model.KanjiWritingReview
import ua.syt0r.kanji.core.user_data.model.Practice
import ua.syt0r.kanji.core.user_data.model.ReviewedPractice
import java.time.LocalDateTime

interface UserDataContract {

    interface PreferencesRepository {

    }

    interface PracticeRepository {

        suspend fun createPracticeSet(kanjiList: List<String>, setName: String)
        suspend fun deletePracticeSet(practiceSetId: Long)

        suspend fun getAllPracticeSets(): List<ReviewedPractice>
        suspend fun getPracticeInfo(practiceId: Long): Practice
        suspend fun getKanjiForPracticeSet(practiceSetId: Long): List<String>

        suspend fun addKanjiToPracticeSet(practiceSetId: Long, kanjiList: List<String>)
        suspend fun removeKanjiFromPracticeSet(practiceSetId: Long, kanjiList: List<String>)

        suspend fun saveKanjiReview(reviewList: List<KanjiWritingReview>)
        suspend fun getLatestReviewedPractice(): ReviewedPractice?

        suspend fun getCharactersReviewTimestamps(
            practiceId: Long, maxMistakes: Int
        ): Map<String, LocalDateTime>

    }

}