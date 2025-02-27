package ua.syt0r.kanji.core.user_data.database.migration

import app.cash.sqldelight.db.SqlDriver
import kotlinx.coroutines.runBlocking

actual fun SqlDriver.migrationScope(block: suspend () -> Unit) {
    runBlocking { block() }
}