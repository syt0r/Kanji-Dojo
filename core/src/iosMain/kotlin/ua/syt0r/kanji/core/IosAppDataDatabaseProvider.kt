package ua.syt0r.kanji.core

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import co.touchlab.sqliter.DatabaseConfiguration
import io.ktor.http.decodeURLPart
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import org.jetbrains.compose.resources.ExperimentalResourceApi
import ua.syt0r.kanji.Res
import ua.syt0r.kanji.core.app_data.AppDataDatabaseProvider
import ua.syt0r.kanji.core.app_data.AppDataDatabaseResourceName
import ua.syt0r.kanji.core.app_data.AppDataDatabaseVersion
import ua.syt0r.kanji.core.app_data.db.AppDataDatabase

class IosAppDataDatabaseProvider : AppDataDatabaseProvider {

    private val coroutineScope = CoroutineScope(context = Dispatchers.IO)

    @OptIn(ExperimentalResourceApi::class)
    override fun provideAsync(): Deferred<AppDataDatabase> = coroutineScope.async {
        val driver = NativeSqliteDriver(
            schema = AppDataDatabase.Schema,
            name = AppDataDatabaseResourceName,
            onConfiguration = { config ->
                config.copy(
                    version = AppDataDatabaseVersion.toInt(),
                    extendedConfig = DatabaseConfiguration.Extended(
                        basePath = Res.getUri("files/").decodeURLPart()
                    )
                )
            }
        )

        AppDataDatabase(driver)
    }

}

