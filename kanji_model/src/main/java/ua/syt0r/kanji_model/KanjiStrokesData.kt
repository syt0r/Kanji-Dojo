package ua.syt0r.kanji_model

interface KanjiStrokesData {
    val kanji: Char
    val strokes: List<String>
}

interface KanjiReadingData {
    val kanji: Char
    val onReadings: List<String>
    val kunReadings: List<String>
}

interface KanjiCompleteData {
    val kanji: Char
    val strokes: List<String>
    val onReadings: List<String>
    val kunReadings: List<String>
}