package ua.syt0r.kanji.core.kanji_data

import ua.syt0r.kanji.core.kanji_data.entity.KanjiClassificationGroup

interface KanjiDataContract {

    interface Repository {

        fun getKanjiClassifications(): List<KanjiClassificationGroup>

        fun getStrokes(kanji: String): List<String>
        fun getMeanings(kanji: String): List<String>

    }

}