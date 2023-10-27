package ua.syt0r.kanji.core

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver

suspend fun SqlDriver.readUserVersion(): Long {
    val queryResult = executeQuery(
        identifier = null,
        sql = "PRAGMA user_version;",
        mapper = { sqlCursor ->
            QueryResult.AsyncValue {
                val isSet = sqlCursor.next()
                if (isSet.await()) sqlCursor.getLong(0)!!
                else 0
            }
        },
        parameters = 0
    )
    return queryResult.await()
}

suspend fun SqlDriver.setUserVersion(version: Long) {
    execute(null, "PRAGMA user_version = $version;", 0).await()
}
