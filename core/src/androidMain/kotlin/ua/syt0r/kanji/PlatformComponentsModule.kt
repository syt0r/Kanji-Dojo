package ua.syt0r.kanji

import kotlinx.coroutines.runBlocking
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module
import ua.syt0r.kanji.core.AndroidThemeManager
import ua.syt0r.kanji.core.kanji_data.KanjiDatabaseProvider
import ua.syt0r.kanji.core.kanji_data.KanjiDatabaseProviderAndroid
import ua.syt0r.kanji.core.theme_manager.ThemeManager
import ua.syt0r.kanji.core.user_data.AndroidUserPreferencesRepository
import ua.syt0r.kanji.core.user_data.UserDataDatabaseProvider
import ua.syt0r.kanji.core.user_data.UserDataDatabaseProviderAndroid
import ua.syt0r.kanji.core.user_data.UserPreferencesRepository

actual val platformComponentsModule: Module = module {

    single<KanjiDatabaseProvider> {
        KanjiDatabaseProviderAndroid(
            app = androidApplication()
        )
    }

    single<UserDataDatabaseProvider> {
        UserDataDatabaseProviderAndroid(
            app = androidApplication()
        )
    }

    single<UserPreferencesRepository> {
        AndroidUserPreferencesRepository(
            context = androidContext(),
            defaultAnalyticsEnabled = false,
            defaultAnalyticsSuggestionEnabled = false
        )
    }

    single<ThemeManager> {
        val repository: UserPreferencesRepository = get()
        AndroidThemeManager(
            getTheme = { runBlocking { repository.getTheme() } },
            setTheme = { runBlocking { repository.setTheme(it) } }
        )
    }

}