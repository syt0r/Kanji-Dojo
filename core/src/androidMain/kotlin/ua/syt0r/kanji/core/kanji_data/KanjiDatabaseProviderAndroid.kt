package ua.syt0r.kanji.core.kanji_data

import android.app.Application
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import ua.syt0r.kanji.core.kanji_data.db.KanjiDatabase
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.core.readSqliteUserVersion
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
        val dbVersion = kotlin.runCatching { readSqliteUserVersion(dbFile) }
            .getOrElse {
                Logger.d("Error when reading db version: $it")
                null
            }
        Logger.d("dbVersion[$dbVersion]")

        /***
         * KanjiDatabase.Schema.version value corresponds to latest migration file, so to increase
         * it it's necessary to create empty migration sqm file on 1 version lower than actual
         */
        if (dbVersion != KanjiDatabase.Schema.version) {
            Logger.d("Database with version[$dbVersion] is outdated, copying DB from assets")
            app.deleteDatabase(KanjiDatabaseName)
            val input = app.assets.open(KanjiDatabaseAssetName)
            val path = dbFile.toPath()
            withContext(Dispatchers.IO) {
                Files.copy(input, path, StandardCopyOption.REPLACE_EXISTING)
            }
        }

        val driver = AndroidSqliteDriver(
            schema = KanjiDatabase.Schema,
            context = app,
            name = dbFile.name
        )
        KanjiDatabase(driver).also { Logger.d("<<") }
    }

}