package ua.syt0r.kanji.core.user_data

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import ua.syt0r.kanji.core.getUserDataDirectory
import ua.syt0r.kanji.core.readUserVersion
import ua.syt0r.kanji.core.user_data.db.UserDataDatabase
import java.io.File

class UserDataDatabaseManagerJvm(
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : BaseUserDataDatabaseManager(coroutineScope) {

    override suspend fun getDatabase(): DatabaseInstanceData {
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
                *getMigrationCallbacks()
            )
        }
        return DatabaseInstanceData(
            sqlDriver = driver,
            database = UserDataDatabase(driver)
        )
    }
}