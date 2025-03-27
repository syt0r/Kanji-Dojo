package ua.syt0r.kanji.core.user_data

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.jvm.javaio.toByteReadChannel
import ua.syt0r.kanji.core.getUserDataDirectory
import ua.syt0r.kanji.core.readUserVersion
import ua.syt0r.kanji.core.user_data.database.DatabaseConnection
import ua.syt0r.kanji.core.user_data.database.UserDataDatabaseContract
import ua.syt0r.kanji.core.user_data.db.UserDataDatabase
import java.io.File

class JvmUserDataDatabasePlatformHandler(
    private val migrationProvider: UserDataDatabaseContract.MigrationProvider
) : UserDataDatabaseContract.PlatformHandler {

    companion object {
        private const val DEFAULT_DB_NAME = "user_data.sqlite"
    }

    override suspend fun newConnection(): DatabaseConnection {
        val databaseFile = getDBFile()
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

    override fun readDatabaseFile(): ByteReadChannel {
        return getDBFile().inputStream().toByteReadChannel()
    }

    private fun getDBFile(): File {
        val userDataDirectory = getUserDataDirectory()
        return File(userDataDirectory, DEFAULT_DB_NAME)
    }

}
