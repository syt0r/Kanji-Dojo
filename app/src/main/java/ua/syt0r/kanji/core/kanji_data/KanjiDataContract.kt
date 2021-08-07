package ua.syt0r.kanji.core.kanji_data

import ua.syt0r.kanji.core.kanji_data.entity.KanjiClassificationGroup
import ua.syt0r.kanji_db_model.db.KanjiReadingTable

interface KanjiDataContract {

    interface Repository {

        fun getKanjiClassifications(): List<KanjiClassificationGroup>

        fun getStrokes(kanji: String): List<String>
        fun getMeanings(kanji: String): List<String>
        fun getReadings(kanji: String): Map<String, KanjiReadingTable.ReadingType>

    }

}