package ua.syt0r.kanji.core.kanji_data_store

import ua.syt0r.kanji.core.kanji_data_store.db.dao.KanjiDatabaseDao
import ua.syt0r.kanji.core.kanji_data_store.entity.KanjiReading
import ua.syt0r.kanji_model.KanjiReadingData
import ua.syt0r.kanji_model.db.ReadingsTableConstants

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


}