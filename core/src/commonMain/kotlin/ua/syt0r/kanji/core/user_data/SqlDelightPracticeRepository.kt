package ua.syt0r.kanji.core.user_data

import kotlinx.coroutines.Deferred
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ua.syt0r.kanji.core.user_data.db.UserDataDatabase
import ua.syt0r.kanji.core.user_data.model.CharacterReviewResult
import ua.syt0r.kanji.core.user_data.model.Practice
import ua.syt0r.kanji.core.userdata.db.PracticeQueries

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
        characters.forEach { insertPracticeEntry(it, practiceId) }
    }

    override suspend fun deletePractice(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun updatePractice(
        id: Long,
        title: String,
        charactersToAdd: List<String>,
        charactersToRemove: List<String>
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllPractices(): List<Practice> =
        runTransaction {
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
    ): LocalDateTime? = runTransaction {
        getLastReadingReview(practiceId).executeAsOneOrNull()
            ?.MAX
            ?.let { Instant.fromEpochMilliseconds(it) }
            ?.toLocalDateTime(TimeZone.currentSystemDefault())
    }

    override suspend fun getLatestReadingReviewTime(
        practiceId: Long
    ): LocalDateTime? = runTransaction {
        getLastReadingReview(practiceId).executeAsOneOrNull()
            ?.MAX
            ?.let { Instant.fromEpochMilliseconds(it) }
            ?.toLocalDateTime(TimeZone.currentSystemDefault())
    }

    override suspend fun getKanjiForPractice(id: Long): List<String> {
        TODO("Not yet implemented")
    }

    override suspend fun saveWritingReview(
        time: LocalDateTime,
        reviewResultList: List<CharacterReviewResult>,
        isStudyMode: Boolean
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun saveReadingReview(
        time: LocalDateTime,
        reviewResultList: List<CharacterReviewResult>
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun getReviewedCharactersCount(): Long {
        TODO("Not yet implemented")
    }

    override suspend fun getWritingReviewWithErrors(character: String): Map<LocalDateTime, Int> {
        TODO("Not yet implemented")
    }

    override suspend fun getReadingReviewWithErrors(character: String): Map<LocalDateTime, Int> {
        TODO("Not yet implemented")
    }


}
