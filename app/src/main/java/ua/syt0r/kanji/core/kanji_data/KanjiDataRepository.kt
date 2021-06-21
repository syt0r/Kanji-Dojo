package ua.syt0r.kanji.core.kanji_data

import ua.syt0r.kanji.core.kanji_data.db.dao.KanjiDataDao
import ua.syt0r.kanji.core.kanji_data.entity.KanjiClassificationGroup
import javax.inject.Inject

class KanjiDataRepository @Inject constructor(
    private val kanjiDataDao: KanjiDataDao
) : KanjiDataContract.Repository {

    override fun getKanjiClassifications(): List<KanjiClassificationGroup> {
        return kanjiDataDao.getClassificationGroups().map {
            KanjiClassificationGroup(
                className = it,
                classVariants = kanjiDataDao.getClassifications(it)
            )
        }
    }

    override fun getStrokes(kanji: String): List<String> {
        return kanjiDataDao.getStrokes(kanji)
    }

    override fun getMeanings(kanji: String): List<String> {
        return kanjiDataDao.getMeanings(kanji).map { it.meaning }
    }

}