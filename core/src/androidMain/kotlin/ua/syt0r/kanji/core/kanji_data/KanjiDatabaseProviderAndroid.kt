package ua.syt0r.kanji.core.kanji_data

import android.app.Application
import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import ua.syt0r.kanji.core.kanji_data.db.KanjiDatabase
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.core.readUserVersion
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

private const val KanjiDatabaseAssetName = "kanji_data.sqlite"
private const val KanjiDatabaseName = "kanji_data"

class KanjiDatabaseProviderAndroid(
    private val app: Application
) : KanjiDatabaseProvider {

    private val context = CoroutineScope(context = Dispatchers.IO)

    override fun provideAsync(): Deferred<KanjiDatabase> = context.async {
        Logger.d(">>")
        val dbFile = app.getDatabasePath(KanjiDatabaseName)

        if (!dbFile.exists()) {
            recreateDatabase(dbFile)
        }

        val driver = AndroidSqliteDriver(
            schema = KanjiDatabase.Schema.synchronous(),
            context = app,
            name = dbFile.name
        )

        val dbVersion = driver.readUserVersion()
        Logger.d("dbVersion[$dbVersion]")

        /***
         * KanjiDatabase.Schema.version value corresponds to latest migration file, so to increase
         * it it's necessary to create empty migration sqm file on 1 version lower than actual
         */
        if (dbVersion != KanjiDatabase.Schema.version) {
            Logger.d("Database with version[$dbVersion] is outdated, copying DB from assets")
            recreateDatabase(dbFile)
        }

        KanjiDatabase(driver).also { Logger.d("<<") }
    }

    private suspend fun recreateDatabase(dbFile: File) {
        app.deleteDatabase(KanjiDatabaseName)
        val input = app.assets.open(KanjiDatabaseAssetName)
        val path = dbFile.toPath()
        withContext(Dispatchers.IO) {
            Files.copy(input, path, StandardCopyOption.REPLACE_EXISTING)
        }
    }

}