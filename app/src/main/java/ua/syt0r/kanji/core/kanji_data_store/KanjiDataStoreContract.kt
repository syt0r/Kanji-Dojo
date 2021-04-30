package ua.syt0r.kanji.core.kanji_data_store

import ua.syt0r.kanji.core.kanji_data_store.entity.KanjiClassificationGroup

interface KanjiDataStoreContract {

    interface DataStore {

        fun getKanjiClassifications(): List<KanjiClassificationGroup>

        fun getStrokes(kanji: String): List<String>
        fun getMeanings(kanji: String): List<String>

    }

}