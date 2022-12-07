package ua.syt0r.kanji.core.user_data

import ua.syt0r.kanji.core.user_data.db.UserDataDao
import ua.syt0r.kanji.core.user_data.db.UserDataDatabase
import ua.syt0r.kanji.core.user_data.db.entity.PracticeEntity
import ua.syt0r.kanji.core.user_data.db.entity.PracticeEntryEntity
import ua.syt0r.kanji.core.user_data.db.entity.WritingReviewEntity
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

    override suspend fun saveReview(
        time: LocalDateTime,
        reviewResultList: List<CharacterReviewResult>,
        isStudyMode: Boolean
    ) {
        database.runInTransaction {
            reviewResultList.forEach {
                dao.insert(
                    WritingReviewEntity(
                        character = it.character,
                        practiceSetId = it.practiceId,
                        reviewTime = time,
                        mistakes = it.mistakes,
                        isStudy = isStudyMode
                    )
                )
            }
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

    override suspend fun getCharactersFirstReviewTimestamps(
        practiceId: Long,
        maxMistakes: Int
    ): Map<String, LocalDateTime> {
        return dao.getFirstWritingReviewEntries(practiceId, maxMistakes)
            .associate { it.character to it.timestamp }
    }

    override suspend fun getCharactersLastReviewTimestamps(
        practiceId: Long,
        maxMistakes: Int
    ): Map<String, LocalDateTime> {
        return dao.getLastWritingReviewEntries(practiceId, maxMistakes)
            .associate { it.character to it.timestamp }
    }

    override suspend fun getReviewedCharactersCount(): Long {
        return dao.getReviewedCharactersCount()
    }

}
