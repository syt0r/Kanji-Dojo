package ua.syt0r.kanji.core.kanji_data_store.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ua.syt0r.kanji.core.kanji_data_store.KanjiDataStore
import ua.syt0r.kanji.core.kanji_data_store.KanjiDataStoreContract
import ua.syt0r.kanji.core.kanji_data_store.db.KanjiDataDatabase
import ua.syt0r.kanji.core.kanji_data_store.db.dao.KanjiDataDao

val kanjiDataStoreModule = module {

    single<KanjiDataDatabase> { KanjiDataDatabase.create(androidContext()) }

    single<KanjiDataDao> { get<KanjiDataDatabase>().dao }

    single<KanjiDataStoreContract.DataStore> {
        KanjiDataStore(
            kanjiDataDao = get()
        )
    }

}