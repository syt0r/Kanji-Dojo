package ua.syt0r.kanji.core.user_data

import app.cash.sqldelight.db.AfterVersion
import kotlinx.coroutines.Deferred
import ua.syt0r.kanji.core.user_data.db.UserDataDatabase

interface UserDataDatabaseProvider {
    fun provideAsync(): Deferred<UserDataDatabase>
}

val userDataDatabaseMigrationCallbacks = arrayOf<AfterVersion>(
    AfterVersion(3) { UserDataDatabaseMigrationAfter3.handleMigrations(it) },
    AfterVersion(4) { UserDataDatabaseMigrationAfter4.handleMigrations(it) }
)