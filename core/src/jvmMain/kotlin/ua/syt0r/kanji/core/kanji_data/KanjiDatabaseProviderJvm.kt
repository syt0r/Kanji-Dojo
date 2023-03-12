package ua.syt0r.kanji.core.kanji_data

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import ua.syt0r.kanji.core.kanji_data.db.KanjiDatabase
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.absolutePathString

class KanjiDatabaseProviderJvm : KanjiDatabaseProvider {

    override fun provide(): KanjiDatabase {
        val input = ClassLoader.getSystemResourceAsStream("kanji_data.sqlite")!!
        val path = Path.of("kanji_data.sqlite")
        Files.copy(input, path, StandardCopyOption.REPLACE_EXISTING)
        val driver = JdbcSqliteDriver("jdbc:sqlite:${path.toAbsolutePath().absolutePathString()}")
        return KanjiDatabase(driver)
    }

}