package ua.syt0r.kanji.presentation.screen.screen.writing_practice.data

data class ReviewKanjiData(
    val kanji: String,
    val onYomiReadings: List<String>,
    val kunYomiReading: List<String>,
    val meaningVariants: List<String>
)