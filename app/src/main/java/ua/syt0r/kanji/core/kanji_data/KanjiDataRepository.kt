package ua.syt0r.kanji.core.kanji_data

import ua.syt0r.kanji.core.kanji_data.db.dao.KanjiDataDao
import ua.syt0r.kanji_dojo.shared.CharactersClassification
import ua.syt0r.kanji_dojo.shared.db.KanjiReadingTable
import javax.inject.Inject

class KanjiDataRepository @Inject constructor(
    private val kanjiDataDao: KanjiDataDao
) : KanjiDataContract.Repository {

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

}