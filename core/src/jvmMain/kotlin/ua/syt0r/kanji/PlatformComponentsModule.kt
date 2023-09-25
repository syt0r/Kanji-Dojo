package ua.syt0r.kanji

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.module
import ua.syt0r.kanji.core.kanji_data.KanjiDatabaseProvider
import ua.syt0r.kanji.core.kanji_data.KanjiDatabaseProviderJvm
import ua.syt0r.kanji.core.logger.LoggerConfiguration
import ua.syt0r.kanji.core.user_data.JavaUserPreferencesRepository
import ua.syt0r.kanji.core.user_data.UserDataDatabaseProvider
import ua.syt0r.kanji.core.user_data.UserDataDatabaseProviderJvm
import ua.syt0r.kanji.core.user_data.UserPreferencesRepository
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings.SettingsScreenContentJvm
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings.SettingsScreenContract

actual val platformComponentsModule: Module = module {

    factory { LoggerConfiguration(true) }

    single<KanjiDatabaseProvider> {
        KanjiDatabaseProviderJvm()
    }

    single<UserPreferencesRepository> {
        JavaUserPreferencesRepository(
            preferences = JavaUserPreferencesRepository.defaultPreferences()
        )
    }

    single<UserDataDatabaseProvider> {
        UserDataDatabaseProviderJvm
    }

    single<SettingsScreenContract.Content> { SettingsScreenContentJvm }

}