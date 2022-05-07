package ua.syt0r.kanji.core.user_data

import ua.syt0r.kanji.core.user_data.db.UserDataDao
import ua.syt0r.kanji.core.user_data.db.UserDataDatabase
import ua.syt0r.kanji.core.user_data.db.converter.WritingReviewConverter
import ua.syt0r.kanji.core.user_data.db.entity.PracticeEntity
import ua.syt0r.kanji.core.user_data.db.entity.PracticeEntryEntity
import ua.syt0r.kanji.core.user_data.model.CharacterReviewResult
import ua.syt0r.kanji.core.user_data.model.Practice
import ua.syt0r.kanji.core.user_data.model.ReviewedPractice
import java.time.LocalDateTime
import javax.inject.Inject

class PracticeRepository @Inject constructor(
    private val database: UserDataDatabase,
    private val dao: UserDataDao = database.dao
) : UserDataContract.PracticeRepository {

    override suspend fun createPractice(title: String, characters: List<String>) {
        database.runInTransaction {

            val id = dao.upsert(PracticeEntity(name = title))

            characters.forEach {
                val characterEntity = PracticeEntryEntity(
                    character = it,
                    practiceId = id
                )
                dao.insert(characterEntity)
            }

        }
    }

    override suspend fun deletePractice(id: Long) {
        database.runInTransaction {
            dao.deletePractice(id)
        }
    }

    override suspend fun updatePractice(
        id: Long,
        title: String,
        charactersToAdd: List<String>,
        charactersToRemove: List<String>
    ) {
        database.runInTransaction {
            dao.upsert(PracticeEntity(id, title))
            charactersToAdd.forEach { dao.insert(PracticeEntryEntity(it, id)) }
            charactersToRemove.forEach { dao.delete(PracticeEntryEntity(it, id)) }
        }
    }


    override suspend fun getAllPractices(): List<ReviewedPractice> {
        return dao.getPracticeSets().map {
            it.run {
                ReviewedPractice(
                    id = id,
                    name = name,
                    timestamp = it.timestamp
                )
            }
        }
    }

    override suspend fun getPracticeInfo(id: Long): Practice {
        val entity = dao.getPracticeInfo(id)
        return Practice(entity.id, entity.name)
    }

    override suspend fun getKanjiForPractice(id: Long): List<String> {
        return dao.getPracticeEntries(id).map { it.character }
    }

    override suspend fun saveReview(reviewResultList: List<CharacterReviewResult>) {
        database.runInTransaction {
            reviewResultList.forEach { dao.insert(WritingReviewConverter.convert(it)) }
        }
    }

    override suspend fun getLatestReviewedPractice(): ReviewedPractice? {
        return dao.getLatestReviewedPractice()?.run {
            ReviewedPractice(
                practiceEntity.id,
                practiceEntity.name,
                writingReviewEntity.reviewTime
            )
        }
    }

    override suspend fun getCharactersReviewTimestamps(
        practiceId: Long,
        maxMistakes: Int
    ): Map<String, LocalDateTime> {
        return dao.getWritingReviewEntries(practiceId, maxMistakes)
            .associate { it.character to it.timestamp }
    }

}
