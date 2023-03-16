package ua.syt0r.kanji.core.user_data

import kotlinx.coroutines.Deferred
import kotlinx.datetime.Instant
import ua.syt0r.kanji.core.user_data.db.UserDataDatabase
import ua.syt0r.kanji.core.user_data.model.CharacterReviewResult
import ua.syt0r.kanji.core.user_data.model.Practice
import ua.syt0r.kanji.core.userdata.db.PracticeQueries
import ua.syt0r.kanji.core.userdata.db.Reading_review
import ua.syt0r.kanji.core.userdata.db.Writing_review

class SqlDelightPracticeRepository(
    private val deferredDatabase: Deferred<UserDataDatabase>
) : PracticeRepository {

    private suspend fun <T> runTransaction(
        transactionScope: PracticeQueries.() -> T
    ): T {
        val queries = deferredDatabase.await().practiceQueries
        return queries.transactionWithResult { queries.transactionScope() }
    }

    override suspend fun createPractice(
        title: String,
        characters: List<String>
    ) = runTransaction {
        insertPractice(name = title)

        val practiceId = getLastInsertRowId().executeAsOne()
        characters.forEach { insertOrIgnorePracticeEntry(it, practiceId) }
    }

    override suspend fun deletePractice(id: Long) = runTransaction {
        deletePractice(id)
    }

    override suspend fun updatePractice(
        id: Long,
        title: String,
        charactersToAdd: List<String>,
        charactersToRemove: List<String>
    ) = runTransaction {
        updatePracticeTitle(title, id)
        charactersToAdd.forEach { insertOrIgnorePracticeEntry(it, id) }
        charactersToRemove.forEach { deletePracticeEntry(id, it) }
    }

    override suspend fun getAllPractices(): List<Practice> = runTransaction {
        getAllPractices().executeAsList().map {
            Practice(it.id, it.name)
        }
    }

    override suspend fun getPracticeInfo(
        id: Long
    ): Practice = runTransaction {
        getPractice(id).executeAsOne().run { Practice(id, name) }
    }

    override suspend fun getLatestWritingReviewTime(
        practiceId: Long
    ): Instant? = runTransaction {
        getLastWritingReview(practiceId).executeAsOneOrNull()
            ?.time
            ?.let { Instant.fromEpochMilliseconds(it) }
    }

    override suspend fun getLatestReadingReviewTime(
        practiceId: Long
    ): Instant? = runTransaction {
        getLastReadingReview(practiceId).executeAsOneOrNull()
            ?.time
            ?.let { Instant.fromEpochMilliseconds(it) }
    }

    override suspend fun getKanjiForPractice(id: Long): List<String> = runTransaction {
        getPracticeEntriesForPractice(id).executeAsList().map { it.character }
    }

    override suspend fun saveWritingReview(
        time: Instant,
        reviewResultList: List<CharacterReviewResult>,
        isStudyMode: Boolean
    ) = runTransaction {
        reviewResultList.forEach {
            insertWritingReview(
                Writing_review(
                    it.character,
                    it.practiceId,
                    time.toEpochMilliseconds(),
                    it.mistakes.toLong(),
                    if (isStudyMode) 1 else 0
                )
            )
        }
    }

    override suspend fun saveReadingReview(
        time: Instant,
        reviewResultList: List<CharacterReviewResult>
    ) = runTransaction {
        reviewResultList.forEach {
            insertReadingReview(
                Reading_review(
                    it.character,
                    it.practiceId,
                    time.toEpochMilliseconds(),
                    mistakes = it.mistakes.toLong()
                )
            )
        }
    }

    override suspend fun getReviewedCharactersCount(): Long = runTransaction {
        getWirtingReviewsCharactersCount().executeAsOne()
    }

    override suspend fun getWritingReviewWithErrors(
        character: String
    ): Map<Instant, Int> = runTransaction {
        getWritingReviews(character)
            .executeAsList()
            .associate { Instant.fromEpochMilliseconds(it.timestamp) to it.mistakes.toInt() }
    }

    override suspend fun getReadingReviewWithErrors(
        character: String
    ): Map<Instant, Int> = runTransaction {
        getReadingReviews(character)
            .executeAsList()
            .associate { Instant.fromEpochMilliseconds(it.timestamp) to it.mistakes.toInt() }
    }

}
