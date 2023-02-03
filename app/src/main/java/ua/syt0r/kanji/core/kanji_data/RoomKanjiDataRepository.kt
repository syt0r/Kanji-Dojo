package ua.syt0r.kanji.core.kanji_data

import ua.syt0r.kanji.common.CharactersClassification
import ua.syt0r.kanji.common.db.entity.CharacterRadical
import ua.syt0r.kanji.common.db.schema.KanjiReadingTableSchema
import ua.syt0r.kanji.core.kanji_data.data.FuriganaString
import ua.syt0r.kanji.core.kanji_data.data.FuriganaStringCompound
import ua.syt0r.kanji.core.kanji_data.data.JapaneseWord
import ua.syt0r.kanji.core.kanji_data.data.KanjiData
import ua.syt0r.kanji.core.kanji_data.db.converters.CharacterRadicalConverter
import ua.syt0r.kanji.core.kanji_data.db.dao.KanjiDataDao
import ua.syt0r.kanji.core.kanji_data.db.entity.WordReadingEntity
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

    override fun getWordsWithText(text: String, limit: Int): List<JapaneseWord> {
        return kanjiDataDao.getRankedDicEntryWithText(text, limit).map { dicEntryId ->
            val readings = kanjiDataDao.getWordReadings(dicEntryId)
                .sortedWith(readingComparator(text))
                .map { entity -> entity.toReading() }
            JapaneseWord(
                readings = readings,
                meanings = kanjiDataDao.getWordMeanings(dicEntryId).map { it.meaning }
            )
        }
    }

    override fun getKanaWords(char: String, limit: Int): List<JapaneseWord> {
        return kanjiDataDao.getKanaWordReadings("%$char%", limit)
            .groupBy { it.dictionaryEntryId }
            .map { (id, entity) ->
                JapaneseWord(
                    readings = entity.sortedWith(readingComparator(char, true))
                        .map { it.toReading(kanaOnly = true) }
                        .distinct(),
                    meanings = kanjiDataDao.getWordMeanings(id).map { it.meaning }
                )
            }
    }


    // To make sure that searched reading is the first in the list
    private fun readingComparator(
        prioritizedText: String,
        kanaOnly: Boolean = false
    ): Comparator<WordReadingEntity> {
        return compareBy(
            {
                val containsText = it.expression
                    ?.takeIf { !kanaOnly }
                    ?.contains(prioritizedText)
                    ?: it.kanaExpression.contains(prioritizedText)
                !containsText
            },
            { it.rank }
        )
    }

    private fun WordReadingEntity.toReading(
        kanaOnly: Boolean = false
    ): FuriganaString {
        val compounds = furiganaDBField
            ?.furigana
            ?.takeIf { !kanaOnly }
            ?.map { FuriganaStringCompound(it.text, it.annotation) }
            ?: listOf(FuriganaStringCompound(kanaExpression))
        return FuriganaString(compounds)
    }

}