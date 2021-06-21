package ua.syt0r.kanji.core.user_data

import kotlinx.coroutines.flow.Flow
import ua.syt0r.kanji.core.user_data.db.UserDataDatabase
import ua.syt0r.kanji.core.user_data.db.entity.PracticeSetEntity
import ua.syt0r.kanji.core.user_data.db.entity.PracticeSetEntryEntity
import ua.syt0r.kanji.core.user_data.model.KanjiReviewInfo
import ua.syt0r.kanji.core.user_data.model.PracticeSetInfo
import java.time.LocalDateTime
import javax.inject.Inject

class WritingRepository @Inject constructor(
    private val database: UserDataDatabase
) : UserDataContract.WritingRepository {

    private val dao = database.dao

    override suspend fun createPracticeSet(kanjiList: List<String>, setName: String) {
        database.runInTransaction {

            val setEntity = PracticeSetEntity(name = setName)
            val setId = dao.createPracticeSet(setEntity)

            kanjiList.forEach { kanji ->
                val kanjiEntity = PracticeSetEntryEntity(
                    kanji = kanji,
                    practiceSetId = setId
                )
                dao.createPracticeSetEntry(kanjiEntity)
            }

        }
    }

    override suspend fun deletePracticeSet(practiceSetId: Long) {
        database.runInTransaction {
            dao.deletePracticeSet(practiceSetId)
        }
    }

    override suspend fun getAllPracticeSets(): List<PracticeSetInfo> {
        return dao.getPracticeSets().map {
            it.run {
                PracticeSetInfo(
                    id = id,
                    name = name,
                    previewKanji = dao.getPracticeSetEntryBy(practiceSetId = id)!!.kanji,
                    latestReviewTime = LocalDateTime.now()
                )
            }
        }
    }

    override suspend fun getKanjiForPracticeSet(practiceSetId: Long): List<String> {
        TODO("Not yet implemented")
    }

    override suspend fun addKanjiToPracticeSet(practiceSetId: Long, kanjiList: List<String>) {
        TODO("Not yet implemented")
    }

    override suspend fun removeKanjiFromPracticeSet(practiceSetId: Long, kanjiList: List<String>) {
        TODO("Not yet implemented")
    }

    override suspend fun addKanjiReviewInfo(reviewInfo: KanjiReviewInfo) {
        TODO("Not yet implemented")
    }

    override suspend fun getLatestReviewInfo(
        kanji: String,
        practiceSetId: Long
    ): Flow<KanjiReviewInfo> {
        TODO("Not yet implemented")
    }

}
