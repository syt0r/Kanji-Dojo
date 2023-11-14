package ua.syt0r.kanji.core.kanji_data

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import kotlinx.coroutines.*
import ua.syt0r.kanji.core.getUserDataDirectory
import ua.syt0r.kanji.core.kanji_data.db.KanjiDatabase
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.core.readUserVersion
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class KanjiDatabaseProviderJvm : KanjiDatabaseProvider {

    private val context = CoroutineScope(context = Dispatchers.IO)

    override fun provideAsync(): Deferred<KanjiDatabase> = context.async {
        val input = ClassLoader.getSystemResourceAsStream("kanji_data.sqlite")!!
        val dataDirectory = getUserDataDirectory()
        dataDirectory.mkdirs()
        val dbFile = File(dataDirectory, "kanji_data.sqlite")
        withContext(Dispatchers.IO) {
            Files.copy(input, dbFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
        val driver = JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePath}")
        val dbVersion = driver.readUserVersion()
        Logger.d("dbVersion[$dbVersion]")
        KanjiDatabase(driver)
    }

}