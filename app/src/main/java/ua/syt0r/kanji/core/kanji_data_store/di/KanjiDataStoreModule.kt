package ua.syt0r.kanji.core.kanji_data_store.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ua.syt0r.kanji.core.kanji_data_store.KanjiDataStore
import ua.syt0r.kanji.core.kanji_data_store.KanjiDataStoreContract
import ua.syt0r.kanji.core.kanji_data_store.db.KanjiDatabase
import ua.syt0r.kanji.core.kanji_data_store.db.dao.KanjiDatabaseDao

val kanjiDataStoreModule = module {

    single<KanjiDatabase> { KanjiDatabase.create(androidContext()) }

    single<KanjiDatabaseDao> { get<KanjiDatabase>().kanjiDao() }

    single<KanjiDataStoreContract.DataStore> {
        KanjiDataStore(
            dao = get()
        )
    }

}