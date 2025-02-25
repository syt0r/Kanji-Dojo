package ua.syt0r.kanji.core.user_data

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import kotlinx.coroutines.Dispatchers
import ua.syt0r.kanji.core.getUserDataDirectory
import ua.syt0r.kanji.core.readUserVersion
import ua.syt0r.kanji.core.user_data.database.UserDataDatabaseContract
import ua.syt0r.kanji.core.user_data.database.UserDataDatabaseContract.DatabaseConnection
import ua.syt0r.kanji.core.user_data.db.UserDataDatabase
import java.io.File
import kotlin.coroutines.CoroutineContext

class JvmUserDataDatabasePlatformHandler(
    private val migrationProvider: UserDataDatabaseContract.MigrationProvider
) : UserDataDatabaseContract.PlatformHandler {

    companion object {
        private const val DEFAULT_DB_NAME = "user_data.sqlite"
    }

    override val connectionContext: CoroutineContext = Dispatchers.IO
    override val queryContext: CoroutineContext = Dispatchers.IO

    override suspend fun newConnection(): DatabaseConnection {
        val databaseFile = getDatabaseFile()
        databaseFile.parentFile.mkdirs()
        val jdbcPath = "jdbc:sqlite:${databaseFile.absolutePath}"
        val driver = JdbcSqliteDriver(jdbcPath)
        if (!databaseFile.exists()) {
            UserDataDatabase.Schema.create(driver)
        } else {
            UserDataDatabase.Schema.migrate(
                driver,
                driver.readUserVersion(),
                UserDataDatabase.Schema.version,
                *migrationProvider()
            )
        }
        return DatabaseConnection(
            sqlDriver = driver,
            database = UserDataDatabase(driver)
        )
    }

    override fun getDatabaseFile(): File {
        val userDataDirectory = getUserDataDirectory()
        return File(userDataDirectory, DEFAULT_DB_NAME)
    }

}
