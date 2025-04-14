package ua.syt0r.kanji.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin
import okio.Path.Companion.toPath
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.koin.core.module.Module
import org.koin.dsl.module
import ua.syt0r.kanji.IosMainBuildConfig
import ua.syt0r.kanji.Res
import ua.syt0r.kanji.core.IosAppDataDatabaseProvider
import ua.syt0r.kanji.core.IosUserDataDatabasePlatformHandler
import ua.syt0r.kanji.core.app_data.AppDataDatabaseProvider
import ua.syt0r.kanji.core.file.IosPlatformFileHandler
import ua.syt0r.kanji.core.file.PlatformFileHandler
import ua.syt0r.kanji.core.formattedIosFilePath
import ua.syt0r.kanji.core.getPrivateAppDataDirPath
import ua.syt0r.kanji.core.logger.LoggerConfiguration
import ua.syt0r.kanji.core.sync.IosSyncBackupFileProvider
import ua.syt0r.kanji.core.sync.SyncBackupFileProvider
import ua.syt0r.kanji.core.tts.KanaVoiceData
import ua.syt0r.kanji.core.tts.Neural2BKanaVoiceData
import ua.syt0r.kanji.core.user_data.database.UserDataDatabaseContract
import ua.syt0r.kanji.core.user_data.preferences.DefaultUserPreferencesMigrationManager
import ua.syt0r.kanji.presentation.IosAccountScreenContent
import ua.syt0r.kanji.presentation.IosSponsorScreenContent
import ua.syt0r.kanji.presentation.addAccountScreenComponents
import ua.syt0r.kanji.presentation.backupScreenComponents
import ua.syt0r.kanji.presentation.screen.main.screen.account.AccountScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.sponsor.SponsorScreenContract

@OptIn(ExperimentalResourceApi::class)
actual val platformComponentsModule: Module = module {

    factory { LoggerConfiguration(true) }

    factory<HttpClientEngineFactory<*>> { Darwin }

    single<AppDataDatabaseProvider> {
        IosAppDataDatabaseProvider()
    }

    single<SyncBackupFileProvider> {
        IosSyncBackupFileProvider()
    }

    single<DataStore<*>> {
        PreferenceDataStoreFactory.createWithPath(
            migrations = DefaultUserPreferencesMigrationManager.DefaultMigrations,
            produceFile = { (getPrivateAppDataDirPath() + "/preferences.preferences_pb").toPath() }
        )
    }

    single<UserDataDatabaseContract.PlatformHandler> {
        IosUserDataDatabasePlatformHandler(
            migrationProvider = get()
        )
    }

    factory<PlatformFileHandler> {
        IosPlatformFileHandler()
    }

    factory<KanaVoiceData> {
        Neural2BKanaVoiceData(
            assetPath = Res.getUri("files/${IosMainBuildConfig.kanaVoiceAssetName}")
                .formattedIosFilePath()
        )
    }

    single<SponsorScreenContract.Content> { IosSponsorScreenContent }
    single<AccountScreenContract.Content> { IosAccountScreenContent }
    backupScreenComponents()
    addAccountScreenComponents()

}