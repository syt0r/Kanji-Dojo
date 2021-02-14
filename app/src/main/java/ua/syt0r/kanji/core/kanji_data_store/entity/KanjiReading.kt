package ua.syt0r.kanji.core.kanji_data_store.entity

import ua.syt0r.kanji_model.KanjiReadingData

data class KanjiReading(
    override val kanji: Char,
    override val onReadings: List<String>,
    override val kunReadings: List<String>
) : KanjiReadingData