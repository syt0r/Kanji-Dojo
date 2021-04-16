package ua.syt0r.kanji_db_model.model

interface KanjiReadingData {
    val kanji: Char
    val onReadings: List<String>
    val kunReadings: List<String>
}