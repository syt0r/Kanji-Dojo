package ua.syt0r.kanji.core.kanji_data

import ua.syt0r.kanji.common.CharactersClassification
import ua.syt0r.kanji.common.db.entity.CharacterRadical
import ua.syt0r.kanji.common.db.schema.KanjiReadingTableSchema
import ua.syt0r.kanji.core.kanji_data.data.*
import ua.syt0r.kanji.core.kanji_data.db.converters.CharacterRadicalConverter
import ua.syt0r.kanji.core.kanji_data.db.dao.KanjiDataDao
import javax.inject.Inject

class RoomKanjiDataRepository @Inject constructor(
    private val kanjiDataDao: KanjiDataDao
) : KanjiDataRepository {

    override fun getStrokes(kanji: String): List<String> {
        return kanjiDataDao.getStrokes(kanji)
    }

    override fun getRadicalsInCharacter(character: String): List<CharacterRadical> {
        return kanjiDataDao.getCharacterRadicals(character)
            .map { CharacterRadicalConverter.fromEntity(it) }
    }

    override fun getMeanings(kanji: String): List<String> {
        return kanjiDataDao.getMeanings(kanji).map { it.meaning }
    }

    override fun getReadings(kanji: String): Map<String, KanjiReadingTableSchema.ReadingType> {
        return kanjiDataDao.getReading(kanji).associate { it.reading to it.type }
    }

    override fun getData(kanji: String): KanjiData? {
        return kanjiDataDao.getData(kanji)?.run {
            KanjiData(
                kanji = kanji,
                frequency = frequency,
                grade = grade,
                jlpt = jlpt?.let { CharactersClassification.JLPT.valueOf(it) }
            )
        }
    }

    override fun getKanjiByJLPT(jlpt: CharactersClassification.JLPT): List<String> {
        return kanjiDataDao.getKanjiByJLPT(jlpt)
    }

    override fun getKanjiByGrade(grade: CharactersClassification.Grade): List<String> {
        val gradeIndex = (grade.ordinal + 1).let { if (it > 6) it + 1 else it } // No grade 7
        return kanjiDataDao.getKanjiByGrade(gradeIndex)
    }

    override fun getWordsWithCharacter(char: String): List<JapaneseWord> {
        return kanjiDataDao.getWordReadings(char).map { reading ->
            val furiganaCompounds = reading.furiganaDBField
                ?.furigana
                ?.map { FuriganaStringCompound(it.text, it.annotation) }
                ?: listOf(FuriganaStringCompound(reading.kanaExpression))
            JapaneseWord(
                furiganaString = FuriganaString(compounds = furiganaCompounds),
                meanings = kanjiDataDao.getWordMeanings(reading.dictionaryEntryId)
                    .map { it.meaning }
            )
        }
    }

    override fun getKanaWords(char: String): List<JapaneseWord> {
        return kanjiDataDao.getKanaWordReadings("%$char%").map {
            JapaneseWord(
                furiganaString = buildFuriganaString { append(it.kanaExpression) },
                meanings = kanjiDataDao.getWordMeanings(it.dictionaryEntryId).map { it.meaning }
            )
        }
    }

}