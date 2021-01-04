package ua.syt0r.kanji.core.kanji_data_store.di

import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ua.syt0r.kanji.core.kanji_data_store.KanjiDataStore
import ua.syt0r.kanji.core.kanji_data_store.KanjiDataStoreContract
import ua.syt0r.kanji.core.kanji_data_store.KanjiDatabase
import ua.syt0r.kanji.core.kanji_data_store.dao.KanjiDatabaseDao

val kanjiDataStoreModule = module {

    single<KanjiDatabase> {
        KanjiDatabase.create(androidContext())
    }

    single<KanjiDataStoreContract.DataStore>(named<KanjiDatabaseDao>()) {
        get<KanjiDatabase>().kanjiDao()
    }

    single<KanjiDataStoreContract.DataStore> {
        KanjiDataStore(
            dao = get<KanjiDataStoreContract.DataStore>(named<KanjiDatabaseDao>())
        )
    }

}