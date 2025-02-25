package ua.syt0r.kanji.core.user_data.database.migration

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import app.cash.sqldelight.db.AfterVersion
import kotlinx.coroutines.runBlocking
import ua.syt0r.kanji.core.app_data.AppDataRepository
import ua.syt0r.kanji.core.user_data.database.UserDataDatabaseContract

class UserDataDatabaseMigrationProvider(
    private val preferences: DataStore<Preferences>,
    val appDataRepository: AppDataRepository
) : UserDataDatabaseContract.MigrationProvider {

    override fun invoke(): Array<AfterVersion> = listOf(
        UserDataDatabaseMigrationAfter3,
        UserDataDatabaseMigrationAfter4,
        UserDataDatabaseMigrationAfter8,
        UserDataDatabaseMigrationAfter9(preferences, appDataRepository),
    ).map { migration ->
        AfterVersion(migration.version) { runBlocking { migration.execute(it) } }
    }.toTypedArray()

}