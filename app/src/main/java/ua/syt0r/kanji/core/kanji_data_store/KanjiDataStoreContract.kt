package ua.syt0r.kanji.core.kanji_data_store

import ua.syt0r.kanji.core.kanji_data_store.entity.KanjiData
import ua.syt0r.kanji_db_model.model.KanjiReadingData

interface KanjiDataStoreContract {

    interface DataStore {

        fun getKanjiList(): List<String>

        fun getStrokes(kanji: String): List<String>

        fun getReadings(kanji: String): KanjiReadingData

        fun getKanji(kanji: String): KanjiData

    }

}