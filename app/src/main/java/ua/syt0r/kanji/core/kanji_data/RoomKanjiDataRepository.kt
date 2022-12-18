package ua.syt0r.kanji.core.kanji_data

import ua.syt0r.kanji.core.kanji_data.data.FuriganaAnnotatedCharacter
import ua.syt0r.kanji.core.kanji_data.data.FuriganaString
import ua.syt0r.kanji.core.kanji_data.data.JapaneseWord
import ua.syt0r.kanji.core.kanji_data.data.KanjiData
import ua.syt0r.kanji.core.kanji_data.db.dao.KanjiDataDao
import ua.syt0r.kanji_dojo.shared.CharactersClassification
import ua.syt0r.kanji_dojo.shared.db.KanjiReadingTable
import javax.inject.Inject

class RoomKanjiDataRepository @Inject constructor(
    private val kanjiDataDao: KanjiDataDao
) : KanjiDataRepository {

    override fun getStrokes(kanji: String): List<String> {
        return kanjiDataDao.getStrokes(kanji)
    }

    override fun getMeanings(kanji: String): List<String> {
        return kanjiDataDao.getMeanings(kanji).map { it.meaning }
    }

    override fun getReadings(kanji: String): Map<String, KanjiReadingTable.ReadingType> {
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
        return kanjiDataDao.getWordsWithCharacter(char)
            .map { wordEntity ->
                JapaneseWord(
                    furiganaString = FuriganaString(
                        compounds = wordEntity.furiganaDBField
                            .furigana
                            .map { FuriganaAnnotatedCharacter(it.text, it.annotation) }
                    ),
                    meanings = kanjiDataDao.getWordMeanings(wordEntity.id!!)
                        .map { it.meaning }
                )
            }
    }

}