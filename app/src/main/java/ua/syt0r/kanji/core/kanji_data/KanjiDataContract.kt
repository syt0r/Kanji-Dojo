package ua.syt0r.kanji.core.kanji_data

import ua.syt0r.kanji_dojo.shared.db.KanjiReadingTable

interface KanjiDataContract {

    interface Repository {

        fun getStrokes(kanji: String): List<String>
        fun getMeanings(kanji: String): List<String>
        fun getReadings(kanji: String): Map<String, KanjiReadingTable.ReadingType>
        fun getData(kanji: String): KanjiData?

    }

}

