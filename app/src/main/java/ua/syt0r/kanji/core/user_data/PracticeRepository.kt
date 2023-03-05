package ua.syt0r.kanji.core.user_data

import ua.syt0r.kanji.core.user_data.db.UserDataDao
import ua.syt0r.kanji.core.user_data.db.UserDataDatabase
import ua.syt0r.kanji.core.user_data.db.entity.PracticeEntity
import ua.syt0r.kanji.core.user_data.db.entity.PracticeEntryEntity
import ua.syt0r.kanji.core.user_data.db.entity.ReadingReviewEntity
import ua.syt0r.kanji.core.user_data.db.entity.WritingReviewEntity
import ua.syt0r.kanji.core.user_data.model.CharacterReviewResult
import ua.syt0r.kanji.core.user_data.model.Practice
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
            dao.update(PracticeEntity(id, title))
            charactersToAdd.forEach { dao.insert(PracticeEntryEntity(it, id)) }
            charactersToRemove.forEach { dao.delete(PracticeEntryEntity(it, id)) }
        }
    }


    override fun getAllPractices(): List<Practice> {
        return dao.getPracticeSets().map { Practice(id = it.id, name = it.name) }
    }

    override fun getPracticeInfo(id: Long): Practice {
        val entity = dao.getPracticeInfo(id)
        return Practice(entity.id, entity.name)
    }

    override fun getLatestWritingReviewTime(practiceId: Long): LocalDateTime? {
        return dao.getLastWritingReviewTime(practiceId)
    }

    override fun getLatestReadingReviewTime(practiceId: Long): LocalDateTime? {
        return dao.getLastReadingReviewTime(practiceId)
    }

    override suspend fun getKanjiForPractice(id: Long): List<String> {
        return dao.getPracticeEntries(id).map { it.character }
    }

    override suspend fun saveWritingReview(
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

    override suspend fun saveReadingReview(
        time: LocalDateTime,
        reviewResultList: List<CharacterReviewResult>
    ) {
        database.runInTransaction {
            reviewResultList.forEach {
                dao.insert(
                    ReadingReviewEntity(
                        character = it.character,
                        practiceSetId = it.practiceId,
                        reviewTime = time,
                        mistakes = it.mistakes
                    )
                )
            }
        }
    }

    override suspend fun getReviewedCharactersCount(): Long {
        return dao.getReviewedCharactersCount()
    }

    override suspend fun getWritingReviewWithErrors(character: String): Map<LocalDateTime, Int> {
        return dao.getWritingReviews(character).associate { it.reviewTime to it.mistakes }
    }

    override suspend fun getReadingReviewWithErrors(character: String): Map<LocalDateTime, Int> {
        return dao.getReadingReviews(character).associate { it.reviewTime to it.mistakes }
    }

}
