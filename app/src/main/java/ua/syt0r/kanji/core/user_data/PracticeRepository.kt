package ua.syt0r.kanji.core.user_data

import ua.syt0r.kanji.core.user_data.db.UserDataDatabase
import ua.syt0r.kanji.core.user_data.db.entity.PracticeSetEntity
import ua.syt0r.kanji.core.user_data.db.entity.PracticeSetEntryEntity
import ua.syt0r.kanji.core.user_data.model.KanjiWritingReview
import ua.syt0r.kanji.core.user_data.model.Practice
import javax.inject.Inject

class PracticeRepository @Inject constructor(
    private val database: UserDataDatabase
) : UserDataContract.PracticeRepository {

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

    override suspend fun getAllPracticeSets(): List<Practice> {
        return dao.getPracticeSets().map {
            it.run {
                Practice(
                    id = id,
                    name = name
                )
            }
        }
    }

    override suspend fun getKanjiForPracticeSet(practiceSetId: Long): List<String> {
        return dao.getPracticeEntries(practiceSetId).map { it.kanji }
    }

    override suspend fun addKanjiToPracticeSet(practiceSetId: Long, kanjiList: List<String>) {
        TODO("Not yet implemented")
    }

    override suspend fun removeKanjiFromPracticeSet(practiceSetId: Long, kanjiList: List<String>) {
        TODO("Not yet implemented")
    }

    override suspend fun saveKanjiReview(reviewList: List<KanjiWritingReview>) {
        database.runInTransaction {

        }
    }

    override suspend fun getLatestReviewedPractice(): KanjiWritingReview {
        TODO("Not yet implemented")
    }

}
