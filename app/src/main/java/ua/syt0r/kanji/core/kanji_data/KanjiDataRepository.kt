package ua.syt0r.kanji.core.kanji_data

import ua.syt0r.kanji.core.kanji_data.data.KanjiData
import ua.syt0r.kanji_dojo.shared.CharactersClassification
import ua.syt0r.kanji_dojo.shared.db.KanjiReadingTable

interface KanjiDataRepository {

    fun getStrokes(kanji: String): List<String>
    fun getMeanings(kanji: String): List<String>
    fun getReadings(kanji: String): Map<String, KanjiReadingTable.ReadingType>
    fun getData(kanji: String): KanjiData?
    fun getKanjiByJLPT(jlpt: CharactersClassification.JLPT): List<String>
    fun getKanjiByGrade(grade: CharactersClassification.Grade): List<String>

}