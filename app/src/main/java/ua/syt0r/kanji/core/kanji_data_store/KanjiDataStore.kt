package ua.syt0r.kanji.core.kanji_data_store

import ua.syt0r.kanji.core.kanji_data_store.db.dao.KanjiDataDao
import ua.syt0r.kanji.core.kanji_data_store.entity.KanjiClassificationGroup

class KanjiDataStore(
    private val kanjiDataDao: KanjiDataDao
) : KanjiDataStoreContract.DataStore {

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