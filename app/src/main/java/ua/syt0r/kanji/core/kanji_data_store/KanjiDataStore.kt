package ua.syt0r.kanji.core.kanji_data_store

import ua.syt0r.kanji.core.kanji_data_store.db.dao.KanjiDatabaseDao
import ua.syt0r.kanji.core.kanji_data_store.entity.KanjiData
import ua.syt0r.kanji.core.kanji_data_store.entity.KanjiReading
import ua.syt0r.kanji_db_model.db.ReadingsTableConstants
import ua.syt0r.kanji_db_model.model.KanjiReadingData

class KanjiDataStore(
    private val dao: KanjiDatabaseDao
) : KanjiDataStoreContract.DataStore {

    override fun getKanjiList(): List<String> {
        return dao.getKanjiList()
    }

    override fun getStrokes(kanji: String): List<String> {
        return dao.getStrokes(kanji)
    }

    override fun getReadings(kanji: String): KanjiReadingData {
        return KanjiReading(
            kanji = kanji.first(),
            onReadings = dao.getReadings(kanji, ReadingsTableConstants.ReadingType.ON.value),
            kunReadings = dao.getReadings(kanji, ReadingsTableConstants.ReadingType.KUN.value)
        )
    }

    override fun getKanji(kanji: String): KanjiData {
        return KanjiData(
            readings = getReadings(kanji),
            meanings = dao.getMeanings(kanji)
        )
    }


}