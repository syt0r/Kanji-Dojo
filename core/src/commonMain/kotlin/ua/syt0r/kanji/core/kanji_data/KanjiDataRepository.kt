package ua.syt0r.kanji.core.kanji_data

import ua.syt0r.kanji.core.japanese.CharactersClassification
import ua.syt0r.kanji.core.kanji_data.data.CharacterRadical
import ua.syt0r.kanji.core.kanji_data.schema.KanjiReadingTableSchema
import ua.syt0r.kanji.core.kanji_data.data.JapaneseWord
import ua.syt0r.kanji.core.kanji_data.data.KanjiData
import ua.syt0r.kanji.core.kanji_data.data.RadicalData

interface KanjiDataRepository {

    suspend fun getStrokes(kanji: String): List<String>
    suspend fun getRadicalsInCharacter(character: String): List<CharacterRadical>

    suspend fun getMeanings(kanji: String): List<String>
    suspend fun getReadings(kanji: String): Map<String, KanjiReadingTableSchema.ReadingType>
    suspend fun getData(kanji: String): KanjiData?
    suspend fun getCharacterClassifications(kanji: String): List<CharactersClassification>

    suspend fun getKanjiByClassification(classification: CharactersClassification): List<String>
    suspend fun getWordsWithTextCount(text: String): Int
    suspend fun getWordsWithText(
        text: String,
        offset: Int = 0,
        limit: Int = Int.MAX_VALUE
    ): List<JapaneseWord>

    suspend fun getKanaWords(char: String, limit: Int = Int.MAX_VALUE): List<JapaneseWord>

    suspend fun getRadicals(): List<RadicalData>
    suspend fun getCharactersWithRadicals(radicals: List<String>): List<String>
    suspend fun getAllRadicalsInCharactersWithSelectedRadicals(radicals: Set<String>): List<String>

}