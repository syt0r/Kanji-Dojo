package ua.syt0r.kanji.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.koin.dsl.module
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import ua.syt0r.kanji.core.analytics.PrintAnalyticsManager
import ua.syt0r.kanji.core.app_data.AppDataDatabaseProvider
import ua.syt0r.kanji.core.app_data.AppDataRepository
import ua.syt0r.kanji.core.app_data.SqlDelightAppDataRepository
import ua.syt0r.kanji.core.app_state.AppStateManager
import ua.syt0r.kanji.core.app_state.DefaultAppStateManager
import ua.syt0r.kanji.core.kanji_data.KanjiDataRepository
import ua.syt0r.kanji.core.kanji_data.KanjiDatabaseProvider
import ua.syt0r.kanji.core.kanji_data.SqlDelightKanjiDataRepository
import ua.syt0r.kanji.core.japanese.CharacterClassifier
import ua.syt0r.kanji.core.japanese.DefaultCharacterClassifier
import ua.syt0r.kanji.core.theme_manager.ThemeManager
import ua.syt0r.kanji.core.time.DefaultTimeUtils
import ua.syt0r.kanji.core.time.TimeUtils
import ua.syt0r.kanji.core.user_data.PracticeRepository
import ua.syt0r.kanji.core.user_data.SqlDelightPracticeRepository
import ua.syt0r.kanji.core.user_data.UserDataDatabaseProvider
import ua.syt0r.kanji.core.user_data.UserPreferencesRepository

val coreModule = module {

    single<AnalyticsManager> { PrintAnalyticsManager() }

    single<AppDataRepository> {
        val deferredDatabase = get<AppDataDatabaseProvider>().provideAsync()
        SqlDelightAppDataRepository(deferredDatabase)
    }

    single<PracticeRepository> {
        val provider = get<UserDataDatabaseProvider>()
        SqlDelightPracticeRepository(
            deferredDatabase = provider.provideAsync()
        )
    }

    factory<TimeUtils> { DefaultTimeUtils }

    single<ThemeManager> {
        val repository: UserPreferencesRepository = get()
        ThemeManager(
            getTheme = { runBlocking { repository.getTheme() } },
            setTheme = { runBlocking { repository.setTheme(it) } }
        )
    }

    single<AppStateManager> {
        DefaultAppStateManager(
            coroutineScope = CoroutineScope(Dispatchers.IO),
            userPreferencesRepository = get(),
            practiceRepository = get(),
            timeUtils = get()
        )
    }

    single<CharacterClassifier> { DefaultCharacterClassifier() }

}