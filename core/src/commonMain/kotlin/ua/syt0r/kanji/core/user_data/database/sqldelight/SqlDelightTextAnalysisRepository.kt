package ua.syt0r.kanji.core.user_data.database.sqldelight

import kotlinx.datetime.Instant
import ua.syt0r.kanji.core.user_data.database.ObservableUserDataRepository
import ua.syt0r.kanji.core.user_data.database.TextAnalysisData
import ua.syt0r.kanji.core.user_data.database.TextAnalysisRepository
import ua.syt0r.kanji.core.user_data.database.UserDataDatabaseContract

class SqlDelightTextAnalysisRepository(
    manager: UserDataDatabaseContract.Manager
) : ObservableUserDataRepository(manager),
    TextAnalysisRepository {

    override suspend fun add(data: TextAnalysisData) = writeTransaction {
        data.run {
            insertTextAnalysisData(
                text = text,
                timestamp = timestamp.toEpochMilliseconds(),
                translation = translation,
                annotatedTextJson = annotatedTextJson
            )
        }
    }

    override suspend fun getCount(): Long = readTransaction {
        countTextAnalysisData().executeAsOne()
    }

    override suspend fun get(
        offset: Long,
        limit: Long
    ): List<TextAnalysisData> = readTransaction {
        selectAllTextAnalysisData(
            offset = offset,
            limit = limit
        ) { id, text, timestamp, translation, annotatedTextJson ->
            TextAnalysisData(
                text = text,
                timestamp = Instant.fromEpochSeconds(timestamp),
                translation = translation,
                annotatedTextJson = annotatedTextJson
            )
        }.executeAsList()
    }

}