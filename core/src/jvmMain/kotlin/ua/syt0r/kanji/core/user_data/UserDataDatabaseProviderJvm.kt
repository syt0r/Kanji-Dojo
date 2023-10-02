package ua.syt0r.kanji.core.user_data

import app.cash.sqldelight.db.AfterVersion
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import ua.syt0r.kanji.core.getUserDataDirectory
import ua.syt0r.kanji.core.readUserVersion
import ua.syt0r.kanji.core.user_data.db.UserDataDatabase
import java.io.File

class UserDataDatabaseProviderJvm(
    private val coroutineScope: CoroutineScope
) : UserDataDatabaseProvider {

    override fun provideAsync(): Deferred<UserDataDatabase> {
        return coroutineScope.async {
            val userDataDirectory = getUserDataDirectory()
            userDataDirectory.mkdirs()
            val databaseFile = File(userDataDirectory, "user_data.sqlite")
            val jdbcPath = "jdbc:sqlite:${databaseFile.absolutePath}"
            val driver = JdbcSqliteDriver(jdbcPath)
            if (!databaseFile.exists()) {
                UserDataDatabase.Schema.create(driver)
            } else {
                UserDataDatabase.Schema.migrate(
                    driver,
                    driver.readUserVersion(),
                    UserDataDatabase.Schema.version,
                    AfterVersion(3) {
                        runBlocking { UserDataDatabaseMigrationAfter3.handleMigrations(it) }
                    }
                )
            }
            UserDataDatabase(driver)
        }
    }

}