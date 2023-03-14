package ua.syt0r.kanji.core.kanji_data

import android.app.Application
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import kotlinx.coroutines.*
import ua.syt0r.kanji.core.kanji_data.db.KanjiDatabase
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class KanjiDatabaseProviderAndroid(
    private val app: Application
) : KanjiDatabaseProvider {

    private val context = CoroutineScope(context = Dispatchers.IO)

    override fun provideAsync(): Deferred<KanjiDatabase> = context.async {
        app.deleteDatabase("kanji_data")
        val dbFile = File(app.dataDir, "databases/kanji_data.db")

        val input = app.assets.open("kanji_data.sqlite")
        val path = dbFile.toPath()
        withContext(Dispatchers.IO) {
            Files.copy(input, path, StandardCopyOption.REPLACE_EXISTING)
        }

        val driver = AndroidSqliteDriver(KanjiDatabase.Schema, app, dbFile.name)
        KanjiDatabase(driver)
    }

}