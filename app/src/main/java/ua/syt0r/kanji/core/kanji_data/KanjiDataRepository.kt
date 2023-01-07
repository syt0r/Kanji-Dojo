package ua.syt0r.kanji.core.kanji_data

import ua.syt0r.kanji.core.kanji_data.data.JapaneseWord
import ua.syt0r.kanji.core.kanji_data.data.KanjiData
import ua.syt0r.kanji.common.CharactersClassification
import ua.syt0r.kanji.common.db.schema.KanjiReadingTableSchema

interface KanjiDataRepository {

    fun getStrokes(kanji: String): List<String>
    fun getMeanings(kanji: String): List<String>
    fun getReadings(kanji: String): Map<String, KanjiReadingTableSchema.ReadingType>
    fun getData(kanji: String): KanjiData?
    fun getKanjiByJLPT(jlpt: CharactersClassification.JLPT): List<String>
    fun getKanjiByGrade(grade: CharactersClassification.Grade): List<String>
    fun getWordsWithCharacter(char: String): List<JapaneseWord>

}