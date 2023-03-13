package ua.syt0r.kanji

import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.dsl.module
import ua.syt0r.kanji.core.kanji_data.KanjiDataRepository
import ua.syt0r.kanji.core.kanji_data.KanjiDatabaseProviderAndroid
import ua.syt0r.kanji.core.kanji_data.SqlDelightKanjiDataRepository
import ua.syt0r.kanji.core.user_data.UserDataDatabaseProvider
import ua.syt0r.kanji.core.user_data.UserDataDatabaseProviderAndroid

actual val platformComponentsModule: Module = module {

    single<KanjiDataRepository> {
        val database = KanjiDatabaseProviderAndroid(androidApplication()).provide()
        SqlDelightKanjiDataRepository(database.kanjiDataQueries)
    }

    single<UserDataDatabaseProvider> {
        UserDataDatabaseProviderAndroid(
            app = androidApplication()
        )
    }

}