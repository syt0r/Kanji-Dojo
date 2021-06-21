package ua.syt0r.kanji.core.user_data

import kotlinx.coroutines.flow.Flow
import ua.syt0r.kanji.core.user_data.model.KanjiReviewInfo
import ua.syt0r.kanji.core.user_data.model.PracticeSetInfo
import java.time.LocalTime

interface UserDataContract {

    interface PreferencesRepository {

        fun areNotificationsEnabled(): Flow<Boolean>
        suspend fun setNotificationEnabled(enabled: Boolean)

        fun getNotificationDisplayTime(): Flow<LocalTime>
        suspend fun setNotificationDisplayTime(time: LocalTime)

    }

    interface WritingRepository {

        suspend fun createPracticeSet(kanjiList: List<String>, setName: String)
        suspend fun deletePracticeSet(practiceSetId: Long)

        suspend fun getAllPracticeSets(): List<PracticeSetInfo>
        suspend fun getKanjiForPracticeSet(practiceSetId: Long): List<String>

        suspend fun addKanjiToPracticeSet(practiceSetId: Long, kanjiList: List<String>)
        suspend fun removeKanjiFromPracticeSet(practiceSetId: Long, kanjiList: List<String>)

        suspend fun addKanjiReviewInfo(reviewInfo: KanjiReviewInfo)
        suspend fun getLatestReviewInfo(kanji: String, practiceSetId: Long): Flow<KanjiReviewInfo>

    }

}