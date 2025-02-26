package ua.syt0r.kanji.core.user_data.database.migration

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import app.cash.sqldelight.db.AfterVersion
import app.cash.sqldelight.db.SqlDriver
import kotlinx.coroutines.runBlocking
import ua.syt0r.kanji.core.app_data.AppDataRepository
import ua.syt0r.kanji.core.user_data.database.UserDataDatabaseContract

class UserDataDatabaseMigrationProvider(
    private val preferences: DataStore<Preferences>,
    val appDataRepository: AppDataRepository,
    private val observable: UserDataDatabaseContract.MigrationObservable
) : UserDataDatabaseContract.MigrationProvider {

    override fun invoke(): Array<AfterVersion> = listOf(
        UserDataDatabaseMigrationAfter3,
        UserDataDatabaseMigrationAfter4,
        UserDataDatabaseMigrationAfter8,
        UserDataDatabaseMigrationAfter10(preferences, appDataRepository, observable),
    ).map { migration ->
        AfterVersion(migration.version) {
            // For safety and to be able to use last_insert_rowid
            it.ensureMigrationTransactionEnabled()

            runBlocking { migration.execute(it) }
        }
    }.toTypedArray()


}

expect fun SqlDriver.ensureMigrationTransactionEnabled()