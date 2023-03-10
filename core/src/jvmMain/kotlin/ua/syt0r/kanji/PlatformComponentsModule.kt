package ua.syt0r.kanji

import org.koin.core.module.Module
import org.koin.dsl.module
import ua.syt0r.kanji.core.kanji_data.KanjiDataRepository
import ua.syt0r.kanji.core.kanji_data.KanjiDatabaseProviderJvm
import ua.syt0r.kanji.core.kanji_data.SqlDelightKanjiDataRepository
import ua.syt0r.kanji.core.user_data.JavaUserPreferencesRepository
import ua.syt0r.kanji.core.user_data.UserPreferencesRepository

actual val platformComponentsModule: Module = module {

    single<KanjiDataRepository> {
        val database = KanjiDatabaseProviderJvm().provide()
        SqlDelightKanjiDataRepository(database.kanjiDataQueries)
    }

    single<UserPreferencesRepository> {
        JavaUserPreferencesRepository(
            preferences = JavaUserPreferencesRepository.defaultPreferences()
        )
    }

}