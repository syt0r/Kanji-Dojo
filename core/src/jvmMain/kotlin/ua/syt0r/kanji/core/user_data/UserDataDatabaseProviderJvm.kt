package ua.syt0r.kanji.core.user_data

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import ua.syt0r.kanji.core.getUserDataDirectory
import ua.syt0r.kanji.core.user_data.db.UserDataDatabase
import java.io.File

object UserDataDatabaseProviderJvm : UserDataDatabaseProvider {

    private val context = CoroutineScope(context = Dispatchers.IO)

    override fun provideAsync(): Deferred<UserDataDatabase> {
        val userDataDirectory = getUserDataDirectory()
        userDataDirectory.mkdirs()
        val dbFile = File(userDataDirectory, "user_data.sqlite")
        val jdbcPath = "jdbc:sqlite:${dbFile.absolutePath}"
        return context.async {
            val driver = JdbcSqliteDriver(jdbcPath)
            if (!dbFile.exists()) {
                UserDataDatabase.Schema.create(driver)
            }
            UserDataDatabase(driver)
        }
    }

}