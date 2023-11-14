package ua.syt0r.kanji.core

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import ua.syt0r.kanji.core.logger.Logger

suspend fun SqlDriver.readUserVersion(): Long {
    val queryResult = executeQuery(
        identifier = null,
        sql = "PRAGMA user_version;",
        mapper = { sqlCursor ->
            val isSet = sqlCursor.next().value
            QueryResult.Value(
                if (isSet) sqlCursor.getLong(0)!!
                else 0
            )
        },
        parameters = 0
    )
    return queryResult.value.also { Logger.d("db version = $it") }
}

suspend fun SqlDriver.setUserVersion(version: Long) {
    execute(null, "PRAGMA user_version = $version;", 0).await()
}
