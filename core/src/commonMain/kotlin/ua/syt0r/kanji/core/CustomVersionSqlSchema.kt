package ua.syt0r.kanji.core

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlSchema

class CustomVersionSqlSchema(
    override val version: Long,
    schema: SqlSchema<QueryResult.Value<Unit>>
) : SqlSchema<QueryResult.Value<Unit>> by schema