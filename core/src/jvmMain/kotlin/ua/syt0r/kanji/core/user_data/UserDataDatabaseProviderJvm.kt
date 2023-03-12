package ua.syt0r.kanji.core.user_data

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import ua.syt0r.kanji.core.user_data.db.UserDataDatabase
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.notExists

object UserDataDatabaseProviderJvm : UserDataDatabaseProvider {

    private val context = CoroutineScope(context = Dispatchers.IO)

    private val dbFilePath = Path.of("user_data.sqlite")
    private val jdbcPath = "jdbc:sqlite:${dbFilePath.toAbsolutePath().absolutePathString()}"

    override fun provideAsync(): Deferred<UserDataDatabase> {
        return context.async {
            val driver = JdbcSqliteDriver(jdbcPath)
            if (dbFilePath.notExists()) {
                UserDataDatabase.Schema.create(driver)
            }
            UserDataDatabase(driver)
        }
    }

}