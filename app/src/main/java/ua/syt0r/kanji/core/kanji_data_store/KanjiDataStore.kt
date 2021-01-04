package ua.syt0r.kanji.core.kanji_data_store

class KanjiDataStore(
    private val dao: KanjiDataStoreContract.DataStore
) : KanjiDataStoreContract.DataStore {

    override fun getKanjiList(): List<String> {
        return dao.getKanjiList()
    }

    override fun getStrokes(kanji: String): List<String> {
        return dao.getStrokes(kanji)
    }

}