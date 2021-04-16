package ua.syt0r.kanji.core.kanji_data_store.entity

import ua.syt0r.kanji_db_model.model.KanjiReadingData

data class KanjiData(
    val readings: KanjiReadingData,
    val meanings: List<String>
)