package ua.syt0r.kanji.core.kanji_data_store

interface KanjiDataStoreContract {

    interface DataStore {
        fun getKanjiList(): List<String>
        fun getStrokes(kanji: String): List<String>
    }

}