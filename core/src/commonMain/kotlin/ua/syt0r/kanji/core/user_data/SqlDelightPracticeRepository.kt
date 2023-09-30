package ua.syt0r.kanji.core.user_data

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.datetime.Instant
import ua.syt0r.kanji.core.user_data.db.UserDataDatabase
import ua.syt0r.kanji.core.user_data.model.CharacterReviewOutcome
import ua.syt0r.kanji.core.user_data.model.CharacterReviewResult
import ua.syt0r.kanji.core.user_data.model.CharacterStudyProgress
import ua.syt0r.kanji.core.user_data.model.Practice
import ua.syt0r.kanji.core.user_data.model.PracticeType
import ua.syt0r.kanji.core.userdata.db.Character_progress
import ua.syt0r.kanji.core.userdata.db.PracticeQueries
import ua.syt0r.kanji.core.userdata.db.Reading_review
import ua.syt0r.kanji.core.userdata.db.Writing_review

class SqlDelightPracticeRepository(
    private val deferredDatabase: Deferred<UserDataDatabase>
) : PracticeRepository {

    private val updateChannel = Channel<Unit>()

    override val practiceChangeFlow: Flow<Unit> = updateChannel.consumeAsFlow()

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

    }.also { updateChannel.send(Unit) }

    override suspend fun deletePractice(id: Long) = runTransaction {
        deletePractice(id)
    }.also { updateChannel.send(Unit) }

    override suspend fun updatePractice(
        id: Long,
        title: String,
        charactersToAdd: List<String>,
        charactersToRemove: List<String>
    ) = runTransaction {
        updatePracticeTitle(title, id)
        charactersToAdd.forEach { insertOrIgnorePracticeEntry(it, id) }
        charactersToRemove.forEach { deletePracticeEntry(id, it) }
    }.also { updateChannel.send(Unit) }

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

    override suspend fun getKanjiForPractice(id: Long): List<String> = runTransaction {
        getPracticeEntriesForPractice(id).executeAsList().map { it.character }
    }

    override suspend fun saveWritingReviews(
        practiceTime: Instant,
        reviewResultList: List<CharacterReviewResult>,
    ) = runTransaction {
        val mode = practiceTypeToDBValue.getValue(PracticeType.Writing).toLong()
        reviewResultList.forEach {
            val currentProgress = getCharacterProgress(it.character, mode).executeAsOneOrNull()
                ?: Character_progress(
                    character = it.character,
                    mode = mode,
                    last_review_time = null,
                    repeats = 0,
                    lapses = 0
                )

            val updatedProgress = currentProgress.run {
                copy(
                    last_review_time = practiceTime.toEpochMilliseconds(),
                    repeats = if (it.outcome == CharacterReviewOutcome.Success) repeats + 1 else 1,
                    lapses = if (it.outcome == CharacterReviewOutcome.Success) lapses else lapses + 1
                )
            }

            upsertCharacterProgress(updatedProgress)

            insertWritingReview(
                Writing_review(
                    character = it.character,
                    practice_id = it.practiceId,
                    timestamp = practiceTime.toEpochMilliseconds(),
                    mistakes = it.mistakes.toLong(),
                    is_study = if (it.isStudy) 1 else 0,
                    duration = it.reviewDuration.inWholeMilliseconds,
                    outcome = it.outcome.toLong()
                )
            )
        }
    }.also { updateChannel.send(Unit) }

    override suspend fun saveReadingReviews(
        practiceTime: Instant,
        reviewResultList: List<CharacterReviewResult>
    ) = runTransaction {
        val mode = practiceTypeToDBValue.getValue(PracticeType.Reading).toLong()
        reviewResultList.forEach {
            val currentProgress = getCharacterProgress(it.character, mode).executeAsOneOrNull()
                ?: Character_progress(
                    character = it.character,
                    mode = mode,
                    last_review_time = null,
                    repeats = 0,
                    lapses = 0
                )

            val updatedProgress = currentProgress.run {
                copy(
                    last_review_time = practiceTime.toEpochMilliseconds(),
                    repeats = if (it.outcome == CharacterReviewOutcome.Success) repeats + 1 else 0,
                    lapses = if (it.outcome == CharacterReviewOutcome.Success) lapses else lapses + 1
                )
            }

            upsertCharacterProgress(updatedProgress)

            insertReadingReview(
                Reading_review(
                    character = it.character,
                    practice_id = it.practiceId,
                    timestamp = practiceTime.toEpochMilliseconds(),
                    mistakes = it.mistakes.toLong(),
                    duration = it.reviewDuration.inWholeMilliseconds,
                    outcome = it.outcome.toLong()
                )
            )
        }
    }.also { updateChannel.send(Unit) }

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

    override suspend fun getStudyProgresses(): List<CharacterStudyProgress> = runTransaction {
        getCharacterProgresses()
            .executeAsList()
            .map { dbItem ->
                CharacterStudyProgress(
                    character = dbItem.character,
                    practiceType = practiceTypeToDBValue.entries
                        .first { dbItem.mode == it.value }.key,
                    lastReviewTime = Instant.fromEpochMilliseconds(dbItem.last_review_time!!),
                    repeats = dbItem.repeats.toInt(),
                    lapses = dbItem.lapses.toInt()
                )
            }
    }

    override suspend fun getCharacterStudyProgressReviewedInRange(
        from: Instant,
        to: Instant
    ): List<CharacterStudyProgress> = runTransaction {
        getCharacterProgressReviewedInRange(from.toEpochMilliseconds(), to.toEpochMilliseconds())
            .executeAsList()
            .map { dbItem ->
                CharacterStudyProgress(
                    character = dbItem.character,
                    practiceType = practiceTypeToDBValue.entries
                        .first { it.value == dbItem.mode }
                        .key,
                    lastReviewTime = Instant.fromEpochMilliseconds(dbItem.last_review_time!!),
                    repeats = dbItem.repeats.toInt(),
                    lapses = dbItem.lapses.toInt()
                )
            }
    }

    private fun CharacterReviewOutcome.toLong(): Long = when (this) {
        CharacterReviewOutcome.Success -> 1
        CharacterReviewOutcome.Fail -> 0
    }

    private val practiceTypeToDBValue = mapOf(
        PracticeType.Writing to 0L,
        PracticeType.Reading to 1L
    )

}
